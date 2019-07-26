package yan.biyan;

/*
 * Copyright (c) This is zhaoxubin's Java program.
 * Copyright belongs to the crabapple organization.
 * The crabapple organization has all rights to this program.
 * No individual or organization can refer to or reproduce this program without permission.
 * If you need to reprint or quote, please post it to zhaoxubin2016@live.com.
 * You will get a reply within a week,
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<T extends Runnable> implements ThreadPool<T> {

    //线程池最大线程限制

    public static final int MAX_WORKER_NUMBERS = 10;

    //线程池默认数量
    public static final int DEFAULT_WORKER_NUMBERS = 5;

    //线程最小限制
    public static final int MIN_WORKER_NUMBERS = 1;

    //任务
    private final LinkedList<T> jobs = new LinkedList<>();

    //工作线程
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());

    //工作者线程的数量
    private int workerNum = DEFAULT_WORKER_NUMBERS;

    //线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWokers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        this.workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWokers(workerNum);
    }

    @Override
    public void execute(T job) {

        if (job != null) {

            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {

        for (Worker worker : workers)
            worker.shutdown();
    }


    @Override
    public void addWorkers(int num) {

        synchronized (jobs) {
            //限制新加工作线程数量
            if (num + this.workerNum > MAX_WORKER_NUMBERS)
                num = MAX_WORKER_NUMBERS - this.workerNum;

            initializeWokers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorker(int num) {

        synchronized (jobs) {

            if (num >= this.workerNum)
                throw new IllegalArgumentException("beyond workNum");
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                if (workers.remove(worker)) {
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }


    //初始化线程工作
    private void initializeWokers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }


    //工作者负责消费任务

    class Worker implements Runnable {

        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                T job = null;
                synchronized (jobs) {

                    //如果工作列表是空的，那就wait
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            //感知外部的中断操作
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception ex) {

                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }

    }
}
