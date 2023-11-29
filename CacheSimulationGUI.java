import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Arrays;

class CacheBlock {
    boolean valid;
    int tag;

    CacheBlock() {
        this.valid = false;
        this.tag = -1;
    }
}

public class CacheSimulationGUI {
    private int cacheSize; // Number of cache blocks

    private List<CacheBlock> cache;
    private Queue<Integer> fifoQueue;

    private int memoryAccessCount;
    private int cacheHitCount;
    private int cacheMissCount;

    private int[] memoryBlocks; // Used for simulation

    private JTextArea outputTextArea;
    private JButton nextButton;
    private JButton skipButton;

    private int currentMemoryAccessIndex;

    private JFrame mainFrame;
    private JFrame resultFrame;

    public CacheSimulationGUI(int cacheSize, int[] memoryBlocks) {
        this.cacheSize = cacheSize;

        this.cache = new ArrayList<>();
        this.fifoQueue = new LinkedList<>();

        for (int i = 0; i < cacheSize; i++) {
            cache.add(new CacheBlock());
        }

        this.memoryAccessCount = 0;
        this.cacheHitCount = 0;
        this.cacheMissCount = 0;

        this.memoryBlocks = memoryBlocks;

        this.currentMemoryAccessIndex = 0;
    }

    public void accessMemory(int memoryBlock) {
        memoryAccessCount++;

        boolean cacheHit = false;
        int cacheIndex = -1;

        for (int i = 0; i < cacheSize; i++) {
            CacheBlock block = cache.get(i);
            if (block.valid && block.tag == memoryBlock) {
                cacheHit = true;
                cacheIndex = i;
                cacheHitCount++;
                break;
            }
        }

        if (!cacheHit) {
            cacheMissCount++;

            if (fifoQueue.size() < cacheSize) {
                cacheIndex = fifoQueue.size();
                fifoQueue.add(cacheIndex);
            } else {
                cacheIndex = fifoQueue.poll();
                fifoQueue.add(cacheIndex);
            }

            loadIntoCache(memoryBlock, cacheIndex);
            System.out.println("Miss: Memory Block " + memoryBlock + " in Cache Block " + cacheIndex);
        } else {
            System.out.println("Hit: Memory Block " + memoryBlock + " is stored in Cache Block " + cacheIndex);
        }

        currentMemoryAccessIndex++;
    }

    private void loadIntoCache(int memoryBlock, int cacheIndex) {
        CacheBlock block = cache.get(cacheIndex);
        block.valid = true;
        block.tag = memoryBlock;
    }

    public void printCacheSnapshot() {
        System.out.println("Cache Memory Snapshot:");
        for (int i = 0; i < cacheSize; i++) {
            CacheBlock block = cache.get(i);
            System.out.println("Block " + i + ": " + (block.valid ? "Contains Memory Block " + block.tag : "Empty"));
        }
        System.out.println();
    }

    public void printCacheStatistics() {
        //Common Specification
        double numOfCacheBlocks = 16;
        double numOfWordsPerBlock = 32;

        //assume cache access time is 1unit and memory access is 10unit
        double assumedCacheAccessTime = 1;
        double assumedMemoryAccessTime = 10;

        //loadthrough
        double missPenalty = assumedCacheAccessTime + assumedMemoryAccessTime + assumedCacheAccessTime;

        //cache miss and hit rate
        double cacheHitRate = (double) cacheHitCount / memoryAccessCount * 100;
        double cacheMissRate = (double) cacheMissCount / memoryAccessCount * 100;

        //AccessTime Calculation
        double averageMemoryAccessTime = (cacheHitRate * assumedCacheAccessTime) + (cacheMissRate * missPenalty);
        double totalMemoryAccessTime = (cacheHitCount * numOfWordsPerBlock * assumedCacheAccessTime) + 
                                        (cacheMissCount * numOfWordsPerBlock* assumedMemoryAccessTime);

        System.out.println("\n\nCache Statistics:");
        System.out.println("1. Memory Access Count: " + memoryAccessCount);
        System.out.println("2. Cache Hit Count: " + cacheHitCount);
        System.out.println("3. Cache Miss Count: " + cacheMissCount);
        System.out.println("4. Cache Hit Rate: " + cacheHitRate + "%");
        System.out.println("5. Cache Miss Rate: " + cacheMissRate + "%");
        System.out.println("6. Average Memory Access Time: " + averageMemoryAccessTime);
        System.out.println("7. Total Memory Access Time: " + totalMemoryAccessTime);
    }

