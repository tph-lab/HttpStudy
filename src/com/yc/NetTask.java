package com.yc;

import com.yc.threadpool.Taskable;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetTask implements Runnable, Taskable {

    private Socket s;
    private InputStream iis;
    private OutputStream oos;

    public NetTask(Socket s){
        this.s=s;
    }

    @Override
    public void run() {
        //取出流  socket
        try {
            this.iis=this.s.getInputStream();
            this.oos=this.s.getOutputStream();
            //request功能就是解析请求行，请求头域，请求实体
            YcHttpServletRequest request=new YcHttpServletRequest(this.iis,this.s);
            request.parse();
            //为什么在response中要有一个requst呢？因为响应时要知道请求中请求的资源地址
            YcHttpServletResponse response=new YcHttpServletResponse(this.oos,request);

            //判断request中资源到底是静态还是动态的
            Processor processor=null;
            if (request.getRequestURI().endsWith(".action")){
                //动态资源
                processor=new DynamicProcessor();
            }else{
                //静态资源
                processor=new StaticProcessor();
            }
            processor.process(request,response);



            //Connection:keep-alive    保持连接     循环
            this.s.close();     //http协议，无状态
        }catch (Exception e){
            e.printStackTrace();


        }

    }

    @Override
    public void doTask() {
        run();
    }
}
