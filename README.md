The actual code to run should be CacheSimulationGUI.java

Common Specification:
Number of cache blocks = 16 blocks
Cache line = 32 words
Read policy: Load-Through

Group Specification: Full Associative - First In First Out

Test Case 1 - Sequential Sequence:
Generated Sequence: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31]


Final State of Cache Memory:
Block 0: Contains Memory Block 16
Block 1: Contains Memory Block 17
Block 2: Contains Memory Block 18
Block 3: Contains Memory Block 19
Block 4: Contains Memory Block 20
Block 5: Contains Memory Block 21
Block 6: Contains Memory Block 22
Block 7: Contains Memory Block 23
Block 8: Contains Memory Block 24
Block 9: Contains Memory Block 25
Block 10: Contains Memory Block 26
Block 11: Contains Memory Block 27
Block 12: Contains Memory Block 28
Block 13: Contains Memory Block 29
Block 14: Contains Memory Block 30
Block 15: Contains Memory Block 31


Cache Statistics:
1. Memory Access Count: 128
2. Cache Hit Count: 0
3. Cache Miss Count: 128
4. Cache Hit Rate: 0.0%
5. Cache Miss Rate: 100.0%
6. Average Memory Access Time: 1200.0
7. Total Memory Access Time: 40960.0



Analysis:
The generated sequence follows the pattern {0, 1, ..., 2n-1} repeated four times, resulting in a total of 128 memory access counts. Despite having 128 memory accesses, there are no cache hits and 128 cache misses. This might initially seem counterintuitive, but it aligns with the FIFO (First-In-First-Out) replacement algorithm employed by the cache since the cache blocks are consistently replaced in a cyclical fashion. As the sequence fills the 16 cache blocks, each subsequent access evicts the oldest block (not to be confused with the least recently used block), leading to a perpetual cycle of cache misses and no hits.


Test Case 2 - Random Sequence:
Generated Block Sequence: [27, 1, 14, 6, 68, 88, 34, 2, 50, 1, 9, 25, 39, 30, 68, 26, 13, 67, 46, 1, 81, 79, 11, 42, 68, 30, 7, 15, 24, 47, 45, 83, 40, 62, 87, 57, 42, 19, 52, 74, 78, 67, 37, 40, 80, 11, 3, 9, 11, 43, 78, 60, 88, 79, 82, 7, 30, 42, 74, 24, 87, 0, 21, 86]

Final State of Cache Memory:
Block 0: Contains Memory Block 30
Block 1: Contains Memory Block 42
Block 2: Contains Memory Block 24
Block 3: Contains Memory Block 87
Block 4: Contains Memory Block 0
Block 5: Contains Memory Block 21
Block 6: Contains Memory Block 86
Block 7: Contains Memory Block 11
Block 8: Contains Memory Block 3
Block 9: Contains Memory Block 9
Block 10: Contains Memory Block 43
Block 11: Contains Memory Block 60
Block 12: Contains Memory Block 88
Block 13: Contains Memory Block 79
Block 14: Contains Memory Block 82
Block 15: Contains Memory Block 7


Cache Statistics:
1. Memory Access Count: 64
2. Cache Hit Count: 9
3. Cache Miss Count: 55
4. Cache Hit Rate: 14.0625%
5. Cache Miss Rate: 85.9375%
6. Average Memory Access Time: 1045.3125
7. Total Memory Access Time: 17888.0

Analysis:
In the random sequence test case, the Memory Access Count is intentionally limited to 64, due to the test case asking for 4n memory blocks. Notably, the random number generator is configured to produce values between 0 and 99, enhancing the likelihood of cache hits. This deliberate modification contrasts with the first test case, where no hits occurred. The Average Memory Access Time is reduced in this scenario, primarily attributed to the efficiency gained from cache hits. When data is already present in the cache, accessing it becomes quicker. The Total Memory Access Time, while smaller compared to the first test case, aligns with expectations given the nearly halved size of the access sequence.

Test Case 3 - Mid Repeat-Blocks:
Generated Block Sequence: {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 
 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31}

Final State of Cache Memory:
Block 0: Contains Memory Block 16
Block 1: Contains Memory Block 17
Block 2: Contains Memory Block 18
Block 3: Contains Memory Block 19
Block 4: Contains Memory Block 20
Block 5: Contains Memory Block 21
Block 6: Contains Memory Block 22
Block 7: Contains Memory Block 23
Block 8: Contains Memory Block 24
Block 9: Contains Memory Block 25
Block 10: Contains Memory Block 26
Block 11: Contains Memory Block 27
Block 12: Contains Memory Block 28
Block 13: Contains Memory Block 29
Block 14: Contains Memory Block 30
Block 15: Contains Memory Block 31


Cache Statistics:
1. Memory Access Count: 192
2. Cache Hit Count: 64
3. Cache Miss Count: 128
4. Cache Hit Rate: 33.33333333333333%
5. Cache Miss Rate: 66.66666666666666%
6. Average Memory Access Time: 833.3333333333333
7. Total Memory Access Time: 43008.0

Analysis:
The block sequence formula for the third test case, {0, 1, 2, ..., 15, 0, 1, 2, 3, ..., 31} repeated four times, has a very predictable pattern when analyzed through Fully Associative (FA) and First-In-First-Out (FIFO) caching strategies. Initially, the sequence fills the 16 empty cache blocks, resulting in 16 cache misses. The subsequent repeat, featuring values from 0 to 15 again, yields 16 cache hits due to the presence of these values in the cache. The sequence then progresses to values 16 to 31, triggering 16 cache misses as it replaces the existing data in the cache. This establishes a hit-to-miss ratio of 1:2. The subsequent repeats follow a similar pattern, leading to a total of 64 hits for every occurrence of 0 to 15 and 128 misses each time 0 to 15 or 16 to 31 replaces existing data. Despite having a larger Memory Access Count, the average access time is notably lower compared to other test cases, emphasizing the efficiency of hits. While the Total Memory Access Time is greater than in other cases, it remains proportionate to the increased access count, and notably smaller than the first test case due to a more favorable hit ratio.



