public class Waiter implements Runnable {
    private final Restaurant restaurant;

    public Waiter(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " приступает к работе");
        while (true) {
            System.out.println(Thread.currentThread().getName() + " ожидаю заказа.");

            restaurant.getWaitersLock().lock();
            try {
                restaurant.getWaitersCondition().await();
                //Получаем заказ
                Order order = restaurant.getWaitersStack().pop();
                order.setStatus("Processing");
                System.out.println(Thread.currentThread().getName() + ": получил заказ " + order.getName() + ", отправляю его на кухню.");
                //Отправляем его на кухню
                sendOrderToKitchen(order);
            } catch (InterruptedException e) {
               // e.printStackTrace();
                break;
            } finally {
                restaurant.getWaitersLock().unlock();
            }
        }
    }

    private void sendOrderToKitchen(Order order) {
        restaurant.getCocksLock().lock();
        try {
            //Закидываем заказ в стек поваров
            restaurant.getCocksStack().push(order);
            //Оповещаем поворов о новом заказе
            restaurant.getCocksCondition().signal();
            //Ожидаем готовности заказа
            System.out.println(Thread.currentThread().getName() + ": отправил заказ " + order.getName() + " на кухню, ожидаю его приготовления");
        } finally {
            restaurant.getCocksLock().unlock();
        }

        waitOrderFromKitchen(order);
        //Меняем статус заказа
        order.setStatus("Prepared. Send to client.");
        //Отдаем заказ поситителяем
        sendOrderToClient(order);
    }

    private void waitOrderFromKitchen(Order order) {
        order.getCockLock().lock();
        try {
            order.getCockCondition().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            order.getCockLock().unlock();
        }
    }

    private void sendOrderToClient(Order order) {
        order.getWaiterLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + ": получил заказ " + order.getName() + "с кухни, отправляю клиенту ");
            order.setStatus("Done");
            order.getWaiterCondition().signal();
        } finally {
            order.getWaiterLock().unlock();
        }
    }
}
