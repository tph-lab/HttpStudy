package com.yc;

import com.yc.javax.servlet.ServletResponse;
import com.yc.javax.servlet.http.HttpServletResponse;

import java.io.*;

public class YcHttpServletResponse implements HttpServletResponse {

    private OutputStream oos;
    private YcHttpServletRequest request;

    public YcHttpServletResponse(OutputStream oos, YcHttpServletRequest request) {
        this.oos=oos;
        this.request=request;
    }


    /*拼接响应*/
    public void sendRedirect() {
        //响应协议头
        String responseprotocol=null;
        //响应的资源的内容
        byte []fileContent=null;
        //请求的资源路径   /kaw/index.html
        String uri=request.getRequestURI();
        System.out.println("uri:"+uri);
        //D:\apache-tomcat-9.0.43\webapps
        System.out.println("request.getRealPath():"+request.getRealPath());
        //D:\apache-tomcat-9.0.43\webapps/kaw/index.html
        File f=new File(request.getRealPath(),uri);

        if(!f.exists()){
            //文件不存在，则回送404协议
            File file404=new File(request.getRealPath(),"404.html");
            fileContent=readFile(file404);
            responseprotocol=gen404(file404,fileContent);
        }else{
            //存在文件，则读取文件
            fileContent=readFile(f);
            responseprotocol=gen200(f,fileContent);
        }

        try{
            //以输出流输出数据到客户端
            //先输出响应协议的头部
            this.oos.write(responseprotocol.getBytes());
            this.oos.flush();
            //输出响应实体
           this.oos.write(fileContent);
            this.oos.flush();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(this.oos!=null){
                try {
                    this.oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public OutputStream getOutputStream() {
        return this.oos;
    }

    private String gen200(File f, byte[] fileContent) {
        String result=null;
        ///kaw/index.html
        String uri=this.request.getRequestURI();
        //////////////////////////////////
        int index=uri.lastIndexOf(".");
        if(index>=0){
            index+=1;
        }

        String fileExtension=uri.substring(index);
        if("JPG".equalsIgnoreCase(fileExtension)||"JPEG".equalsIgnoreCase(fileExtension)){
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: image/jpeg\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        }else if("PNG".equalsIgnoreCase(fileExtension)){
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: image/png\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        }else if("json".equalsIgnoreCase(fileExtension)) {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: application/json\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        }else if("css".equalsIgnoreCase(fileExtension)) {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: text/css\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        }else if("js".equalsIgnoreCase(fileExtension)){
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: application/javascript\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        }else{
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: text/html;charset=UTF-8\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        }
            return result;
    }

    private String gen404(File file404, byte[] fileContent) {
        String result=null;
        result= "HTTP/1.1 404\r\nAccept-Ranges: bytes\r\nContent-Type: text/html;charset=UTF-8\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        return result;
    }

    private byte[] readFile(File file) {
        byte []bs=null;
        //byteArrayOutputStream
        InputStream iis=null;
        try{
            iis=new FileInputStream(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        bs=IoUtil.readFromInputStream(iis);
        return bs;
    }


}
