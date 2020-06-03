<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="javasrc.PostgreUtil" %>
<%@ page import="javafx.geometry.Pos" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Enumeration" %><%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/6/2
  Time: 13:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"
    pageEncoding="UTF-8" %>
<html>
<head>
    <title>showInfo</title>
</head>
<body>
    <%!
        public static final String DBDRIVER = "org.postgresql.Driver";
        public static final String url = "jdbc:postgresql://47.94.150.127:5432/chn_test";
        public static final String user = "postgres";
        public static final String passwd = "xiong123";
    %>
    <%

        request.setCharacterEncoding("utf-8");

        Connection conn = PostgreUtil.getDbConn(url,user,passwd);   //连接数据库
        Enumeration enumSearchKey = request.getParameterNames();
        String searchKey = null;
        String searchTxt = null;
        //只获取第一个参数名
        if(enumSearchKey.hasMoreElements()){
            searchKey = (String)enumSearchKey.nextElement();
            searchTxt = request.getParameter(searchKey); //获取请求查询的参数值
        }

        System.out.println(searchTxt);
        String propertyArrayStr = null; //直接调用封装方法获取属性json String
        try {
            propertyArrayStr = PostgreUtil.getPropertyArrayStr(conn,searchKey,searchTxt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if("未能查询到属性信息".equals(propertyArrayStr)){
            out.println(propertyArrayStr);
            return;
        }
    %>
    <form action="updateInfo.jsp" method="post" onsubmit="">
        <table border="1">
            <tr>
                <%
                    //解析keys动态生成第一行
                    JSONArray propertyArray = new JSONArray(propertyArrayStr);
                    Iterator<String> itKeys = propertyArray.getJSONObject(0).keys();  //获取key迭代器
                    String key = null;
                    while (itKeys.hasNext()) {
                        key = itKeys.next();
                        //out.println("<td>" + key + "</td>");  //java里面出现html不太好，但是先试试
                %>

                <td> <%=key%> </td>  <!--jsp页面直接插入标签，不在java里面出现html，只能使用普通局部变量，代码块里面的变量无法使用-->

                <%
                    }
                %>
                <td>删除</td>
                <td>更新</td>
            </tr>
            <%
                for(int row=0;row<propertyArray.length();row++){
                    //外层循环遍历每一行
                    JSONObject propertyObj = propertyArray.getJSONObject(row);
                    int gid = propertyObj.getInt("gid");  //记录数据当前行的gid

            %>
            <tr>
                <%
                    //内层循环重新遍历每一列
                    itKeys = propertyObj.keys();  //获取key迭代器
                    key = null;
                    String value = null;
                    while (itKeys.hasNext()) {
                        key = itKeys.next();
                        value = propertyObj.optString(key);
                %>
                <td><%= value%></td>
                <%
                    }
                %>
                <td>
                    <button><a href="delete.jsp?id=<%=gid%>">删除该记录</a></button>
                </td>
                <td>
                    <button><a href="update.jsp?id=<%=gid%>">更新该记录</a></button>
                </td>
            </tr>
            <%
                }

            %>
            <tr>
                <td>
                    <button><a href="insert.jsp">插入新纪录</a></button>
                </td>
            </tr>
        </table>
    </form>
<%
    PostgreUtil.closeDbConn(conn);

%>
</body>
</html>
