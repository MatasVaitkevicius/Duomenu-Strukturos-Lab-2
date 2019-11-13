package edu.ktu.ds.lab2.vaitkevicius;

import edu.ktu.ds.lab2.gui.ValidationException;
import edu.ktu.ds.lab2.utils.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class SimpleBenchmark {

    public static final String FINISH_COMMAND = "                       ";
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("edu.ktu.ds.lab2.gui.messages");

    private static final String[] BENCHMARK_NAMES = {"addBstRec", "addBstIte", "addAvlRec",
        "removeBst", "treeSetAdd", "hashSetAdd", "treeRemove", "hashRemove"};
    private static final int[] COUNTS = {10000, 20000, 40000, 80000};

    private final Timekeeper timeKeeper;
    private final String[] errors;

    private final SortedSet<Book> cSeries = new BstSet<>(Book.byPrice);
    private final SortedSet<Book> cSeries2 = new BstSetIterative<>(Book.byPrice);
    private final SortedSet<Book> cSeries3 = new AvlSet<>(Book.byPrice);
    private TreeSet<Integer> treeSeries = Benchmark.treeSet;
    private HashSet<Integer> hashSeries = Benchmark.hashSet;
    /**
     * For console benchmark
     */
    public SimpleBenchmark() {
        timeKeeper = new Timekeeper(COUNTS);
        errors = new String[]{
                MESSAGES.getString("badSetSize"),
                MESSAGES.getString("badInitialData"),
                MESSAGES.getString("badSetSizes"),
                MESSAGES.getString("badShuffleCoef")
        };
    }

    /**
     * For Gui benchmark
     *
     * @param resultsLogger
     * @param semaphore
     */
    public SimpleBenchmark(BlockingQueue<String> resultsLogger, Semaphore semaphore) {
        semaphore.release();
        timeKeeper = new Timekeeper(COUNTS, resultsLogger, semaphore);
        errors = new String[]{
                MESSAGES.getString("badSetSize"),
                MESSAGES.getString("badInitialData"),
                MESSAGES.getString("badSetSizes"),
                MESSAGES.getString("badShuffleCoef")
        };
    }

    public static void main(String[] args) {
        executeTest();
    }

    public static void executeTest() {
        // suvienodiname skaičių formatus pagal LT lokalę (10-ainis kablelis)
        Locale.setDefault(new Locale("LT"));
        Ks.out("Greitaveikos tyrimas:\n");
        new SimpleBenchmark().startBenchmark();
    }

    public void startBenchmark() {
        try {
            benchmark();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    private void benchmark() throws InterruptedException {
        try {
            for (int k : COUNTS) {
                Book[] books = new BooksGenerator().generateShuffle(k, 1.0);
                cSeries.clear();
                cSeries2.clear();
                cSeries3.clear();
                timeKeeper.startAfterPause();

                timeKeeper.start();
                Arrays.stream(books).forEach(cSeries::add);
                timeKeeper.finish(BENCHMARK_NAMES[0]);

                Arrays.stream(books).forEach(cSeries2::add);
                timeKeeper.finish(BENCHMARK_NAMES[1]);

                Arrays.stream(books).forEach(cSeries3::add);
                timeKeeper.finish(BENCHMARK_NAMES[2]);

                Arrays.stream(books).forEach(cSeries::remove);

                timeKeeper.finish(BENCHMARK_NAMES[3]);
                
                treeSeries = Benchmark.genTree(k);
                timeKeeper.finish(BENCHMARK_NAMES[4]);
                
                hashSeries = Benchmark.genHash(k);
                timeKeeper.finish(BENCHMARK_NAMES[5]);

                treeSeries.remove(k);
                timeKeeper.finish(BENCHMARK_NAMES[6]);
                
                hashSeries.remove(k);
                timeKeeper.finish(BENCHMARK_NAMES[7]);
                
                timeKeeper.seriesFinish();
            }
            timeKeeper.logResult(FINISH_COMMAND);
        } catch (ValidationException e) {
            if (e.getCode() >= 0 && e.getCode() <= 3) {
                timeKeeper.logResult(errors[e.getCode()] + ": " + e.getMessage());
            } else if (e.getCode() == 4) {
                timeKeeper.logResult(MESSAGES.getString("allSetIsPrinted"));
            } else {
                timeKeeper.logResult(e.getMessage());
            }
        }
    }
}
