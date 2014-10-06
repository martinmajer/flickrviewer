/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.async;

import flickrviewer.api.FlickrException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vlákno pro asynchronní načítání dat z FlickrAPI.
 * @author Martin
 */
public class AsyncLoader implements Runnable {
    
    private static AsyncLoader asyncLoader = null;
    
    private Thread loaderThread;
    private final Object lock;
    
    private PriorityQueue<AsyncJobInternal> queue;
    
    private AsyncLoader() {
        loaderThread = new Thread(this);
        loaderThread.setDaemon(true);
        
        lock = new Object();
        queue = new PriorityQueue<>(10, new AsyncJobComparator());
    }
    
    /**
     * Vrátí instanci objektu pro assynchronní načátání dat.
     * @return 
     */
    public static AsyncLoader getInstance() {
        if (asyncLoader == null) asyncLoader = new AsyncLoader();
        return asyncLoader;
    }
    
    public void load(AsyncJob job) {
        synchronized (lock) {
            if (!loaderThread.isAlive()) {
                loaderThread.start();

                // počkáme na start vlákna
                try { lock.wait(); } 
                catch (InterruptedException ex) {}
            }
            
            queue.add(new AsyncJobInternal(job));
            System.out.println("AsyncLoader: Added job " + job + " (" + Thread.currentThread().getName() + ")");
            lock.notifyAll();
        }
    }
    

    @Override
    public void run() {
        System.out.println("AsyncLoader: thread started (" + Thread.currentThread().getName() + ")");
        
        // oznámení o startu vlákna
        synchronized (lock) {
            lock.notifyAll();
        }
        
        while (true) {
            synchronized (lock) {
                while (queue.isEmpty()) {
                    try { lock.wait(); } 
                    catch (InterruptedException ex) {}
                }
            }
            
            AsyncJob nextJob = queue.poll().job;
            System.out.println("AsyncLoader: Retrieved job " + nextJob);
            
            try {
                Object result = nextJob.loadData();
                nextJob.done(result, null);
            } 
            catch (FlickrException ex) {
                nextJob.done(null, ex);
            }
        }
    }
    
    
    
    private static class AsyncJobInternal {
        
        private static int currentId = 0;
        
        public AsyncJob job;
        public int id;
        
        public AsyncJobInternal(AsyncJob job) {
            this.job = job;
            id = ++currentId;
        }
        
    }
    
    
    private static class AsyncJobComparator implements Comparator<AsyncJobInternal> {

        @Override
        public int compare(AsyncJobInternal a, AsyncJobInternal b) {
            int priorityA = 0, priorityB = 0;
            if (a.job instanceof AsyncJob.Priority) {
                priorityA = ((AsyncJob.Priority)a.job).getPriority();
            }
            if (b.job instanceof AsyncJob.Priority) {
                priorityB = ((AsyncJob.Priority)b.job).getPriority();
            }
            
            if (priorityA > priorityB) return -1;
            else if (priorityA < priorityB) return 1;
            else {
                if (a.id < b.id) return -1;
                else if (a.id > b.id) return 1;
                else return 0;
            }
        }
        
    }
    
    
}
