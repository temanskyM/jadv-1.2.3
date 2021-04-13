import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Order {
    private static int id;
    private String name;
    private String status;

    private ReentrantLock waiterLock;
    private Condition waiterCondition;
    private ReentrantLock cockLock;
    private Condition cockCondition;

    public Order(String name) {
        id++;

        waiterLock = new ReentrantLock();
        waiterCondition = waiterLock.newCondition();
        cockLock = new ReentrantLock();
        cockCondition = cockLock.newCondition();

        this.name = name;
        this.status = "Created";
    }

    public ReentrantLock getWaiterLock() {
        return waiterLock;
    }

    public Condition getWaiterCondition() {
        return waiterCondition;
    }

    public ReentrantLock getCockLock() {
        return cockLock;
    }

    public Condition getCockCondition() {
        return cockCondition;
    }

    public static int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
