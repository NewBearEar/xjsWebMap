package servletsrc;

import javasrc.PostgreUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "GetAdcodeHistDataServlet",urlPatterns = "/getAdcodeHistData")
public class GetAdcodeHistDataServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");

        int statusCode = 0;    //用于是否成功，返回给客户端，也可以考虑用枚举类型,0为不匹配，1为成功
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        String countSql = "select t.* from (select '第'||left(''||adcode93,1)||'区域' as name,count(*) from all_city_county group by left(''||adcode93,1)) as t\n" +
                "    order by substr(name,2,1);"; //以adcode93的第一个数字分组统计
        //先写死，用adcode分组统计
        try {
            PreparedStatement ppStatement = conn.prepareStatement(countSql);
            ResultSet rs = ppStatement.executeQuery();
            //获取字段label ，注意label和name的区别
            ResultSetMetaData rsMeta = rs.getMetaData();
            String nameKey =  rsMeta.getColumnLabel(1);
            String countKey = rsMeta.getColumnLabel(2);
            //将数据组合到Echarts要求的格式
            JSONArray nameValJsonArray = new JSONArray();
            JSONArray countValJsonArray = new JSONArray();
            while (rs.next()){
                nameValJsonArray.put(rs.getString(nameKey));
                countValJsonArray.put(rs.getInt(countKey));
            }
            JSONObject dataObj = new JSONObject();
            dataObj.put(nameKey,nameValJsonArray);
            dataObj.put(countKey,countValJsonArray);
            if(! "".equals(dataObj.toString())){
                response.getWriter().println(dataObj.toString());
            }else {
                response.getWriter().println("{}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("{}");
        }

        PostgreUtil.closeDbConn(conn);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
