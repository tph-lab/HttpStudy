package com.yc;


import com.yc.threadpool.Taskable;
import com.yc.threadpool.ThreadPoolManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Main {

    //日志的创建，根据全路径名创建logger
    private static Logger logger=Logger.getLogger(Main.class.getName());

    static {
//        System.out.println(System.getProperties());
//        System.out.println("\n\n" + System.getProperty("user.dir"));
//        System.out.println(File.separator);
        String userDir = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "log4j.properties";
        //System.out.println(userDir);
        //PropertyConfigurator 是 log4j框架提供的配置类
        //用来配置该框架的配置文件的位置，一般情况下，只要框架需要有读取配置文件，一般都有设置路径的类
        PropertyConfigurator.configure(userDir);
    }

    public static void main(String[] args) {
        ThreadPoolManager tpm=null;
        //解析xml文件
        Map<String,String> xmlPros=initXml();
        if("true".equalsIgnoreCase(xmlPros.get("threadpool"))){
            tpm=new ThreadPoolManager();
        }
        try(ServerSocket ss=new ServerSocket(Integer.parseInt(xmlPros.get("port")));){
            logger.info(ss.getInetAddress()+"正常启动，监听"+ss.getLocalPort()+"端口");
            while(true){
                Socket s=ss.accept();
                logger.info(s.getRemoteSocketAddress()+"连接到服务器");
                if("true".equalsIgnoreCase(xmlPros.get("threadpool"))){
                    tpm.process(new NetTask(s));
                }else{
                    //TODO:考虑线程和普通线程
                    Thread t=new Thread(new NetTask(s));
                    t.setDaemon(true);
                    t.start();
                }

            }
        }catch (IOException e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }


    /**
     * 1.读取xml文件
     *      原始:dom,sax
     *      框架:dom4j,jdom
     *      解析出server.xml的port,threadpool,shutdown_port存入map
     *
     * 2.项目路径
     *
     */
    private static Map<String,String> initXml(){
        Map<String,String> xmlPros=new HashMap<String, String>();
        //读取xml
        //dom，sax
        //***  dom4j  jdom
        //J2EE中自带的xml解析器（dom方式），javax.xml.parsers.DocumentBuilderFactory
        //通过DocumentBuilderFactory创建xml解释器
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        try{
            //通过解释器创建一个可以加载并生成Xml的DocumentBuilder
            DocumentBuilder builder=factory.newDocumentBuilder();
            //TODO:发布后是否有路径问题
            //通过DocumentBuilder加载并生成一棵xml树，document对象的实例
            Document doc=builder.parse(System.getProperty("user.dir")+"/conf/server.xml");
            //javascript中的dom的解析方式
            //通过Document可以遍历这棵树，并读取相应节点中的内容
            NodeList nl=doc.getElementsByTagName("Server");

            for(int i=0;i<nl.getLength();i++){
                Element node= (Element) nl.item(i);
                String value=node.getAttribute("shutdown");
                if("SHUTDOWN".equals(value)){
                    String port=node.getAttribute("port");
                    xmlPros.put("shutdown_port",port);
                }

                //node是server节点，取它下面的一个conneter节点
                NodeList nls=node.getElementsByTagName("Connector");
                for(int j=0;j<nls.getLength();j++){
                    Element node2= (Element) nls.item(j);
                    String port="9090";
                    String threadpool="false";
                    if(node2.getAttribute("port")!=null){
                        port=node2.getAttribute("port");
                    }
                    if(node2.getAttribute("threadpool")!=null){
                        threadpool=node2.getAttribute("threadpool");
                    }
                    xmlPros.put("port",port);
                    xmlPros.put("threadpool",threadpool);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return xmlPros;
    }


}
