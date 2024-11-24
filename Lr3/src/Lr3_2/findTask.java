package Lr3_2;

import java.io.File;
import java.util.concurrent.RecursiveTask;

public class findTask extends RecursiveTask<Integer> {
    int availableResource = Runtime.getRuntime().availableProcessors() - 4;
    int startRow, endRow;
    File[] files;
    String word;

    // Змінна matches має бути локальною для кожної підзадачі, а не спільною для всіх.

    findTask(File[] files, String word, int startRow, int endRow) {
        this.files = files;
        this.word = word;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    protected Integer compute() {
        if (endRow - startRow <= availableResource) {
            int matches = 0;
            for (int i = startRow; i < endRow; i++) {
                if (files[i].getName().contains(word)) {
                    matches++;
                    System.out.println(files[i].getName());
                }
            }
            return matches;
        } else {
            // Якщо задачі великі, розбиваємо на підзадачі
            int mid = (startRow + endRow) / 2;
            findTask topTask = new findTask(files, word, startRow, mid);
            findTask bottomTask = new findTask(files, word, mid, endRow);

            // Викликаємо обидві підзадачі
            invokeAll(topTask, bottomTask);


            return topTask.join() + bottomTask.join();
        }
    }
}
