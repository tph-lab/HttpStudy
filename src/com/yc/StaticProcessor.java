package com.yc;

import com.yc.javax.servlet.http.HttpServletRequest;
import com.yc.javax.servlet.http.HttpServletResponse;

/**
 * 静态处理
 */
public class StaticProcessor implements Processor{


    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) {
        response.sendRedirect();
    }
}
