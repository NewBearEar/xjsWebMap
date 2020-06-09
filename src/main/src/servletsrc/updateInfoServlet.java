package servletsrc;

import javasrc.PostgreUtil;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "updateInfoServlet",urlPatterns = "/update")
public class updateInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //解决中文乱码
        request.setCharacterEncoding("utf-8");
        int statusCode = 0;    //用于描述用户名密码是否匹配，返回给客户端，也可以考虑用枚举类型,0为不匹配，1为成功
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        String updatesql = "update all_city_county set pinyin=?,adcode93=?,name=?,intro=?,image_name=? where gid=?;";  //试试
        String udImageTable = "update image_table set image_name=? where img_id=?";
        PreparedStatement ppStatement = null;
        PreparedStatement udImageppState = null;   //向image_table添加内容的

        String pinyin = request.getParameter("pinyin");
        int adcode93 =  Integer.parseInt(request.getParameter("adcode93"));
        String name = request.getParameter("name");
        String intro = request.getParameter("intro");
        String image_name = request.getParameter("image_name");
        int gid = Integer.parseInt(request.getParameter("gid"));

        int img_id = Integer.parseInt(request.getParameter("img_id"));

        response.setCharacterEncoding("UTF-8");
        int isUpdateSuc = 0;   //成功的判断标准
        int isImageUdSuc = 0;   //成功的判断标准
        //response.setContentType("application/json; charset=utf-8");
        try {
            ppStatement = conn.prepareStatement(updatesql);
            udImageppState = conn.prepareStatement(udImageTable);
            //填充问号
            ppStatement.setString(1,pinyin);
            ppStatement.setInt(2,adcode93);
            ppStatement.setString(3,name);
            ppStatement.setString(4,intro);
            ppStatement.setString(5,image_name);
            ppStatement.setInt(6,gid);

            udImageppState.setString(1,image_name);
            udImageppState.setInt(2,img_id);
            isUpdateSuc = ppStatement.executeUpdate();
            isImageUdSuc = udImageppState.executeUpdate();



        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("{}");
            return;
        }


        JSONObject responObj = new JSONObject();
        if(1==isUpdateSuc && 1==isImageUdSuc){
            //注意JSON字符串格式，必定全是双引号,数字不用
            responObj.put("statusCode",1);
            //response.getWriter().println("{\"statusCode\":1}");  //更新成功  自己写字符串也可
            //System.out.println(responObj.toString());
            //System.out.println("{\"statusCode\":1}");

        }else {
            responObj.put("statusCode",0);
            //response.getWriter().println("{\"statusCode\":0}");   //更新失败
        }
        response.getWriter().println(responObj.toString()); //更新结果

        if(ppStatement!=null){
            try {
                ppStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        PostgreUtil.closeDbConn(conn);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
