package com.yc.javax.servlet;

public interface Servlet {

    public void destory();

    public void service(ServletRequest req,ServletResponse res);

}
