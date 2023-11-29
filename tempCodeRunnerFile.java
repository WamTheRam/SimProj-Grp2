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
    
        // Check if the data is in the cache
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
    
            // Check if there is space in the cache
            if (fifoQueue.size() < cacheSize) {
                cacheIndex = fifoQueue.size();
                fifoQueue.add(cacheIndex);
            } else {
                // Cache is full, perform FIFO replacement
                cacheIndex = fifoQueue.poll();
                fifoQueue.add(cacheIndex);
            }
    
            loadIntoCache(memoryBlock, cacheIndex);
            System.out.println("Miss: Memory Block " + memoryBlock + " in Cache Block " + cacheIndex);
        } else {
            // Cache hit, print Hit message
            System.out.println("Hit: Memory Block " + memoryBlock + " is stored in Cache Block " + cacheIndex);
        }
    
        // Increment the index for the next memory access
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
        double cacheHitRate = (double) cacheHitCount / memoryAccessCount * 100;
        double cacheMissRate = (double) cacheMissCount / memoryAccessCount * 100;
        double averageMemoryAccessTime = (double) memoryAccessCount / (cacheHitCount + cacheMissCount);
        double totalMemoryAccessTime = (cacheHitCount + 2 * cacheMissCount); // Assuming cache hit time is 1 unit, and cache miss penalty is 2 units

        System.out.println("\n\nCache Statistics:");
        System.out.println("1. Memory Access Count: " + memoryAccessCount);
        System.out.println("2. Cache Hit Count: " + cacheHitCount);
        System.out.println("3. Cache Miss Count: " + cacheMissCount);
        System.out.println("4. Cache Hit Rate: " + cacheHitRate + "%");
        System.out.println("5. Cache Miss Rate: " + cacheMissRate + "%");
        System.out.println("6. Average Memory Access Time: " + averageMemoryAccessTime);
        System.out.println("7. Total Memory Access Time: " + totalMemoryAccessTime);
    }

    // New method to display the output in a GUI
    public void displayOutputInGUI() {
        JFrame frame = new JFrame("Cache Simulation Output");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        nextButton = new JButton("Next");
        skipButton = new JButton("Skip to Final State");

        // Configure button actions
        nextButton.addActionListener(e -> {
            if (currentMemoryAccessIndex < memoryBlocks.length) {
                accessMemory(memoryBlocks[currentMemoryAccessIndex]);
                printCacheSnapshot();
                
                // Check if it's the final step
                if (currentMemoryAccessIndex == memoryBlocks.length) {
                    // Display the final state and summary
                    printFinalStateAndSummary();
                }
            }
        });
        

        skipButton.addActionListener(e -> {
        
            // Skip to the final state
            while (currentMemoryAccessIndex < memoryBlocks.length) {
                accessMemory(memoryBlocks[currentMemoryAccessIndex]);
                printCacheSnapshot();
            }
        
            // Print the final state and summary
            printFinalStateAndSummary();
        });
        
        
        
        
        

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nextButton);
        buttonPanel.add(skipButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Redirect System.out to the JTextArea
        PrintStream printStream = new PrintStream(new CustomOutputStream(outputTextArea));
        System.setOut(printStream);

        // Display the generated block sequence
        outputTextArea.append("Generated Block Sequence:\n{");
        for (int i = 0; i < memoryBlocks.length; i++) {
            outputTextArea.append(memoryBlocks[i] + ", ");
            // Add a newline and open curly brace after every 10 numbers
            if ((i + 1) % 10 == 0) {
                outputTextArea.append("\n");
                if (i < memoryBlocks.length - 1) {
                    outputTextArea.append(" ");
                }
            }
        }

        // Remove trailing comma and space
        outputTextArea.replaceRange("", outputTextArea.getText().length() - 2, outputTextArea.getText().length());
        outputTextArea.append("\n}\n\n");

        frame.setVisible(true);
    }

    private void printFinalStateAndSummary() {
        System.out.println("\nFinal State of Cache Memory:");
        for (int i = 0; i < cacheSize; i++) {
            CacheBlock block = cache.get(i);
            System.out.println("Block " + i + ": " + (block.valid ? "Contains Memory Block " + block.tag : "Empty"));
        }

        // Display cache statistics
        printCacheStatistics();
    }

    public static void main(String[] args) {
        // Run GUI in the event dispatch thread to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            JFrame welcomeFrame = new JFrame("Welcome");
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setSize(300, 200);

            JPanel welcomePanel = new JPanel(new GridLayout(2, 2));
            welcomeFrame.add(welcomePanel);

            // Create buttons
            JButton customInputButton = new JButton("Enter Custom Sequence");
            JButton sequentialButton = new JButton("Use Sequential Sequence");
            JButton randomButton = new JButton("Use Random Sequence");
            JButton midRepeatButton = new JButton("Use Mid-Repeat Blocks");

            // Add buttons to the panel
            welcomePanel.add(customInputButton);
            welcomePanel.add(sequentialButton);
            welcomePanel.add(randomButton);
            welcomePanel.add(midRepeatButton);

            // Configure button actions
            customInputButton.addActionListener(e -> handleButtonClick(1));
            sequentialButton.addActionListener(e -> handleButtonClick(2));
            randomButton.addActionListener(e -> handleButtonClick(3));
            midRepeatButton.addActionListener(e -> handleButtonClick(4));

            // Make the frame visible
            welcomeFrame.setVisible(true);
        });
    }

    private static void handleButtonClick(int option) {
        CacheSimulationGUI cacheSimulationGUI;

        switch (option) {
            case 1:
                // User input for the sequence of memory blocks
                String input = JOptionPane.showInputDialog("Enter the sequence of memory blocks (comma-separated): ");
                if (input != null) {
                    String[] blocksInput = input.split(",");
                    cacheSimulationGUI = new CacheSimulationGUI(16, Arrays.stream(blocksInput).mapToInt(Integer::parseInt).toArray());
                } else {
                    return; // User canceled input
                }
                break;
            case 2:
                // Sequential sequence test case
                cacheSimulationGUI = new CacheSimulationGUI(16, generateSequentialSequence(16, 4));
                break;
            case 3:
                // Random sequence test case
                cacheSimulationGUI = new CacheSimulationGUI(16, generateRandomSequence(16));
                break;
            case 4:
                // Mid-repeat blocks test case
                cacheSimulationGUI = new CacheSimulationGUI(16, generateMidRepeatBlocks(16, 4));
                break;
            default:
                return; // Invalid option
        }

        // Display generated block sequence
        System.out.println("Generated Block Sequence:");
        System.out.println(Arrays.toString(cacheSimulationGUI.memoryBlocks));
        System.out.println();

        // Cache simulation setup and display in GUI
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
            sequence[i] = random.nextInt(100); // Generates random integers from 0 to 99
        }
        return sequence;
    }

    private static int[] generateMidRepeatBlocks(int n, int repeatCount) {
        int[] sequence = new int[(repeatCount * n) + (repeatCount * 2 * n)];
        int index = 0;

        // Repeat the inner sequence four times
        for (int r = 0; r < repeatCount; r++) {
            // Iterate from 0 to n-1
            for (int i = 0; i < n; i++) {
                sequence[index++] = i;
            }

            // Iterate from 0 to 2n
            for (int j = 0; j < 2 * n; j++) {
                sequence[index++] = j;
            }
        }

        return sequence;
    }

    // CustomOutputStream class to redirect System.out to a JTextArea
    static class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            // Redirect the byte to the JTextArea
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}
