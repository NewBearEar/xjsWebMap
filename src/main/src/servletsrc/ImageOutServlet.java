package servletsrc;

import javasrc.PostgreUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

@WebServlet(name = "ImageOutServlet",urlPatterns = "/imageOut")
public class ImageOutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int gid = Integer.parseInt(request.getParameter("gid"));
        //用数据库中城市的gid确定图片，以后还可以将图片单独存储到一个数据表，然后在其他表里存储图片id
        int imgId = 0;   //记录img_id
        response.setCharacterEncoding("utf-8");

        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        String imgIdSql = "select img_id from all_city_county where gid=?";
        PreparedStatement imgIdPreStatement = null;
        try {
            imgIdPreStatement = conn.prepareStatement(imgIdSql);  //通过gid查询img_id
            imgIdPreStatement.setInt(1,gid);
            ResultSet rs = imgIdPreStatement.executeQuery();
            while (rs.next()){
                imgId = rs.getInt("img_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(imgId);
        //对于没有图片的直接返回无图
        InputStream inputStream = null;
        response.setHeader("content-type", "text/html;charset=UTF-8");//通过设置响应头控制浏览器以UTF-8的编码显示数据，如果不加这句话，那么浏览器显示的将是乱码
        ServletOutputStream sopStream = response.getOutputStream();
        if(0==imgId){
            //无图
            sopStream.println("No Image <br>");
            return;
        }
        try {
            inputStream = PostgreUtil.queryImgStream(conn,imgId);   //获取图片
            if(inputStream == null){
                //无图片时默认输出
                sopStream.println("No Image<br>");
            }else {
                //定义字节流缓冲数组
                byte[] buffer = new byte[1024];
                while(inputStream.read(buffer) != -1){
                    sopStream.write(buffer);
                }
                //输入完毕清除缓存
                sopStream.flush();
                sopStream.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PostgreUtil.closeDbConn(conn);

    }
}
