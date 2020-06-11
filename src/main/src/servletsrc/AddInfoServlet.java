package servletsrc;

import javasrc.PostgreUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AddInfoServlet",urlPatterns = "/addInfo")
public class AddInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        String name = request.getParameter("name");
        String pinyin = request.getParameter("pinyin");
        String intro = request.getParameter("intro");
        int adcode93 = Integer.parseInt(request.getParameter("adcode93"));
        String longitudeStr = request.getParameter("longitude");
        String latitudeStr = request.getParameter("latitude");
        String type = request.getParameter("type");

        int statusCode = 0;    //用于描述用户名密码是否匹配，返回给客户端，也可以考虑用枚举类型,0为不匹配，1为成功
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        int isAddInfoSuc = 0;  //信息添加状态
        int isAddCoordinateSuc = 0;  //坐标添加状态
        int gid = 0; //记录自增的gid号
        String addInfoSql = "insert into all_city_county(name,pinyin,adcode93,intro) values(?,?,?,?) RETURNING gid;";
        String addCoorSql = "update all_city_county set geom=ST_GeomFromText(?,4326) where gid=?;";

        PreparedStatement addInfoStatement = null;
        PreparedStatement addCoorStatement= null;
        try {
            addInfoStatement = conn.prepareStatement(addInfoSql, Statement.RETURN_GENERATED_KEYS);
            addCoorStatement = conn.prepareStatement(addCoorSql);
            //为info添加变量
            addInfoStatement.setString(1,name);
            addInfoStatement.setString(2,pinyin);
            addInfoStatement.setInt(3,adcode93);
            addInfoStatement.setString(4,intro);

            isAddInfoSuc = addInfoStatement.executeUpdate();
            ResultSet rs = addInfoStatement.getGeneratedKeys(); //获取存储的自增变量gid
            while (rs.next()){
                gid = rs.getInt(1);
            }
            if(gid!=0){
                String wktStr = type + "(" + longitudeStr +" " +latitudeStr +")" ; //一定要注意中间有个空格
                addCoorStatement.setString(1,wktStr);
                addCoorStatement.setInt(2,gid);
                isAddCoordinateSuc = addCoorStatement.executeUpdate();
            }
            if(1==isAddCoordinateSuc && 1==isAddInfoSuc){
                //都成功
                response.getWriter().println("{\"statusCode\":1," +
                        "\"gid\":"+gid+"}");  //成功  自己写字符串也可
            }else{
                response.getWriter().println("{\"statusCode\":0}");  //失败
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
