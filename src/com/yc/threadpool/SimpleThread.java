package com.yc.threadpool;

public class SimpleThread extends Thread{

    //private int count;      //使用权重，1    2   3   4   5   6-->1/1     1/2     1/3     1/4

    private Taskable task;      //任务
    private boolean runningFlag;    //此线程的运行状态

    //构造方法正常情况下，空的即可，如需要加入修饰，可加代码
    public SimpleThread(){
        this.runningFlag=false;
        System.out.println("线程:"+this.getName()+"实例化完成,进入创建态...");
    }

    //获取线程的运行状态
    public boolean isRunning(){
        return runningFlag;
    }


    public synchronized void setRunningFlag(boolean flag){
        this.runningFlag=flag;
        if(this.runningFlag){
            this.notify();
           //当前线程为主线程
            System.out.println(this.getName()+"进入active");
        }
    }

    public void setTask(Taskable task){
        this.task=task;         //绑定任务给当前线程
    }


    @Override
    public synchronized void run() {
        while (true){
            if(runningFlag==false){
                try {
                    this.wait();    //wait和notify方法一定要放在同步锁中
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                if(this.task!=null){
                    this.task.doTask();
                    //执行任务，直到任务完成
                    //break；return；不能写这两个关键字，因为：这样的话，会将当前线程结束
                    setRunningFlag(false);      //任务完成后，将运行态改为false
                    //其实任务完成后，最终的目标都是将当前线程设置wait
                }
            }
        }
    }
}
