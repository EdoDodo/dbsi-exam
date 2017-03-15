package org.candidate697229.benchmarking;

import org.candidate697229.algorithms.Naive;
import org.candidate697229.database.Database;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int NUM_OF_SCALES = 20;
    private static final int TIMEOUT_SECONDS = 240;
    private static final int REPEATS_PER_SCALE = 4;

    public static void main(String[] args) {
        System.out.println("Reading in data and creating Naive database to benchmark against");
        ArrayList<Database> databases = new ArrayList<>();
        for (int i = 0; i <= NUM_OF_SCALES; ++i) {
            databases.add(Database.makeFromDirectory("housing/housing-" + i));
            System.out.println("... Read in database number " + i);
            if (!new File("housing/housing-" + i + ".db").exists()) {
                Naive.makeSQLiteDatabase(databases.get(i), "housing/housing-" + i + ".db");
                System.out.println("... Created Naive database for database number " + i);
            } else System.out.println("... Naive database for database number " + i + " already exists, skipping creation");
        }

        List<QueryRunner> queryRunners = Arrays.asList(new NaiveRunner(), new AggOneRunner(databases));

        experiment:
        for (QueryRunner runner : queryRunners) {
            long experimentStart = System.currentTimeMillis();
            for (int i = 0; i < NUM_OF_SCALES; ++i) {
                runner.runQueryAll(i);
                runner.runQueryOne(i);
                GCAndWait();
                long[] allAggregatesTimes = new long[REPEATS_PER_SCALE];
                long[] oneAggregateTimes = new long[REPEATS_PER_SCALE];
                for (int j = 0; j < REPEATS_PER_SCALE; ++j) {
                    allAggregatesTimes[j] = runner.runQueryAll(i);
                    GCAndWait();
                    oneAggregateTimes[j] = runner.runQueryOne(i);
                    GCAndWait();
                    if ((System.currentTimeMillis() - experimentStart) > (TIMEOUT_SECONDS * 1000L))
                        break experiment;
                }
                System.out.println("TIME\t" + queryRunners.getClass().getName() + "\tAll\t" + average(allAggregatesTimes));
                System.out.println("TIME\t" + queryRunners.getClass().getName() + "\tOne\t" + average(oneAggregateTimes));
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
     * is executed between runs to ensure each run starts with the heap clear of any garbage, giving fairer timings.
     */
    private static void GCAndWait() {
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new InternalError(e);
        }
    }
}
