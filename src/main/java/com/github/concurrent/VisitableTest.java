package com.github.concurrent;

import sun.misc.Unsafe;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JMM 可见性测试
 */
public class VisitableTest {
    Lock lock = new ReentrantLock();

    private int count = 0;

    private boolean flag = true;

    public void refresh(boolean flag) {
        System.out.println("refresh: " + flag);
        this.flag = flag;
    }

    public void load() {
        System.out.println("load");
        while (flag) {
            lock.lock();
            count++;
            lock.unlock();

            // 下列方式可解决可见性问题
            // Thread.yield();
            // 休眠可以解决可见性
            /*try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            // println内部使用了synchronized关键字，可解决可见性问题
            // System.out.println(count);
            // 内存屏障
            // Unsafe.getUnsafe().loadFence();
        }
        System.out.println("load finish, count: " + count);
    }

    public static void main(String[] args) throws InterruptedException {
        VisitableTest visitableTest = new VisitableTest();

        Thread threadA =  new Thread(visitableTest::load);
        Thread threadB = new Thread(() -> visitableTest.refresh(false));

        threadA.start();
        Thread.sleep(1000);
        threadB.start();
    }
}