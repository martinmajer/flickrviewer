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
    
    private Queue<AsyncJob> queue;
    
    private AsyncLoader() {
        loaderThread = new Thread(this);
        loaderThread.setDaemon(true);
        
        lock = new Object();
        queue = new LinkedList();
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
            
            queue.add(job);
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
            
            AsyncJob nextJob = queue.poll();
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
    
    
}
