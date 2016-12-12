package com.biubiu.miku.util;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池<p>
 * 如果线程数超过minSpareThreads,则会增加thread直到maxThreads，然后才放入队列.队列长度为maxQueueSize.
 */

public class ThreadPool extends AbstractExecutorService {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_MIN_SPARE_THREADS = CPU_COUNT + 1;
    private static final int DEFAULT_MAX_THREADS = CPU_COUNT * 2 + 1;

    // ---------------------------------------------- Properties
    private int threadPriority = Thread.NORM_PRIORITY;

    private boolean daemon = true;

    private String namePrefix = "miku-exec-";

    private int minSpareThreads = DEFAULT_MIN_SPARE_THREADS;

    private int maxThreads = DEFAULT_MAX_THREADS;

    private int maxQueueSize = 128;

    private long maxIdleTime = 60L;

    private ThreadPoolExecutor executor = null;

    protected String name;

    /**
     * Number of tasks submitted and not yet completed.
     */
    private AtomicInteger submittedTasksCount;

    private static volatile ThreadPool instance;

    public static ThreadPool getInstance() {
        if (instance == null) {
            synchronized (ThreadPool.class) {
                if (instance == null) {
                    instance = new ThreadPool();
                }
            }
        }
        return instance;
    }

    private ThreadPool() {
        this(DEFAULT_MIN_SPARE_THREADS, DEFAULT_MAX_THREADS);
    }

    private ThreadPool(int minSpareThreads, int maxThreads) {
        submittedTasksCount = new AtomicInteger();
        TaskQueue taskqueue = new TaskQueue(maxQueueSize);
        this.minSpareThreads = minSpareThreads;
        this.maxThreads = maxThreads;
        executor = new ThreadPoolExecutor(minSpareThreads, maxThreads, maxIdleTime,
                TimeUnit.SECONDS, taskqueue, new TaskThreadFactory(namePrefix),
                new ThreadPoolExecutor.DiscardOldestPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                AtomicInteger atomic = submittedTasksCount;
                if (atomic != null) {
                    atomic.decrementAndGet();
                }
            }
        };
        taskqueue.setParent(executor);
    }

    public void execute(Runnable command) {
        submittedTasksCount.incrementAndGet();
        try {
            executor.execute(command);
        } catch (RejectedExecutionException rx) {
            // there could have been contention around the queue
            if (!((TaskQueue) executor.getQueue()).force(command)) {
                submittedTasksCount.decrementAndGet();
                throw new RejectedExecutionException();
            }
        }
    }

    private int getThreadPriority() {
        return threadPriority;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinSpareThreads() {
        return minSpareThreads;
    }

    public String getName() {
        return name;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        if (executor != null) {
            executor.setKeepAliveTime(maxIdleTime, TimeUnit.SECONDS);
        }
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        executor.setMaximumPoolSize(maxThreads);
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        executor.setCorePoolSize(minSpareThreads);
    }

    public void setName(String name) {
        this.name = name;
    }

    // Statistics from the thread pool
    public int getActiveCount() {
        return executor.getActiveCount();
    }

    public int getSubmittedTasksCount() {
        return this.submittedTasksCount.get();
    }

    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    public int getCorePoolSize() {
        return executor.getCorePoolSize();
    }

    public int getLargestPoolSize() {
        return executor.getLargestPoolSize();
    }

    public int getPoolSize() {
        return executor.getPoolSize();
    }

    /**
     * 任务队列当前长度
     *
     * @return
     */
    public int getQueueSize() {
        return executor.getQueue().size();
    }

    // ---------------------------------------------- TaskQueue Inner Class
    class TaskQueue extends LinkedBlockingQueue<Runnable> {
        private static final long serialVersionUID = 5272493865199834486L;

        ThreadPoolExecutor parent = null;

        public TaskQueue() {
            super();
        }

        public TaskQueue(int initialCapacity) {
            super(initialCapacity);
        }

        public TaskQueue(Collection<? extends Runnable> c) {
            super(c);
        }

        public void setParent(ThreadPoolExecutor tp) {
            parent = tp;
        }

        public boolean force(Runnable o) {
            if (parent.isShutdown())
                throw new RejectedExecutionException(
                        "Executor not running, can't force a command into the queue");
            return super.offer(o); // forces the item onto the queue, to be used
            // if the task is rejected
        }

        public boolean offer(Runnable o) {
            // we can't do any checks
            if (parent == null)
                return super.offer(o);
            int poolSize = parent.getPoolSize();
            // we are maxed out on threads, simply queue the object
            if (poolSize == parent.getMaximumPoolSize())
                return super.offer(o);
            // we have idle threads, just add it to the queue
            // note that we don't use getActiveCount(), see BZ 49730
            AtomicInteger submittedTasksCount = ThreadPool.this.submittedTasksCount;
            if (submittedTasksCount != null) {
                if (submittedTasksCount.get() <= poolSize)
                    return super.offer(o);
            }
            // if we have less threads than maximum force creation of a new
            // thread
            if (poolSize < parent.getMaximumPoolSize())
                return false;
            // if we reached here, we need to add it to the queue
            return super.offer(o);
        }
    }

    // ---------------------------------------------- ThreadFactory Inner Class
    class TaskThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        TaskThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(daemon);
            t.setPriority(getThreadPriority());
            return t;
        }
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }

    @NonNull
    @Override
    public List<Runnable> shutdownNow() {
        return this.executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.awaitTermination(timeout, unit);
    }

}
