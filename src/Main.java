
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        System.out.println(" INITIALIZING CPU SIMULATION PROJECT...");
        System.out.println("Project: CpuSimulation");
       // System.out.println("Package: org.example");
        System.out.println("==========================================");

        CPU cpu = new CPU();
        cpu.runSimulation();
    }
}

class CPU {
    private Cache cache;
    private MainMemory mainMemory;
    private Scanner scanner;
    private Random random;

    // CPU Registers
    private int registerA;
    private int registerB;
    private int result;

    public CPU() {
        System.out.println("  CPU: Initializing Central Processing Unit...");
        this.cache = new Cache();
        this.mainMemory = new MainMemory();
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.registerA = 0;
        this.registerB = 0;
        this.result = 0;
        System.out.println(" CPU: Successfully initialized with registers A, B and Result");
    }

    public void runSimulation() {
        System.out.println("\n🎬 CPU: Starting simulation cycle...");

        boolean running = true;
        int operationCount = 0;

        while (running && operationCount < 20) { // Limit operations for demo
            System.out.println("\n--- OPERATION " + (operationCount + 1) + " ---");

            // Load random data into registers
            loadRandomData();

            // Perform random operation
            performRandomOperation();

            // Print results
            printResults();

            // Check if user wants to continue
            if (operationCount >= 5) { // Ask after a few operations
                System.out.print("\nContinue simulation? (y/n): ");
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("n") || input.equals("no")) {
                    running = false;
                    System.out.println(" CPU: Simulation stopped by user");
                }
            }

            operationCount++;
        }

        // Display final statistics
        displayStatistics();
        scanner.close();
        System.out.println("\n CPU: Simulation completed successfully!");
    }

    private void loadRandomData() {
        System.out.println("\nCPU: Loading data into registers...");

        // Load into register A
        System.out.println(" CPU: Accessing data for Register A...");
        registerA = cache.getData("A");
        System.out.println(" CPU: Register A loaded with value: " + registerA);

        // Load into register B
        System.out.println(" CPU: Accessing data for Register B...");
        registerB = cache.getData("B");
        System.out.println(" CPU: Register B loaded with value: " + registerB);
    }

    private void performRandomOperation() {
        String[] operations = {"ADD", "SUBTRACT", "MULTIPLY", "DIVIDE"};
        String operation = operations[random.nextInt(operations.length)];

        System.out.println("\n⚡ CPU: Executing operation: " + operation);

        switch (operation) {
            case "ADD":
                result = registerA + registerB;
                System.out.println("➕ CPU: " + registerA + " + " + registerB + " = " + result);
                break;
            case "SUBTRACT":
                result = registerA - registerB;
                System.out.println("➖ CPU: " + registerA + " - " + registerB + " = " + result);
                break;
            case "MULTIPLY":
                result = registerA * registerB;
                System.out.println("✖️ CPU: " + registerA + " * " + registerB + " = " + result);
                break;
            case "DIVIDE":
                if (registerB != 0) {
                    result = registerA / registerB;
                    System.out.println("➗ CPU: " + registerA + " / " + registerB + " = " + result);
                } else {
                    System.out.println(" CPU: Division by zero prevented!");
                    result = 0;
                }
                break;
        }
    }

    private void printResults() {
        System.out.println("\n CPU: Printing operation results...");
        System.out.println("Register A: " + registerA);
        System.out.println("Register B: " + registerB);
        System.out.println("Operation Result: " + result);

        // Store result back to cache
        System.out.println(" CPU: Storing result back to cache...");
        cache.storeData("RESULT_" + System.currentTimeMillis(), result);
    }

    private void displayStatistics() {
        System.out.println("\n SIMULATION STATISTICS:");
        System.out.println("Cache Hit Rate: " + cache.getHitRate() + "%");
        System.out.println("Cache Miss Rate: " + cache.getMissRate() + "%");
        System.out.println("Total Memory Accesses: " + cache.getTotalAccesses());
        System.out.println("Main Memory Size: " + mainMemory.getSize() + " entries");
        System.out.println("Cache Size: " + cache.getSize() + " blocks");
    }
}

class Cache {
    private Map<String, LinkedList<Integer>> cacheMap;
    private MainMemory mainMemory;
    private int hitCount;
    private int missCount;
    private int totalAccesses;
    private static final int MAX_CACHE_SIZE = 5;

