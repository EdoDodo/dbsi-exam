package org.candidate697229.benchmarking;

import org.candidate697229.algorithms.Naive;
import org.candidate697229.database.Database;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Benchmarker {
    private static final int NUM_OF_SCALES = 20;
    private static final int TIMEOUT_SECONDS = 900; // 15 minutes
    private static final int REPEATS_PER_SCALE = 4;

    public static void main(String[] args) {
        System.out.println("Creating Naive database to benchmark against");
        for (int i = 1; i <= NUM_OF_SCALES; ++i) {
            if (!new File("housing/housing-" + i + ".db").exists()) {
                Naive.makeSQLiteDatabase(Database.makeFromDirectory("housing/housing-" + i), "housing/housing-" + i + ".db");
                System.out.println("... Created Naive database for database number " + i);
            } else System.out.println("... Naive database for database number " + i + " already exists, skipping creation");
        }

        List<QueryRunner> queryRunners = Arrays.asList(new NaiveRunner(), new AggOneRunner(), new AggTwoRunner());

        experiment:
        for (QueryRunner runner : queryRunners) {
            long experimentStart = System.currentTimeMillis();
            for (int i = 1; i <= NUM_OF_SCALES; ++i) {
                GCAndWait();
                runner.runQueryAll(i);
                GCAndWait();
                runner.runQueryOne(i);
                long[] allAggregatesTimes = new long[REPEATS_PER_SCALE];
                long[] oneAggregateTimes = new long[REPEATS_PER_SCALE];
                for (int j = 0; j < REPEATS_PER_SCALE; ++j) {
                    allAggregatesTimes[j] = runner.runQueryAll(i);
                    GCAndWait();
                    oneAggregateTimes[j] = runner.runQueryOne(i);
                    GCAndWait();
                    if ((System.currentTimeMillis() - experimentStart) > (TIMEOUT_SECONDS * 1000L))
                        continue experiment;
                }
                System.out.println("TIME\t" + runner.getClass().getSimpleName() + "\t" + i + "\tAll\t" + average(allAggregatesTimes));
                System.out.println("TIME\t" + runner.getClass().getSimpleName() + "\t" + i + "\tOne\t" + average(oneAggregateTimes));
            }
        }
    }

    private static long average(long[] times) {
        long sum = 0;
        for (long time : times)
            sum += time;
        return Math.floorDiv(sum, times.length);
    }

    /**
     * This method triggers garbage collection and finalisation then waits for a short time for these to complete. This
     * is executed between runs to attempt to ensure each run starts with the heap clear of any garbage, giving fairer timings.
     *
     * It is worth noting that these calls do not actually guarantee that garbage collection will occur, since the JVM
     * can still decide to not perform it. However, it is good practice to execute these calls anyway in profiling code.
     */
    private static void GCAndWait() {
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new InternalError(e);
        }
    }
}
