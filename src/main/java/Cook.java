public class Cook implements Runnable {
    private final long TIME_COOKING = 1500;
    private final Restaurant restaurant;
    private int clientMaxCount;

    public Cook(Restaurant restaurant, int clientMaxCount) {
        this.restaurant = restaurant;
        this.clientMaxCount = clientMaxCount;
    }

    public void run() {
        int countClients = 0;
        System.out.println(Thread.currentThread().getName() + ": приступает к работе");
        while (countClients<clientMaxCount) {
            restaurant.getCocksLock().lock();
            try {
                //Ожидаем заказа на кухню
                System.out.println(Thread.currentThread().getName() + ": ожидаю заказа.");
                restaurant.getCocksCondition().await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                restaurant.getCocksLock().unlock();
            }
            //Забираем заказ
            Order order = restaurant.getCocksStack().pop();
            //Начинаем его готовить
            preparingOrder(order);
            //Уведомляю официанта
            sendToWaiter(order);
            countClients++;
        }
        System.out.println(Thread.currentThread().getName() + " я закончил работу.");
    }

    private void preparingOrder(Order order) {
        System.out.println(Thread.currentThread().getName() + ": получил заказ " + order.getName() + " начинаю готовить");
        //Меняем статус заказа
        order.setStatus("Preparing");
        try {
            Thread.sleep(TIME_COOKING);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": приготовил заказ: " + order.getName());
        //Меняем статус заказа
        order.setStatus("Prepared. Send to waiter.");
    }

    private void sendToWaiter(Order order) {
        order.getCockLock().lock();
        try {
            order.getCockCondition().signal();
        } finally {
            order.getCockLock().unlock();
        }
    }
}
