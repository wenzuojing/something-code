package com.github.wens.code.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by wens on 15-12-2.
 */
public class LockUtils {

    public static <T> T inLock( Object monitorObj , CodeBlock<T> codeBlock  ){
        synchronized (monitorObj){
            return codeBlock.codeBlock();
        }
    }

    public static <T> T inLock( Lock lock , CodeBlock<T> codeBlock ){
        lock.lock();
        try {
            return codeBlock.codeBlock();
        }finally {
            lock.unlock();
        }
    }

    public static <T> T inReadLock( ReadWriteLock lock , CodeBlock<T> codeBlock ){
        lock.readLock().lock();
        try {
            return codeBlock.codeBlock();
        }finally {
            lock.readLock().unlock();
        }
    }

    public static <T> T inWriteLock( ReadWriteLock lock , CodeBlock<T> codeBlock ){
        lock.writeLock().lock();
        try {
            return codeBlock.codeBlock();
        }finally {
            lock.writeLock().unlock();
        }
    }

}
