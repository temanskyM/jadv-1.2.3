import java.util.concurrent.TimeUnit;

public class Client implements Runnable {
    private final long TIME_WAIT = 10000L;
    private Restaurant restaurant;

    public Client(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void run() {
        //Заходим в ресторан
        System.out.println(Thread.currentThread().getName() + " заходит в ресторан.");
        Order order = new Order("'Блюдо для " + Thread.currentThread().getName() + "'");
        restaurant.getWaitersLock().lock();
        try {
            restaurant.getWaitersStack().push(order);//Закидываем заказ в стек
            System.out.println(Thread.currentThread().getName() + ": отправил заказ " + order.getName() + " официанту, ожидаю его приготовления");
            //Сигнализируем официантам, чтобы они забрали заказ
            restaurant.getWaitersCondition().signal();
        } finally {
            restaurant.getWaitersLock().unlock();
        }

        waitOrder(order);
    }

    private void waitOrder(Order order) {
        order.getWaiterLock().lock();
        try {
            //Ожидаем заказа
            order.getWaiterCondition().await(TIME_WAIT, TimeUnit.MILLISECONDS);
            //Проверяем статус заказа
            while (!order.getStatus().equals("Done")) {
                if (order.getStatus().equals("Created")) {
                    //Оповещаем официантов
                    signalWaiter();
                } else {
                    System.out.println(Thread.currentThread().getName() + ": заказ в обработке, ожидаю.");
                }
                //Ожидаем заказа
                order.getWaiterCondition().await(TIME_WAIT, TimeUnit.MILLISECONDS);
            }


            //Получили заказ
            System.out.println(Thread.currentThread().getName() + " получил свой заказ и ухожу.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            order.getWaiterLock().unlock();
        }
    }

    private void signalWaiter() {
        restaurant.getWaitersLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + ": еще раз уведомил официанта, ожидаю приготовления моего заказа");
            //Сигнализируем официантам, чтобы они забрали заказ
            restaurant.getWaitersCondition().signal();
        } finally {
            restaurant.getWaitersLock().unlock();
        }
    }
}
