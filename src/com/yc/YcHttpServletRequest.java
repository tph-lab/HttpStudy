package com.yc;


import com.yc.javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

//实现标准j2ee
public class YcHttpServletRequest implements HttpServletRequest {

    private Socket socket;
    private String realPath;        //D:\apche-tomcat-9.0.43\webapps
    private String requestURI;      // /test1/a.jsp
    private String requestURL;      // http://localhost:8080/test1/a.jsp
    private String quertString;     //name=zy&age=20

    private InputStream iis;



    private String method;  //请求方式
    private Map<String,String> headers=new ConcurrentHashMap<String, String>();     //头域
    private String uri;    //请求的资源地址
    private String protocol;    //协议的版本
    private Map<String,String[]> parameterMap=new ConcurrentHashMap<String, String[]>();






    public InputStream getIis() {
        return iis;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }



    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public String[] getParameterValues(String key){
        return this.parameterMap.get(key);
    }

    public YcHttpServletRequest(InputStream iis, Socket socket) {
        this.iis = iis;
        this.socket=socket;
    }


    public String getParameter(String key){
        String []values=getParameterValues(key);
        if(values!=null&&values.length>=0){
            return values[0];
        }
        return null;
    }

    public String getHeader(String headerName){
        if(headers!=null){
            return headers.get(headerName);
        }
        return null;
    }


    public void parse(){
        //1.从iis中取出协议
        String protocolContent=readProtocolFromInputStream();
        //2.解析协议
        parseProtocol(protocolContent);

    }


    //解析：请求行、请求头域、实体     存信息到headers及其它的域中
    private void parseProtocol(String protocolContent) {
        if(protocolContent==null||"".equals(protocolContent)){
            //TODO:注意此时通过response生产404页面
            return;
        }

        //字符串分割类:对字符串自动以     空格,回车,换行来分割
        //此处指定以\r\n来分割
        StringTokenizer st=new StringTokenizer(protocolContent,"\r\n");
        //标识第一行
        int index=0;
        while (st.hasMoreElements()){//按行循环
            //取每一行
            String line=st.nextToken();
            if(index==0){   //如果是第一行
                String []first=line.split(" ");
                this.method=first[0];
                this.uri=first[1];
                //TODO:还要做进一步的拆分，以防止有参数的情况/kaw/index.html?name=a&age=20
                this.protocol=first[2];

                //解析出realPath
                this.realPath=System.getProperty("user.dir")+ File.separator+"webapps"+File.separator+this.uri.split("/")[0];
                this.requestURI=this.uri.split("\\?")[0];
                if("HTTP/1.1".equals(this.protocol)||"HTTP/1.0".equals(this.protocol)){
                    this.requestURL="http:/"+this.socket.getLocalSocketAddress()+this.requestURI;
                }
                if(this.uri.indexOf("?")>=0){
                    this.quertString=this.uri.split("\\?")[1];
                    String []params=this.quertString.split("&");
                    for (int i=0;i<params.length;i++){
                        String []pv=params[i].split("=");
                        //name=tph,zy&age=20
                        if(pv[1].indexOf(",")>=0){
                            String[] values=pv[1].split(",");
                            this.parameterMap.put(pv[0],values);
                        }else{
                            this.parameterMap.put(pv[0],new String[]{pv[1]});
                        }
                    }
                }


            }else if("".equals(line)){
                if ("POST".equals(this.method)){
                    //以下的数据都是请求实体部分的数据了，比如post的参数
                    parseParams(st);
                }
                break;
            }else{
                String []heads=line.split(":");
                headers.put(heads[0],heads[1]);
            }
            index++;

        }

    }

    private void parseParams(StringTokenizer st) {
        while (st.hasMoreElements()){
            String line=st.nextToken();
            String[] params=line.split("&");
            for(int i=0;i<params.length;i++){
                String []pv=params[i].split("=");
                //name=tph,zy&age=20
                if(pv[1].indexOf(",")>=0){
                    String []values=pv[1].split(",");
                    this.parameterMap.put(pv[0],values);
                }else{
                    this.parameterMap.put(pv[0],new String[]{pv[1]});
                }
            }
        }
    }

    //从iis中取出协议
    private String readProtocolFromInputStream() {
        String protocolContent=null;
//        byte[] bs=IoUtil.readFromInputStream(this.iis);
//        protocolContent=new String(bs);
//        System.out.println(protocolContent);


        StringBuffer sb=new StringBuffer(1024*30);
        int length=-1;
        byte[] bs=new byte[1024*30];
        try {
            /*while((length=this.iis.read(bs))!=-1) {
                for (int i = 0; i < length; i++) {
                    sb.append((char) bs[i]);
                }
            }*/
            ///////////////////////
            length = this.iis.read(bs);

        }catch (Exception e){
            e.printStackTrace();
        }
        //////////////////////////////////
        for (int i = 0; i < length; i++) {
            sb.append((char) bs[i]);
        }

        protocolContent=sb.toString();
        return protocolContent;

    }


    public Socket getSocket() {
        return socket;
    }

    @Override
    public String getRealPath() {
        return realPath;
    }


    public String getRequestURI() {
        return requestURI;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getQuertString() {
        return quertString;
    }

}
