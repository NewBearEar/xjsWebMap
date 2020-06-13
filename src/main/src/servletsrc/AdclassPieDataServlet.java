package servletsrc;

import javasrc.PostgreUtil;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AdclassPieDataServlet",urlPatterns = "/getAdclassData")
public class AdclassPieDataServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        int statusCode = 0;    //用于描述是否成功，返回给客户端，也可以考虑用枚举类型,0为不匹配，1为成功
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        PreparedStatement ppStatement = null;
        String countSql = "select (case when adclass = 1 then '首都'\n" +
                "             when adclass = 2 then '省会城市'\n" +
                "             when adclass = 3 then '其他地级市'\n" +
                "             when adclass = 9 then '港澳地区' else '未知级别' end\n" +
                "    ) as name ,count(*) from\n" +
                "    (select * from all_city_county as a left join (select adcode93,adclass from res2_4m) as b on a.adcode93 = b.adcode93) as t\n" +
                "    where t.adclass is not null\n" +
                "    group by t.adclass;";
        try{
            ppStatement = conn.prepareStatement(countSql);
            ResultSet rs = ppStatement.executeQuery();
            ResultSetMetaData rsMeta = ppStatement.getMetaData();
            JSONArray dataArray = new JSONArray();
            JSONArray colLabelArray = new JSONArray();
            for(int i=1;i<=rsMeta.getColumnCount();i++){
                colLabelArray.put(rsMeta.getColumnLabel(i));
            }
            dataArray.put(colLabelArray);
            while(rs.next()){
                String name = rs.getString("name");
                int count = rs.getInt("count");
                JSONArray colValArray = new JSONArray();
                colValArray.put(name);
                colValArray.put(count);
                dataArray.put(colValArray);
            }
            if(! "".equals(dataArray.toString())){
                response.getWriter().println(dataArray.toString());
            }else {
                response.getWriter().println("{}");
            }

        }catch (SQLException e){
            e.printStackTrace();
            response.getWriter().println("{}");
            return;
        }
        PostgreUtil.closeDbConn(conn);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
