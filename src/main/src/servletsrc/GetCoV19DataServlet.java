package servletsrc;

import javasrc.PostgreUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "GetCoV19DataServlet",urlPatterns = "/getCoV19Data")
public class GetCoV19DataServlet extends HttpServlet {
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
        String sql = "select json_build_object('type', 'FeatureCollection',\n" +
                "                        'features', json_agg(ST_AsGeoJSON(t.*)::json)\n" +
                "    ) from\n" +
                "    (select name,currentconfirmedcount,confirmedcount,suspectedcount,curedcount,deadcount,adcode93,updatetime,provincename,geom\n" +
                "        from all_city_county as a inner join cov_19_area as b on a.name = b.cityname) as t;";
        try{
            ppStatement = conn.prepareStatement(sql);
            ResultSet rs = ppStatement.executeQuery();
            String resultJsonStr = null;
            while(rs.next()){
                resultJsonStr = rs.getString(1);  //只有一行
            }
            if(! "".equals(resultJsonStr)){
                response.getWriter().println(resultJsonStr);
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