    public Cache() {
        System.out.println(" CACHE: Initializing Cache Memory...");
        this.cacheMap = new LinkedHashMap<>();
        this.mainMemory = new MainMemory();
        this.hitCount = 0;
        this.missCount = 0;
        this.totalAccesses = 0;
        System.out.println(" CACHE: Successfully initialized with " + MAX_CACHE_SIZE + " block capacity");
    }

    public int getData(String key) {
        totalAccesses++;
        System.out.println(" CACHE: Looking for key '" + key + "' in cache...");

        if (cacheMap.containsKey(key)) {
            hitCount++;
            System.out.println(" CACHE HIT: Key '" + key + "' found in cache!");
            LinkedList<Integer> block = cacheMap.get(key);
            int value = block.getFirst();

            // Move to end to mark as recently used (LRU simulation)
            cacheMap.remove(key);
            cacheMap.put(key, block);

            return value;
        } else {
            missCount++;
            System.out.println(" CACHE MISS: Key '" + key + "' not found in cache!");

            // Fetch from main memory
            System.out.println(" CACHE: Fetching data from main memory...");
            int value = mainMemory.getData(key);

            // Store in cache (with eviction if needed)
            storeData(key, value);

            return value;
        }
    }

    public void storeData(String key, int value) {
        System.out.println(" CACHE: Storing key '" + key + "' with value " + value);

        // Check if cache is full and evict if necessary
        if (cacheMap.size() >= MAX_CACHE_SIZE) {
            evictLRU();
        }

        LinkedList<Integer> newBlock = new LinkedList<>();
        newBlock.add(value);
        cacheMap.put(key, newBlock);

        System.out.println(" CACHE: Successfully stored. Current cache size: " + cacheMap.size() + "/" + MAX_CACHE_SIZE);
    }

    private void evictLRU() {
        // Remove the first entry (oldest in LinkedHashMap)
        String firstKey = cacheMap.keySet().iterator().next();
        System.out.println(" CACHE: Evicting LRU block with key '" + firstKey + "'");
        cacheMap.remove(firstKey);
    }

    public double getHitRate() {
        return totalAccesses > 0 ? (double) hitCount / totalAccesses * 100 : 0;
    }

    public double getMissRate() {
        return totalAccesses > 0 ? (double) missCount / totalAccesses * 100 : 0;
    }

    public int getTotalAccesses() {
        return totalAccesses;
    }

    public int getSize() {
        return cacheMap.size();
    }
}

class MainMemory {
    private Map<String, LinkedList<Integer>> memoryMap;
    private Random random;

    public MainMemory() {
        System.out.println(" MAIN MEMORY: Initializing Main Memory...");
        this.memoryMap = new HashMap<>();
        this.random = new Random();
        initializeMemory();
        System.out.println(" MAIN MEMORY: Successfully initialized with pre-loaded data");
    }

    private void initializeMemory() {
        System.out.println(" MAIN MEMORY: Pre-loading data into memory...");

        // Pre-load some initial data
        String[] initialKeys = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (String key : initialKeys) {
            LinkedList<Integer> block = new LinkedList<>();
            int randomValue = random.nextInt(100) + 1; // Values between 1-100
            block.add(randomValue);
            memoryMap.put(key, block);
            System.out.println("   Loaded key '" + key + "' with value: " + randomValue);
        }

        System.out.println(" MAIN MEMORY: Total " + initialKeys.length + " data blocks initialized");
    }

    public int getData(String key) {
        System.out.println(" MAIN MEMORY: Accessing data for key '" + key + "'");

        if (!memoryMap.containsKey(key)) {
            System.out.println(" MAIN MEMORY: Key '" + key + "' not found, generating new data...");
            LinkedList<Integer> newBlock = new LinkedList<>();
            int randomValue = random.nextInt(100) + 1;
            newBlock.add(randomValue);
            memoryMap.put(key, newBlock);
        }

        int value = memoryMap.get(key).getFirst();
        System.out.println(" MAIN MEMORY: Retrieved value " + value + " for key '" + key + "'");
        return value;
    }

    public int getSize() {
        return memoryMap.size();
    }
}