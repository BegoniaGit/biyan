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

public interface ThreadPool<T extends Runnable> {

    //执行一个线程
    void execute(T job);

    //关闭工作线程
    void shutdown();

    //增加工作线程
    void addWorkers(int num);

    //减少工作线程
    void removeWorker(int num);

    //得到正在等待执行的任务的数量
    int getJobSize();
}
