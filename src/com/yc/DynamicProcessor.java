package com.yc;

import com.yc.javax.servlet.Servlet;
import com.yc.javax.servlet.http.HttpServlet;
import com.yc.javax.servlet.http.HttpServletRequest;
import com.yc.javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;


/**
 * 动态处理类
 */
public class DynamicProcessor implements Processor{

    public static Map<String,Object> map=new HashMap<>();
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) {



        //1.取出uri中servlet的名字    -->HelloServlet的字节码文件
        String uri=request.getUri();
        int slash=uri.lastIndexOf("/")+1;
        int slash2=uri.indexOf("/")+1;
        int dot=uri.lastIndexOf(".");
        String path=uri.substring(slash2,slash);
        System.out.println("path:"+path);
        String serletName=uri.substring(slash,dot);

        //2.加载  URLClassLoadler
        String basePath=request.getRealPath();
        //basePath:C:\Users\Lenovo\Desktop\httpserver\webapps\
        System.out.println("basePath+path:"+basePath+path);
        //访问本地文件用的是file协议
        try{
            URL url=new URL("file",null,basePath+path);
            URL[] urls=new URL[]{url};
            //指定加载器扫描的地址/kaw
            URLClassLoader ucl=new URLClassLoader(urls);
            //加载字节码文件kaw/HelloServlet
            //一定要保证取名（类名和请求的xxx.action）一致
            //ucl.loadClass要是一个全类名
            Class cls=ucl.loadClass(serletName);
            // if(cls.getSuperclass().getName().equals("HttpServlet"))
            //serletName:HelloServlet
            System.out.println("serletName:"+serletName);
            //3.反射  -->Class.newInstance-->构造
            Servlet servlet=null;
            if(map.get(serletName)==null){
                System.out.println("Servlet未实例化、、、、、、、、、、、、、、");
                servlet=(Servlet) cls.newInstance();
                map.put(serletName,servlet);

            }else{
                System.out.println("servlet使用之前的。。。。。。。。。。。。。。。");
                servlet= (Servlet) map.get(serletName);
            }

            //4.按servlet的生命周期调用
            if(servlet!=null&&servlet instanceof HttpServlet){
                HttpServlet ser= (HttpServlet) servlet;
                ser.init();
                ser.service(request,response);
                //关服务器掉destory
            }
        }catch (Exception e){
            e.printStackTrace();
            //获取报错信息
            String content=e.getMessage();
            String protocal=gen500(null,content.getBytes());
            try (OutputStream oos=response.getOutputStream();){
                oos.write(protocal.getBytes());
                oos.flush();
                oos.write(content.getBytes());
                oos.flush();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }


        //init-->service-->
    }
    private String gen500(File file, byte[] fileContent) {
        String result=null;
        result= "HTTP/1.1 500 Internel Server error\r\nAccept-Ranges: bytes\r\nContent-Type: text/html;charset=UTF-8\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        return result;
    }
}
