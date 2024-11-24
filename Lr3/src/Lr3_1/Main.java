package Lr3_1;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;


class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int lowestMeasure = 0;
        int highestMeasure = 0;
        int row1 = 0;
        int col1 = 0;
        int row2 = 0;
        int col2 = 0;

        System.out.printf("Main Thread: begin\n");

        //Ввід діапозону генерованих чисел
        while (true){
            Scanner gloriousNImport = new Scanner(System.in);
            System.out.println("Enter lowest measure in range [0, 1000]:");
            try{
                lowestMeasure = gloriousNImport.nextInt();
                if (lowestMeasure>1000 || lowestMeasure<0){
                    throw new InputMismatchException("Input error!");
                }

            }catch (InputMismatchException e){
                System.out.println("Proposed measure is out of range!");
                System.out.println("Try again!");

            }
            System.out.println("Enter highest measure in range [0, 1000]:");
            try{
                highestMeasure = gloriousNImport.nextInt();
                if (highestMeasure>1000 || highestMeasure<0 || highestMeasure<lowestMeasure){
                    throw new InputMismatchException("Input error!");
                }

                System.out.println();


            }catch (InputMismatchException e){
                System.out.println("Proposed measure is out of range!");
                System.out.println("Try again!");

            }

            System.out.println("Enter amount of rows in first matrix:");

            try{

                row1 = gloriousNImport.nextInt();
                if (row1<1){
                    throw new InputMismatchException("Input error!");
                }
                System.out.println("Enter amount of columns in first matrix:");
                col1 = gloriousNImport.nextInt();
                if (col1<1){
                    throw new InputMismatchException("Input error!");
                }
                System.out.println("Enter amount of rows in second matrix:");
                row2 = gloriousNImport.nextInt();
                if (row2<1){
                    throw new InputMismatchException("Input error!");
                }
                System.out.println("Enter amount of columns in second matrix:");
                col2 = gloriousNImport.nextInt();
                if (col2<1){
                    throw new InputMismatchException("Input error!");
                }
                break;
            }catch (InputMismatchException e){
                System.out.println("Proposed measure is out of range!");
                System.out.println("Try again!");

            }


        }


        long startTime = System.currentTimeMillis();
        System.out.println("Generating Matrix A...");
        int[][] A = initializeMatrix(row1, col1, lowestMeasure, highestMeasure);

        System.out.println("Generating Matrix B...");
        int[][] B = initializeMatrix(row2, col2, lowestMeasure, highestMeasure);

        long resultTime1 = System.currentTimeMillis()-startTime;
        System.out.printf("Initialization complete!\n", resultTime1);
        System.out.printf("Array init time: %s ms\n", resultTime1);

        System.out.println("\nMatrix A:");
        printMatrix(A, row1, col1);
        System.out.println("\nMatrix B:");
        printMatrix(B, row2, col2);

        long startTime2 = System.currentTimeMillis();
        askKaiserToMultiplyMatrix(row1, col1, A, row2, col2, B);


        System.out.printf("-----------------------------------------------------------------------------------------\n");
        System.out.printf("Task finished!\n");
        System.out.printf("Main Thread: end\n");
        long resultTime2 = System.currentTimeMillis()-startTime2;
        System.out.printf("Computing time: %s ms\n",resultTime2);
        System.out.printf("Total completion time: %s ms\n",resultTime2+resultTime1);
        System.out.printf("Total session time: %s ms\n",System.currentTimeMillis()-startTime);


    }



    static void printMatrix(int M[][], int rowSize, int colSize) {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++)
                System.out.print(M[i][j] + " ");
            System.out.println();
        }
    }





    static int[][] initializeMatrix(int rows, int cols, int low, int high) throws InterruptedException, ExecutionException {
        int[][] matrix = new int[rows][cols];
        ExecutorService executor = Executors.newFixedThreadPool(rows * cols);
        Random kaiserRandom = new Random();

        Future<Integer>[][] futures = new Future[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                futures[i][j] = executor.submit(() -> {

                    return kaiserRandom.nextInt(high+1-low)+low;
                });
            }
        }


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = futures[i][j].get();
            }
        }

        executor.shutdown();
        return matrix;
    }
    static int[][] askKaiserToMultiplyMatrix(int row1, int col1, int A[][], int row2, int col2, int B[][]){


        if (row2 != col1) {
            System.out.println("\nMultiplication is not possible");
            int[][] C = new int[row1][col2];
            return null;
        }

        int[][] C = new int[row1][col2];

        ForkJoinPool pool = new ForkJoinPool();
        Lr3_1.MultiplyMatrixTask task = new Lr3_1.MultiplyMatrixTask(A, B, C, 0, row1);
        pool.invoke(task);
        pool.shutdown();

        System.out.println("\nResultant Matrix:");
        printMatrix(C, row1, col2);
        return C;
    }

}
