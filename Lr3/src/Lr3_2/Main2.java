package Lr3_2;


import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class Main2 {
    public static void main(String[] args) {
        Scanner fabulousDirectoryImport = new Scanner(System.in);
        System.out.printf("Enter directory path: \n");
        String directoryPath = fabulousDirectoryImport.nextLine();


        System.out.printf("Main Thread: begin\n");
        long startTime = System.currentTimeMillis();

        File directory = new File(directoryPath);


        File[] files = directory.listFiles();
        long resultTime1 = System.currentTimeMillis()-startTime;
        System.out.printf("Search complete!\n", resultTime1);
        System.out.printf("Array init time: %s ms\n", resultTime1);

        if (files != null) {
            for (File file : files) {
                System.out.printf(file.getName() + ", " );

            }
            System.out.printf("Enter word: \n");
            String word = fabulousDirectoryImport.nextLine();
            System.out.printf("\n");
            long startTime2 = System.currentTimeMillis();

            findWords(files, word);
            System.out.printf("\n-----------------------------------------------------------------------------------------\n");
            System.out.printf("Task finished!\n");
            System.out.printf("Main Thread: end\n");
            long resultTime2 = System.currentTimeMillis()-startTime2;
            System.out.printf("Computing time: %s ms\n",resultTime2);
            System.out.printf("Total completion time: %s ms\n",resultTime2+resultTime1);
            System.out.printf("Total session time: %s ms\n",System.currentTimeMillis()-startTime);

        }else {
            System.out.printf("No files found!");
        }



    }
    static void findWords(File[] files, String word){

        System.out.printf("\n");
        ForkJoinPool pool = new ForkJoinPool();
        findTask task = new findTask(files, word,0, files.length);

        System.out.printf("\nMatches found: %d", pool.invoke(task));
        pool.shutdown();
    }
}
