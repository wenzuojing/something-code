package com.github.wens.code.lock;

import java.util.Random;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by wens on 15-11-26.
 */
public class WaitGroup {

    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        protected int tryAcquireShared(int acquires) {
            return getState() == acquires ? 1 : -1 ;
        }

        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-releases;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }

        }

        protected void add(int delta){
            for(;;){
                int c = getState();
                int nextc = c + delta;
                if (compareAndSetState(c, nextc))
                    break;
            }
        }
    }

    private final Sync sync;

    public WaitGroup(){
        this.sync = new Sync();
    }

    public void add( int  delta){
        this.sync.add(delta);
    }

    public void done(int delta ){
        this.sync.releaseShared(delta);
    }

    public void await() throws InterruptedException {
        this.sync.acquireSharedInterruptibly(0);
    }


    public static void main(String[] args) throws InterruptedException {

        final WaitGroup waitGroup = new WaitGroup();

        for(int i = 0 ; i < 1000 ; i++ ){
            final int n = i ;
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(new Random().nextInt(5000));
                    } catch (InterruptedException e) {
                        //
                    }
                    System.out.println( n + " OK");
                    waitGroup.done(1);
                }
            }.start();
            waitGroup.add(1);
        }
        waitGroup.await();

        System.out.println("All OK");
    }

}
