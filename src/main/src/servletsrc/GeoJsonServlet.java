package servletsrc;

import javasrc.PostgreUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "GeoJsonServlet",urlPatterns = "/getGeoJson")  //urlPatterns带 /，但AJAX请求不带
public class GeoJsonServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //连接数据库，获取json
        //尝试连接
        String url = "jdbc:postgresql://47.94.150.127:5432/chn_test";
        String user = "postgres";
        String passwd = "xiong123";
        Connection testConn = PostgreUtil.getDbConn(url,user,passwd);   //连接数据库

        //获取查询文字，由于数据限制，这里支持城市中文名
        String searchTxt = request.getParameter("searchTxt");
        System.out.println(searchTxt);
        //获取查询
        //查询基础语句
        String sqlBase = "select json_build_object(\n" +
                "               'type', 'FeatureCollection',\n" +
                "               'features', json_agg(ST_AsGeoJSON(t.*)::json)\n" +
                "           )\n" +
                "from xianch_point as t ";
        String sql = "";
        if(!"".equals(searchTxt)) {   //判断查询参数是否为空，利用“”字符串常量调用equals方法，避免searchTxt为null调用equals抛出异常，equals会处理参数为null的情况
            //这里不能使用逻辑运算符==或！=比较，因为只比较了引用是否相等，注意String Pool
            sql = sqlBase + "where name=" + "\'" + searchTxt + "\'";    //先支持城市名称查询试试
        }else {
            sql = sqlBase;
        }
        ResultSet queryResult = PostgreUtil.getResultSet(testConn,sql);
        ArrayList<String> jsonStringList = PostgreUtil.parseResult2String(queryResult);
        //System.out.println(jsonStringList.get(0));
        String jsonStringTest = jsonStringList.get(0);  //只有一个对象，取第一个对象
        //输出流生成之前，设置数据集编码方式
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        //生成输出流
        PrintWriter out = response.getWriter();
        out.print(jsonStringTest);
        out.flush();
        out.close();   //关闭输出流

        PostgreUtil.closeDbConn(testConn);   //关闭数据库连接
    }

}
