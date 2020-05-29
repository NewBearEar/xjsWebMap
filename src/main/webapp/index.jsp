<%@ page import="java.sql.Connection" %>
<%@ page import="javasrc.PostgreUtil" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>

<html>
<body>
<%
    //尝试在jsp里面连接数据库

    //连接数据库，获取json
    //尝试连接
    String url = "jdbc:postgresql://47.94.150.127:5432/chn_test";
    String user = "postgres";
    String passwd = "xiong123";
    Connection testConn = PostgreUtil.getDbConn(url,user,passwd);   //连接数据库

    //获取查询文字，由于数据限制，这里支持城市中文名
    String searchTxt = request.getParameter("searchTxt");
    System.out.println(searchTxt);
    //获取查询
    //查询基础语句
    String sqlBase = "select json_build_object(\n" +
            "               'type', 'FeatureCollection',\n" +
            "               'features', json_agg(ST_AsGeoJSON(t.*)::json)\n" +
            "           )\n" +
            "from xianch_point as t ";
    ResultSet queryResult = PostgreUtil.getResultSet(testConn,sqlBase);
    ArrayList<String> jsonStringList = PostgreUtil.parseResult2String(queryResult);
    //System.out.println(jsonStringList.get(0));
    String jsonStringTest = jsonStringList.get(0);  //只有一个对象，取第一个对象
    out.println(jsonStringTest);
    PostgreUtil.closeDbConn(testConn);   //关闭数据库连接

%>
</body>
</html>
