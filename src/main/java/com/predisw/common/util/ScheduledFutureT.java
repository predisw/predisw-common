package com.predisw.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by eggnwwg on 7/3/2017.
 */
public class ScheduledFutureT {

    private ScheduledExecutorService executor;
    private ScheduledFuture<String> scheduledFuture;

    private Logger logger =Logger.getLogger(ScheduledFutureT.class.getName());

    public class PrintT implements Callable<String>{

        @Override
        public String call() throws Exception {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss SS");

            String timeNotation = sdf.format(new Date());

            logger.fine(timeNotation);
            return timeNotation;
        }
    }


    public static void main(String[] args) throws InterruptedException {

        ScheduledFutureT testTask = new ScheduledFutureT();
        testTask.setUP();
        testTask.testDelayExecute();
        testTask.testImediateExecute();

    }



    public   void setUP(){
//
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);
        logger.setLevel(Level.FINE);

        executor= Executors.newScheduledThreadPool(2);

    }
    public void testDelayExecute(){
       logger.fine("put delay task in Thread Pool");
        PrintT delayPrint=new PrintT();
       scheduledFuture= executor.schedule(delayPrint,3, TimeUnit.SECONDS);

    }

    public void testImediateExecute() throws InterruptedException {

        if(scheduledFuture!=null && !scheduledFuture.isDone()
                && !scheduledFuture.isCancelled()){

            //logger.fine("Cancel(false) return is "+ scheduledFuture.cancel(false));
            scheduledFuture=null;
            executor.submit(new PrintT());
        }


    }


}
