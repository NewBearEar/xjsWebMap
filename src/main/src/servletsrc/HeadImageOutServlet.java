package servletsrc;

import javasrc.PostgreUtil;
import sun.security.krb5.internal.crypto.RsaMd5CksumType;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "HeadImageOutServlet",urlPatterns = "/headImage")
public class HeadImageOutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //int user_uid = Integer.parseInt(request.getParameter("user_uid"));
        int user_uid = 0;  //记录user_uid
        //提取头像图片
        int headimgId = 0;   //记录img_id
        response.setCharacterEncoding("utf-8");
        InputStream inputStream = null;
        response.setHeader("content-type", "text/html;charset=UTF-8");//通过设置响应头控制浏览器以UTF-8的编码显示数据，如果不加这句话，那么浏览器显示的将是乱码
        ServletOutputStream sopStream = response.getOutputStream();

        HttpSession session = request.getSession(false);  //获取session
        String username = null;
        String password = null;
        if(session!=null){
            username = (String) session.getAttribute("username");
            password = (String) session.getAttribute("password");
            if(username ==null || password==null){
                // 设置默认图片文件
                //String filePath = "D:"+ File.separator + "xWebMap/src/main//webapp/img/defaultHeadimg.jpg";
                String filePath = "D:"+ File.separator +"xWebMap/src/main/webapp/img/noneLogin.jpg";  //绝对路径
                //////////////////////绝对路径不太好
                File file = new File(filePath);
                FileInputStream fileInputStream = new FileInputStream(file);
                //定义字节流缓冲数组
                byte[] defaultBuffer = new byte[1024];
                while(fileInputStream.read(defaultBuffer) != -1){
                    sopStream.write(defaultBuffer);
                }
                //输入完毕清除缓存
                sopStream.flush();
            }
        }else {
            return;
        }

        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "user_info";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);

        //用username和password筛选user_uid
        String uidSql = "select user_uid from local_auth where username=? and password=?";  //通过用户名密码获取user_uid
        PreparedStatement uidPreStatement = null;
        try {
            uidPreStatement = conn.prepareStatement(uidSql);
            uidPreStatement.setString(1,username);
            uidPreStatement.setString(2,password);
            ResultSet uidRs = uidPreStatement.executeQuery();  //查询uid
            while (uidRs.next()){
                user_uid = uidRs.getInt("user_uid");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(0==user_uid){
            //未查询到uid
            sopStream.println("No Image <br>");
            return;
        }

        String imgIdSql = "select img_id from user_profile where user_uid=?";
        PreparedStatement imgIdPreStatement = null;
        try {
            imgIdPreStatement = conn.prepareStatement(imgIdSql);  //通过user_uid查询img_id
            imgIdPreStatement.setInt(1,user_uid);
            ResultSet rs = imgIdPreStatement.executeQuery();
            while (rs.next()){
                headimgId = rs.getInt("img_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(headimgId);
        //对于没有图片的直接返回无图

        if(0==headimgId){
            //无图
            sopStream.println("No Image <br>");
            return;
        }
        try {
            String headimgTableName = "headimg_table";
            inputStream = PostgreUtil.queryImgStream(conn,headimgId,headimgTableName);   //获取图片
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
