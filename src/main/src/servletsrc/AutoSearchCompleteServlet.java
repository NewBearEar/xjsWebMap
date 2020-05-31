package servletsrc;

import javasrc.PostgreUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet(name = "AutoSearchCompleteServlet",urlPatterns = "/autoComplete")
public class AutoSearchCompleteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getParameter("searchTxt"));  //注意大小写

        //尝试连接
        String url = "jdbc:postgresql://47.94.150.127:5432/chn_test";
        String user = "postgres";
        String passwd = "xiong123";
        Connection testConn = PostgreUtil.getDbConn(url,user,passwd);   //连接数据库

        //获取查询文字，由于数据限制，这里支持城市中文名

        String searchTxt = request.getParameter("searchTxt"); //这里为什么不需要ajax的data 的键写成“searchTxt”

        //获取查询
        //查询基础语句,先查询城市中文名试试
        String sqlBase = "select id,name "+
                "from xianch_point ";
        String sql = "";
        if(!"".equals(searchTxt) || searchTxt!=null) {   //判断查询参数是否为空，利用“”字符串常量调用equals方法，避免searchTxt为null调用equals抛出异常，equals会处理参数为null的情况
            //这里不能使用逻辑运算符==或！=比较，因为只比较了引用是否相等，注意String Pool
            sql = sqlBase + "where name like " + "\'%" + searchTxt + "%\'";    //先支持城市名称的模糊查询试试
            System.out.println(sql);   //看看sql语句
            ResultSet queryResult = PostgreUtil.getResultSet(testConn,sql);

        }else {
            response.getWriter().print("{}");   //表示没有查询到并返回空json
            return;
        }



    }
}
