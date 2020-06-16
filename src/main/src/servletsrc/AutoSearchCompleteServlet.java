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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@WebServlet(name = "AutoSearchCompleteServlet",urlPatterns = "/autoComplete")
public class AutoSearchCompleteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //解决中文乱码
        request.setCharacterEncoding("utf-8");
        // System.out.println(request.getParameter("searchTxt"));  //注意大小写

        //尝试连接
        String url = "jdbc:postgresql://47.94.150.127:5432/chn_test";
        String user = "postgres";
        String passwd = "xiong123";
        Connection testConn = PostgreUtil.getDbConn(url,user,passwd);   //连接数据库

        //获取查询文字，由于数据限制，这里支持城市中文名

        String searchTxt = request.getParameter("searchTxt"); //这里为什么不需要ajax的data 的键写成“searchTxt”

        //为response解决乱码
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        //获取查询
        //查询基础语句,先查询城市中文名试试
        String sqlBase = "select gid,name,pinyin "+
                "from all_city_county ";
        String sql = "";
        if((!("".equals(searchTxt)))|| searchTxt != null) {   //判断查询参数是否为空，利用“”字符串常量调用equals方法，避免searchTxt为null调用equals抛出异常，equals会处理参数为null的情况
            //这里不能使用逻辑运算符==或！=比较，因为只比较了引用是否相等，注意String Pool
            sql = sqlBase + "where name like " + "\'" + searchTxt + "%\'";    //先支持城市名称的模糊查询试试  //模糊查询只匹配后面的
            System.out.println(sql);   //看看sql语句
            ResultSet queryResult = PostgreUtil.getResultSet(testConn,sql);
            //数据库记录转JSON，有时间重构成一个函数
            JSONArray jsonArray = new JSONArray();
            ResultSetMetaData resultSetMetaData = null;
            try {
                resultSetMetaData = queryResult.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                while (queryResult.next()){
                    JSONObject jsonObject = new JSONObject();
                    //遍历每一列
                    for (int i = 1;i<=columnCount;i++){  //强烈注意这里的i从1开始，getColumnLabel从1开始
                        String columnLabel = resultSetMetaData.getColumnLabel(i);
                        String key = null;
                        if("name".equals(columnLabel)){
                            key = "label";
                        }else {
                            key = columnLabel;
                        }
                        String value = queryResult.getString(columnLabel);
                        jsonObject.put(key,value);
                    }
                    jsonArray.put(jsonObject);  //向json数组添加jsonObj
                }
                //json转String
                String resultStr = jsonArray.toString();
                response.getWriter().print(resultStr);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else {
            response.getWriter().print("{}");   //表示没有查询到并返回空json
            PostgreUtil.closeDbConn(testConn);
            return;
        }

        PostgreUtil.closeDbConn(testConn);

    }
}
