package com.yc.threadpool;

/**
 * 线程池的任务
 */
public interface Taskable {

    //这里放要执行的任务
    public void doTask();
}
