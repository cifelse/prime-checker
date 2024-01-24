package src;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    //Source: Marimuthu Madasamy https://stackoverflow.com/questions/5516383/how-to-return-object-from-callable
    public static Callable<Boolean[]> getPrimesPerThread(int limit){
        return new Callable<Boolean[]>(){
            public Boolean[] call() throws Exception{
                Boolean[] temp = new Boolean[limit];
                temp[0] = false;
                for (int current_num = 2 ; current_num <= limit; current_num++) {
                    temp[current_num-1] = check_prime(current_num);
                }
                return temp;
            }
        };   
    }

    private static final int LIMIT = 100;
    public static void main (String[] args) throws InterruptedException, ExecutionException {
        List<Integer> primes = new ArrayList<Integer>();
        int upper_bound = LIMIT;
        int n_threads = 1;

        //Get User Input
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter upper bound of integers to check: ");
        upper_bound = scan.nextInt();

        System.out.println("Enter number of threads:");
        n_threads = scan.nextInt();

        //Start timer
        long start_time = System.nanoTime();
        
        //Auto thread, auto mutex
        Callable <Boolean[]> callable = getPrimesPerThread(upper_bound);
        ExecutorService executor = Executors.newFixedThreadPool(n_threads);
        Future<Boolean[]> result = executor.submit(callable);


        //End timer
        long end_time = 0;
        long n_primes = 0;
        executor.shutdown();
        try{
            executor.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);
            Boolean[] result_array = result.get();
            end_time =  System.nanoTime();
            //Count Primes
            List<Boolean> resultList  = Arrays.asList(result_array);
            n_primes = resultList.stream().filter(prime -> prime == true).count();
        }catch(InterruptedException e){
            System.out.println("NOT DONE EXECUTING");
        }

        // Total Time
        long total_time = TimeUnit.NANOSECONDS.toMillis(end_time - start_time);

        System.out.printf("%d primes were found.\n", n_primes);
        System.out.printf("Start: %d | End: %d | Total Runtime: %d miliseconds",start_time, end_time, total_time);
    }    

    /**
     * This function checks if an integer n is prime.
     * @param n - integer to check
     * @return true if n is prime, and false otherwise.
     */
    public static boolean check_prime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}