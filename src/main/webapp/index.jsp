<%@ page import="java.sql.Connection" %>
<%@ page import="javasrc.PostgreUtil" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>

<html>
<body>
<h1>欢迎使用,即将为您跳转到WebGIS页面</h1>
<%
    // 重定向到新地址
    // 3秒定时跳转
    String site = new String("olMap.html");
    response.setStatus(response.SC_MOVED_TEMPORARILY);
    //response.setHeader("Location", site);
    response.setHeader("Refresh","3;URL="+site);
%>
</body>
</html>
