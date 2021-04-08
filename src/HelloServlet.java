import com.yc.YcHttpServletRequest;
import com.yc.YcHttpServletResponse;
import com.yc.javax.servlet.http.HttpServlet;
import com.yc.javax.servlet.http.HttpServletRequest;
import com.yc.javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;


public class HelloServlet extends HttpServlet {

    public HelloServlet(){
        System.out.println("HelloServlet 的构造方法");
    }



    @Override
    public void init() {
        super.init();
        System.out.println("HelloServlet 的init方法");
    }

    @Override
    public void destory() {
        super.destory();
        System.out.println("HelloServlet 的destory方法");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        super.service(req, resp);
        System.out.println("HelloServlet 的service方法");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        super.doPost(req, resp);
        System.out.println("HelloServlet 的doPost方法");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        super.doGet(req, resp);
        System.out.println("HelloServlet 的doGet方法");
        String name=req.getParameter("uname");
        String password=req.getParameter("upassword");
        System.out.println(name+password);
        String result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: application/javascript\r\nContent-Length:0 " + "\r\n\r\n";
        try (OutputStream oos=resp.getOutputStream();){
            oos.write(result.getBytes());
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
