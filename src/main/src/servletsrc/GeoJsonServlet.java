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
        String sql = "select json_build_object(\n" +
                "               'type', 'FeatureCollection',\n" +
                "               'features', json_agg(ST_AsGeoJSON(t.*)::json)\n" +
                "           )\n" +
                "from xianch_point as t;";
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
