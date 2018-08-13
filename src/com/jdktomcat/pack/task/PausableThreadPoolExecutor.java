package com.jdktomcat.pack.task;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * desc:实现暂停恢复功能
 *
 * @author jdktomcat
 */
public class PausableThreadPoolExecutor extends ThreadPoolExecutor {

    private boolean isPaused;

    private ReentrantLock pauseLock = new ReentrantLock();

    private Condition unPaused = pauseLock.newCondition();


    public PausableThreadPoolExecutor() {
        super(5, 10, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) {
                unPaused.await();
            }
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unPaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
}

