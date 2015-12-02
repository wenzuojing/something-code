package com.github.wens.code.leveldb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wens on 15-12-1.
 */
public class Benchmark {


    public static void main(String[] args) throws IOException {


        Options options = new Options();
        options.createIfMissing(true);
        final DB db = factory.open(new File("example"), options);
        final AtomicLong opt = new AtomicLong() ;
        final AtomicLong bytes = new AtomicLong() ;
        int n = 5 ;
        final byte[] data = "dfdfdsf".getBytes() ;
        for(int i = 0 ; i < data.length ; i++ ){
            data[i] = 0 ;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0 ; i < n ; i++ ){
            final String  ii = i +"" ;
            new Thread(){
                @Override
                public void run() {
                    for(long j = 0 ; ; j++){
                        db.put( ( ii + j) .getBytes(), data );
                        opt.incrementAndGet();
                        bytes.addAndGet(data.length);
                    }
                }
            }.start();

        }

        new Thread(){
            @Override
            public void run() {
                while(true){
                    System.out.println(String.format("%d opt/s %d byte/s" , opt.get() / 5 , bytes.get() / 5 ));
                    opt.set(0);
                    bytes.set(0);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
