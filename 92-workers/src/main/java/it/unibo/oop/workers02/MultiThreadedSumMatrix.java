package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int start_row;
        private final int nrows;
        private double res;

        Worker(final double[][] matrix, final int start_row, final int nrows) {
            super();
            this.matrix = matrix;
            this.start_row = start_row;
            this.nrows = nrows;
        }

        public void run() {
         System.out.println("Working from row " + start_row + " to row " + (start_row + nrows - 1));
             for (int i = start_row; i < matrix.length && i < start_row + nrows; i++) {
                 for (double ds : this.matrix[i]) {
                     this.res += ds;
                 }
             }
        }

        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length / nthread + matrix.length % nthread;
        final List<Worker> workers = new ArrayList<>(nthread);

        for (int start = 0; start < matrix.length ; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker w: workers) {
            w.start();
        }

        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }
}
