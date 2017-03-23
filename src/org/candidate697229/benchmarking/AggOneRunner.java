package org.candidate697229.benchmarking;

import org.candidate697229.algorithms.AggOne;
import org.candidate697229.database.Database;

import java.util.Arrays;
import java.util.List;

class AggOneRunner implements QueryRunner {
    private final List<Database> databases;
    private boolean debug = true;

    AggOneRunner(List<Database> databases) {
        this.databases = databases;
    }

    @Override
    public long runQueryAll(int database) {
        long start = System.currentTimeMillis();
        long[] result = new AggOne(databases.get(database)).computeAllAggregatesOfNaturalJoin();
        if (debug)
            System.out.println("RESULT\tAggOneRunner\t" + (database + 1) + "\tAll\t" + Arrays.toString(result));
        return System.currentTimeMillis() - start;
    }

    @Override
    public long runQueryOne(int database) {
        long start = System.currentTimeMillis();
        long result = new AggOne(databases.get(database)).computeOneAggregateOfNaturalJoin();
        if (debug)
            System.out.println("RESULT\tAggOneRunner\t" + (database + 1) + "\tOne\t[" + result + "]");
        return System.currentTimeMillis() - start;
    }
}
