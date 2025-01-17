# Prerequisites

To run the code, first ensure you have suitable versions of the Java Runtime Environment (JRE) and Java compiler installed by running the commands:

    java -version
    javac -version

And checking that the two version numbers are the same and at least 1.8 (this is the case on the lab machines).

Throughout these instructions, we assume the the reader is running a shell instance in the directory this file is located in.

# Building the code

The submission includes built classes in the `build` directory. However, should the reader wish to re-build these classes from scratch this can be achieved by running:

    rm -rf build && mkdir build
    javac $(find src -name "*.java") -d build

# Running the code

Set the CLASSPATH environment variable to the `build` directory by running.

    CLASSPATH=$(pwd)/build

Also, add the SQLite driver to the `CLASSPATH` environment variable by running:

    CLASSPATH=$(pwd)/lib/sqlite-jdbc-3.16.1.jar:$CLASSPATH

Finally, run the benchmarker (which will run all three solutions in turn) with 4GB of memory by executing:

    java -Xmx4G org.candidate697229.benchmarking.Benchmarker

As provided, the code will timeout after 15 minutes for each solution, which will likely only give the final solution enough time to complete with every scale factor.
The `TIMEOUT_SECONDS` constant in `Configuration.java` can be changed to vary this timeout.

The `exampleOutput` file contains example output obtained by running the program with longer timeout (4 hours per solution).
This was obtained on a computer with Ubuntu 16.04, an Intel i7-4980HQ processor and 16GB of memory (of which 8GB was given as a heap limit to the JRE).
The JRE used was that from OpenJDK 1.8, 64-bit version.

# Using the output

Results for specific plots can be filtered out from the program's output, by running for example:

    cat exampleOutput | grep TIME | grep AggOneRunner | grep All

To get the average times (in milliseconds) for computing all the aggregates using `AggOne` as the scale factor varies.

These results have been copied into the spreadsheet `timings.ods` (which can be opened with, for example, LibreOffice Calc) and plotted there.