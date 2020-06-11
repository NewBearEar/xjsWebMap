package servletsrc;

import javasrc.PostgreUtil;
import org.omg.CORBA.INTERNAL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "DeleteInfoServlet",urlPatterns = "/deleteInfo")
public class DeleteInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        int statusCode = 0;    //用于描述用户名密码是否匹配，返回给客户端，也可以考虑用枚举类型,0为不匹配，1为成功
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        PrintWriter out = response.getWriter();
        int gid = Integer.parseInt(request.getParameter("gid"));

        String deletesql = "delete from all_city_county where gid=?;";  //试试
        try {
            PreparedStatement ppStatement = conn.prepareStatement(deletesql);
            ppStatement.setInt(1,gid);
            int isDeleteSuc = ppStatement.executeUpdate();
            if(1==isDeleteSuc){  //成功
                out.println("{\"statusCode\":1}");
            }else {
                out.println("{\"statusCode\":0}");
            }
        } catch (SQLException e) {
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
