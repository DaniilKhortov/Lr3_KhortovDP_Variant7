package Lr3_1;

import java.util.concurrent.RecursiveTask;

class MultiplyMatrixTask extends RecursiveTask<Void> {
        int availableResource = Runtime.getRuntime().availableProcessors() - 4;
        int[][] A, B, C;
        int startRow, endRow;
    MultiplyMatrixTask(int[][] A, int[][] B, int[][] C, int startRow, int endRow) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.startRow = startRow;
            this.endRow = endRow;
        }

    @Override
    protected Void compute() {
        if (endRow - startRow <= availableResource ) {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < B[0].length; j++) {
                    for (int k = 0; k < B.length; k++) {
                        C[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
        } else {
            int mid = (startRow + endRow) / 2;
            Lr3_1.MultiplyMatrixTask topTask = new Lr3_1.MultiplyMatrixTask(A, B, C, startRow, mid);
            Lr3_1.MultiplyMatrixTask bottomTask = new Lr3_1.MultiplyMatrixTask(A, B, C, mid, endRow);
            invokeAll(topTask, bottomTask);
        }
        return null;
    }
}


