package servletsrc;

import javasrc.JsonUtil;
import javasrc.PostgreUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "loginServlet", urlPatterns = "/login")
public class loginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        int statusCode = 0;    //用于描述用户名密码是否匹配，返回给客户端，也可以考虑用枚举类型,0为不匹配，1为成功
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "user_info";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        //Map<String,String[]> parameterMap = request.getParameterMap();  //获取键值对

        //request.getSession(true)意思是:如果session存在,则返回该session,否则创建一个新的session.
        //request.getSession(false)意思是:如果session存在,则返回该session,否则返回null.
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(10*60);  //设置session超时时间，单位是秒
        //Set<String> keys = parameterMap.keySet();  //获取键集合

        String usernameKey = "username";
        String passwordKey = "password";

        String sessUsernameVal = (String) session.getAttribute(usernameKey);
        String sessPasswordVal = (String) session.getAttribute(passwordKey);
        JSONObject responJsonObj = new JSONObject();  //最终返回的JSONOBJ
        if(sessUsernameVal!=null && sessPasswordVal!=null){

            //通过session判断是否为空
            System.out.println("使用session");
            String uidsql = "select user_uid from local_auth where "+usernameKey+"="+"\'"+sessUsernameVal+"\'"+" and "+passwordKey+"="+"\'"+sessPasswordVal+"\'";
            ResultSet uidRSet = PostgreUtil.getResultSet(conn,uidsql);
            int user_uid=0;
            try {
                while (uidRSet.next()) {
                    user_uid = uidRSet.getInt("user_uid"); //仅有一行
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            responJsonObj.put("statusCode",1);
            responJsonObj =  getUserProfile(conn,user_uid,responJsonObj);
            response.getWriter().println(responJsonObj.toString());
            PostgreUtil.closeDbConn(conn);   //关闭数据库连接
            return;
        }


        String usernameVal = request.getParameter(usernameKey);
        String passwordVal = request.getParameter(passwordKey);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        if(usernameVal==null || passwordVal==null){
            response.getWriter().println("{}");
            return;
        }
        //


        String tableName = "local_auth";
        //查询基础语句
        String sqlBase = "select user_uid,password \n" +
                "from "+tableName;
        String sql1 =sqlBase + " where " + usernameKey + "=\'" +usernameVal+"\'";
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ResultSet queryResult = PostgreUtil.getResultSet(conn,sql1);
        int user_uid = 0;
        String foundPassword = null;  //从数据库中找到的密码，用于比较

        if (queryResult == null){
            response.getWriter().println("{}");
            return;
        }else {
            try{
                while (queryResult.next()){
                    user_uid = queryResult.getInt("user_uid");
                    foundPassword = queryResult.getString("password");
                    if(passwordVal.equals(foundPassword)){
                        statusCode = 1;
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            responJsonObj.put("statusCode",statusCode);
            long nowtimestramp = System.currentTimeMillis();
            if(statusCode == 1){  //1表示登录成功
                //记录到session当中
                session.setAttribute(usernameKey,usernameVal);
                session.setAttribute(passwordKey,passwordVal);

                //这里写入一个记录上次登录时间
                //String sql2 = "update user_profile set last_login_time=" + nowtimestramp + " where user_uid=" + user_uid;
                //获取详细信息
                responJsonObj = getUserProfile(conn,user_uid,responJsonObj);
            }
            response.getWriter().println(responJsonObj.toString());
        }
        PostgreUtil.closeDbConn(conn);   //关闭数据库连接

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    protected JSONObject getUserProfile(Connection conn,int user_uid,JSONObject responJsonObj){
        //这里获取详细信息
        String sql3 = "select row_to_json(t.*) from (select * from user_profile where user_uid="+user_uid+") as t;";
        ResultSet userResult = PostgreUtil.getResultSet(conn,sql3);
        try{
            while (userResult.next()){
                String userProfile = userResult.getString(1);  //这样的uid必定至多一条记录
                JSONObject userProfileObj = new JSONObject(userProfile);
                responJsonObj = JsonUtil.combineJsonObj(responJsonObj,userProfileObj);
            }
            return responJsonObj;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return responJsonObj;
        }
    }

}
