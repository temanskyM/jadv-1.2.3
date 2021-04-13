import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class App {
    final static private int MAX_WAITERS = 2;
    final static private int MAX_CLIENTS = 5;
    final static private long TIME_OPEN = 1000L;
    final static private long TIME_CLOSE_WAIT_CLIENTS = 5000L;
    final static private long TIME_CLOSE = 5000L;

    public static void main(String[] args) throws InterruptedException {

        Restaurant restaurant = new Restaurant();

        Thread threadCook = new Thread(new Cook(restaurant, MAX_CLIENTS));
        threadCook.setName("Повар №1");
        threadCook.start();

        ThreadFactory threadFactoryWaiters = new NamedThreadFactory("waiter-");
        ExecutorService executorWaiters = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                threadFactoryWaiters);
        for (int i = 0; i < MAX_WAITERS; i++) {
            Thread thread = new Thread(new Waiter(restaurant));
            thread.setName("Оффициант №" + (i + 1));
            executorWaiters.execute(thread);
        }
        Thread.sleep(TIME_OPEN);

        ThreadFactory threadFactoryClient = new NamedThreadFactory("client-");
        ExecutorService executorClients = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                threadFactoryClient);
        for (int i = 0; i < MAX_CLIENTS; i++) {
            Thread thread = new Thread(new Client(restaurant));
            thread.setName("Посититель №" + (i + 1));
            executorClients.execute(thread);
        }


        threadCook.join();
        Thread.sleep(TIME_CLOSE_WAIT_CLIENTS);
        System.out.println("Закрываем ресторан");
        executorWaiters.shutdownNow();
        executorClients.shutdownNow();
        Thread.sleep(TIME_CLOSE);
    }
}
