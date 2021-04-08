package com.yc;

import com.yc.javax.servlet.http.HttpServletRequest;
import com.yc.javax.servlet.http.HttpServletResponse;

/**
 * 资源处理接口
 */
public interface Processor {

    //面向j2ee（sun公司）接口开发，而非实现类
    public void process(HttpServletRequest request, HttpServletResponse response);
}
