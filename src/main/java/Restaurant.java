import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private ReentrantLock waitersLock;
    private Condition waitersCondition;
    private ReentrantLock cocksLock;
    private Condition cocksCondition;

    private Stack<Order> waitersStack;
    private Stack<Order> cocksStack;

    public Restaurant() {
        waitersLock = new ReentrantLock();
        waitersCondition = waitersLock.newCondition();
        cocksLock = new ReentrantLock();
        cocksCondition = cocksLock.newCondition();
        waitersStack = new Stack<>();
        cocksStack = new Stack<>();
    }

    public ReentrantLock getWaitersLock() {
        return waitersLock;
    }

    public Condition getWaitersCondition() {
        return waitersCondition;
    }

    public ReentrantLock getCocksLock() {
        return cocksLock;
    }

    public Condition getCocksCondition() {
        return cocksCondition;
    }

    public Stack<Order> getWaitersStack() {
        return waitersStack;
    }

    public Stack<Order> getCocksStack() {
        return cocksStack;
    }
}
