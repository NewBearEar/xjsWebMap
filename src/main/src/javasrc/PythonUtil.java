package javasrc;

import javafx.concurrent.Task;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

@WebServlet(name = "PythonUtil",loadOnStartup = 1,urlPatterns = "/crawler")
public class PythonUtil extends HttpServlet {
    //继承HttpServlet，以便web容器启动时自动调用爬虫,loadOnStartup =1 代表启动容器时自动执行,而且必须要有urlPatterns才会调用init
    public static void main(String[] args){
        //runCrawler();
    }

    @Override
    public void init(){
        System.out.println("Crawler Run!");
        System.out.println("监控启动");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runCrawler();
            }
        }, 0, 60*30*1000);   //定期执行,立即执行+30min一次
        //runCrawler();
    }
    public void runCrawler(){
        System.out.println(System.getProperty("user.dir"));
        //System.out.println(Thread.currentThread().getContextClassLoader().getResource("").getFile());
        //通过相对路径访问
        String projectRealPath = this.getServletContext().getRealPath("/");
        String[] arguments = new String[]{"python",projectRealPath + "WEB-INF/classes/DXY-nCoV-19-Area-crawler/main.py"};
        try {
            //执行python脚本
            Process process = Runtime.getRuntime().exec(arguments);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);  //将python控制台信息输出到server控制台
            }
            in.close();

            int re = process.waitFor();
            if(0==re) {
                System.out.println("Successfully crawled.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Crawler is running!");
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