    public void displayOutputInGUI() {
        mainFrame = new JFrame("Cache Simulation Output");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 500);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2));
        mainFrame.add(mainPanel);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        nextButton = new JButton("Next");
        skipButton = new JButton("Skip to Final State");

        nextButton.addActionListener(e -> {
            if (currentMemoryAccessIndex < memoryBlocks.length) {
                accessMemory(memoryBlocks[currentMemoryAccessIndex]);
                printCacheSnapshot();

                if (currentMemoryAccessIndex == memoryBlocks.length) {
                    showResultFrame();
                }
            }
        });

        skipButton.addActionListener(e -> {
            while (currentMemoryAccessIndex < memoryBlocks.length) {
                accessMemory(memoryBlocks[currentMemoryAccessIndex]);
                printCacheSnapshot();
            }

            showResultFrame();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nextButton);
        buttonPanel.add(skipButton);

        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(buttonPanel, BorderLayout.SOUTH);

        PrintStream printStream = new PrintStream(new CustomOutputStream(outputTextArea));
        System.setOut(printStream);

        outputTextArea.append("Generated Block Sequence:\n{");
        for (int i = 0; i < memoryBlocks.length; i++) {
            outputTextArea.append(memoryBlocks[i] + ", ");
            if ((i + 1) % 10 == 0) {
                outputTextArea.append("\n");
                if (i < memoryBlocks.length - 1) {
                    outputTextArea.append(" ");
                }
            }
        }

        outputTextArea.replaceRange("", outputTextArea.getText().length() - 2, outputTextArea.getText().length());
        outputTextArea.append("\n}\n\n");

        mainFrame.setVisible(true);
    }

    private void showResultFrame() {
        resultFrame = new JFrame("Cache Simulation Result");
        resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        resultFrame.setSize(500, 500);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        JButton tryNewSequenceButton = new JButton("Try a New Memory Block Sequence");

        tryNewSequenceButton.addActionListener(e -> {
            resultFrame.dispose();
            mainFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(tryNewSequenceButton);

        resultFrame.add(scrollPane, BorderLayout.CENTER);
        resultFrame.add(buttonPanel, BorderLayout.SOUTH);

        PrintStream printStream = new PrintStream(new CustomOutputStream(outputTextArea));
        System.setOut(printStream);

        outputTextArea.append("Generated Block Sequence:\n{");
        for (int i = 0; i < memoryBlocks.length; i++) {
            outputTextArea.append(memoryBlocks[i] + ", ");
            if ((i + 1) % 10 == 0) {
                outputTextArea.append("\n");
                if (i < memoryBlocks.length - 1) {
                    outputTextArea.append(" ");
                }
            }
        }

        outputTextArea.replaceRange("", outputTextArea.getText().length() - 2, outputTextArea.getText().length());
        outputTextArea.append("\n}\n\n");

        printFinalStateAndSummary();

        resultFrame.setVisible(true);
    }

    private void printFinalStateAndSummary() {
        System.out.println("\nFinal State of Cache Memory:");
        for (int i = 0; i < cacheSize; i++) {
            CacheBlock block = cache.get(i);
            System.out.println("Block " + i + ": " + (block.valid ? "Contains Memory Block " + block.tag : "Empty"));
        }

        printCacheStatistics();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame welcomeFrame = new JFrame("Welcome");
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setSize(300, 200);

            JPanel welcomePanel = new JPanel(new GridLayout(2, 2));
            welcomeFrame.add(welcomePanel);

            JButton customInputButton = new JButton("Enter Custom Sequence");
            JButton sequentialButton = new JButton("Use Sequential Sequence");
            JButton randomButton = new JButton("Use Random Sequence");
            JButton midRepeatButton = new JButton("Use Mid-Repeat Blocks");

            welcomePanel.add(customInputButton);
            welcomePanel.add(sequentialButton);
            welcomePanel.add(randomButton);
            welcomePanel.add(midRepeatButton);

            customInputButton.addActionListener(e -> handleButtonClick(1));
            sequentialButton.addActionListener(e -> handleButtonClick(2));
            randomButton.addActionListener(e -> handleButtonClick(3));
            midRepeatButton.addActionListener(e -> handleButtonClick(4));

            welcomeFrame.setVisible(true);
        });
    }

    private static void handleButtonClick(int option) {
        CacheSimulationGUI cacheSimulationGUI;

        switch (option) {
            case 1:
                String input = JOptionPane.showInputDialog("Enter the sequence of memory blocks (comma-separated): ");
                if (input != null) {
                    String[] blocksInput = input.split(",");
                    cacheSimulationGUI = new CacheSimulationGUI(16, Arrays.stream(blocksInput).mapToInt(Integer::parseInt).toArray());
                } else {
                    return;
                }
                break;
            case 2:
                cacheSimulationGUI = new CacheSimulationGUI(16, generateSequentialSequence(16, 4));
                break;
            case 3:
                cacheSimulationGUI = new CacheSimulationGUI(16, generateRandomSequence(16));
                break;
            case 4:
                cacheSimulationGUI = new CacheSimulationGUI(16, generateMidRepeatBlocks(16, 4));
                break;
            default:
                return;
        }

        System.out.println("Generated Block Sequence:");
        System.out.println(Arrays.toString(cacheSimulationGUI.memoryBlocks));
        System.out.println();

        cacheSimulationGUI.displayOutputInGUI();
    }

    private static int[] generateSequentialSequence(int n, int repeatCount) {
        int[] sequence = new int[n * repeatCount];
        for (int i = 0; i < repeatCount; i++) {
            for (int j = 0; j < n; j++) {
                sequence[i * n + j] = j;
            }
        }
        return sequence;
    }

    private static int[] generateRandomSequence(int n) {
        Random random = new Random();
        int[] sequence = new int[4 * n];
        for (int i = 0; i < 4 * n; i++) {
            sequence[i] = random.nextInt(100);
        }
        return sequence;
    }

    private static int[] generateMidRepeatBlocks(int n, int repeatCount) {
        int[] sequence = new int[(repeatCount * n) + (repeatCount * 2 * n)];
        int index = 0;

        for (int r = 0; r < repeatCount; r++) {
            for (int i = 0; i < n; i++) {
                sequence[index++] = i;
            }

            for (int j = 0; j < 2 * n; j++) {
                sequence[index++] = j;
            }
        }

        return sequence;
    }

    static class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}
