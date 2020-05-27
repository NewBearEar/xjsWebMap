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
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "GeoJsonServlet",urlPatterns = "/getGeoJson")
public class GeoJsonServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //连接数据库，获取json
        //尝试连接

        PrintWriter out = response.getWriter();
        out.print("hello");

        out.close();
    }

}
