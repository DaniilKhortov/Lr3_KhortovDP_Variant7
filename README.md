# LR3_Async_Khortov

# Завдання
<p><b>Варіант-2</b></p>
<ul>
<li>1. Знайти добуток двох матриць.
Розмірність матриць, початкове та кінцеві значення має задавати
користувач. Значення елементів генеруйте рандомно.
У результаті на екран має бути виведено згенеровані матриці,
результат виконання задачі та час роботи програми.</li>

<li>2. Напишіть програму, яка буде проходити по файлам певної
директорії та рахувати загальну кількість тих, що в своїй назві містять
певну літеру або слово.
Директорію та літеру (або слово) має задавати користувач.
У результаті потрібно вивести кількість знайдених файлів.</li>
</ul>

# Загальний опис рішення
<p><b>Проект складається з трьох частин:</b></p>
<ul>
  <li>1. Калькулятор множення матриць за допомогою work stealing (Завдання 1.1)</li>
  <li>2. Калькулятор множення матриць за допомогою work dealing (Завдання 1.2)</li>
  <li>3. Пошуковик слова у назвах файлів директорії (Завдання 2)</li>
</ul>

# Опис ініціалізації матриць (Завдання 1)
<p>На початку, програма просить користувача встановити діапазон генерованих значень.<\n></p>
 
Основні положення вводу:
<ul>
  <li>1.Мінімальна можлива межа: 0, максимальна можлива межа: 1000.</li>
  <li>2.При введені невідповідних значень, програма попросить заново задати межі діапозону.</li>
</ul>



        int lowestMeasure = 0;
        int highestMeasure = 0;

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
        
   Для генерації масиву використовується функція <b>initializeMatrix</b>. Ось його структура:
   

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


# Опис обчислення
<p>Алгоритм обчислення був взятий з ресурсу <a href="https://www.geeksforgeeks.org/java-program-to-multiply-two-matrices-of-any-size/">Geek for Geeks</a> <b>askKaiserToCheckWhetherTheNumberIsPrime</b>:</p>
        
Метод має ітеративно множити відповідні рядки, заповнюючи вихідну матрицю. При тому, якщо матриці не підходять для множення, програма їх відкидає.<br>

Обчислення матриць Варіант 1 : 
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
    
Оскільки перший варіант використовує принцип  work stealing, тоді необхідно ініціалізувати клас та методи для ForkJoin:

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

Для work dealing використовуємо Collable, надаючи кожному потоку відповідний ряд матриці для обробки. 
Обчислення матриць Варіант 2 : <br>

          static void askKaiserToMultiplyMatrix(int row1, int col1, int[][] A, int row2, int col2, int[][] B) {
          if (row2 != col1) {
              System.out.println("\nMultiplication is not possible");
              return;
          }
          int[][] C = new int[row1][col2];
          ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  
          List<Callable<Void>> tasks = new ArrayList<>();
          for (int i = 0; i < row1; i++) {
              final int rowIndex = i;
              tasks.add(() -> {
                  for (int j = 0; j < col2; j++) {
                      C[rowIndex][j] = 0;
                      for (int k = 0; k < col1; k++) {
                          C[rowIndex][j] += A[rowIndex][k] * B[k][j];
                      }
                  }
                  return null;
              });
          }
          
  
          try {
              executor.invokeAll(tasks);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
  
          executor.shutdown();
  
          System.out.println("\nResultant Matrix:");
          printMatrix(C, row1, col2);
      }
     }
  
# Опис ініціалізації масиву (Завдання 2)
За допомогою бібліотеки java.io.File, можна отримати масив файлів за вказаним шляхом, тому надаємо користувачеві його ввести. У випадку незнаходження жодного файлу, код просто закінчить роботу.

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
            findWords(files, word);
            
# Опис пошуку лексеми у назвах файлів
Після зчитування назв файлів, необхідно надати користувачеві ввести символ або слово. Потім використаймо функцію findWords для пошуку збігів.

    static void findWords(File[] files, String word){

        System.out.printf("\n");
        ForkJoinPool pool = new ForkJoinPool();
        findTask task = new findTask(files, word,0, files.length);

        System.out.printf("\nMatches found: %d", pool.invoke(task));
        pool.shutdown();
    }
    
<b>Для пошуку файлів найкраще підходить work stealing, який також відомий як "Розділяй й володаруй". За допомогою рекурсії, можна швидко оббігти масиви, особливо відносно невеликі (як файлові системи), оскільки не витрачається час на розподіл задач потоками. Замість цього, вони самі підбирають завдання, коли вільні.</b> За допомогою contains знаходимо збіги користувацької лексеми у назвах, потім сумуємо локальні зміни та експортуємо загальний результат.


    public class findTask extends RecursiveTask<Integer> {
    int availableResource = Runtime.getRuntime().availableProcessors() - 4;
    int startRow, endRow;
    File[] files;
    String word;
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

            
