import javax.swing.*;
import java.awt.*;
import java.util.*;

class CacheBlock {
    boolean valid;
    int tag;

    CacheBlock() {
        this.valid = false;
        this.tag = -1;
    }
}

public class CacheSimulation {
    private int cacheSize; // Number of cache blocks

    private List<CacheBlock> cache;
    private Queue<Integer> fifoQueue;

    private int memoryAccessCount;
    private int cacheHitCount;
    private int cacheMissCount;

    public CacheSimulation(int cacheSize) {
        this.cacheSize = cacheSize;

        this.cache = new ArrayList<>();
        this.fifoQueue = new LinkedList<>();

        for (int i = 0; i < cacheSize; i++) {
            cache.add(new CacheBlock());
        }

        this.memoryAccessCount = 0;
        this.cacheHitCount = 0;
        this.cacheMissCount = 0;
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
                loadIntoCache(memoryBlock, cacheIndex);
                System.out.println("Miss: Memory Block " + memoryBlock + " in Cache Block " + cacheIndex);
            } else {
                // Cache is full, perform FIFO replacement
                cacheIndex = fifoQueue.poll();
                fifoQueue.add(cacheIndex);
                loadIntoCache(memoryBlock, cacheIndex);
                System.out.println("Miss: Memory Block " + memoryBlock + " replaces what was in Cache Block " + cacheIndex);
            }
        } else {
            // Cache hit, print Hit message
            System.out.println("Hit: Memory Block " + memoryBlock + " is stored in Cache Block " + cacheIndex);
        }
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

        System.out.println("Cache Statistics:");
        System.out.println("1. Memory Access Count: " + memoryAccessCount);
        System.out.println("2. Cache Hit Count: " + cacheHitCount);
        System.out.println("3. Cache Miss Count: " + cacheMissCount);
        System.out.println("4. Cache Hit Rate: " + cacheHitRate + "%");
        System.out.println("5. Cache Miss Rate: " + cacheMissRate + "%");
        System.out.println("6. Average Memory Access Time: " + averageMemoryAccessTime);
        System.out.println("7. Total Memory Access Time: " + totalMemoryAccessTime);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Cache Simulation!");

        while (true) {
            // Menu
            System.out.println("Choose an option:");
            System.out.println("1. Enter a custom sequence");
            System.out.println("2. Use sequential sequence test case");
            System.out.println("3. Use random sequence test case");
            System.out.println("4. Use mid-repeat blocks test case");
            System.out.println("5. Exit");

            int option = scanner.nextInt();

            if (option == 5) {
                System.out.println("Exiting program.");
                break;
            }

            int[] memoryBlocks;
            switch (option) {
                case 1:
                    // User input for the sequence of memory blocks
                    System.out.print("Enter the sequence of memory blocks (comma-separated): ");
                    String[] blocksInput = scanner.next().split(",");
                    memoryBlocks = Arrays.stream(blocksInput).mapToInt(Integer::parseInt).toArray();
                    break;
                case 2:
                    // Sequential sequence test case
                    memoryBlocks = generateSequentialSequence(16, 4);
                    break;
                case 3:
                    // Random sequence test case
                    memoryBlocks = generateRandomSequence(16);
                    break;
                case 4:
                    // Mid-repeat blocks test case
                    memoryBlocks = generateMidRepeatBlocks(16, 4);
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid option.");
                    continue;
            }

            // Display generated block sequence
            System.out.println("Generated Block Sequence:");
            System.out.println(Arrays.toString(memoryBlocks));
            System.out.println();

            // Cache simulation setup
            CacheSimulation cacheSimulation = new CacheSimulation(16);

            // Simulate memory accesses
            for (int block : memoryBlocks) {
                cacheSimulation.accessMemory(block);
                cacheSimulation.printCacheSnapshot();
            }

            // Display cache statistics
            cacheSimulation.printCacheStatistics();
        }

        scanner.close();
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
    
    
    
}
