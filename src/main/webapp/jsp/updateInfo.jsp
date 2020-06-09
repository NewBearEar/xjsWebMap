<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %><%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/6/2
  Time: 16:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script type="text/javascript" src="../dist/jquery/jquery-3.5.1.js"></script>
    <script type="text/javascript" src="../dist/jquery-ui-1.12.1/jquery-ui.js"></script>
    <link rel="stylesheet" href="../dist/jquery-ui-1.12.1/jquery-ui.css" type="text/css">
    <!--
    <title>updateInfo</title>
    <style>
        body
        {
            background-image: url("../img/defaultImg.jpg");
            background-repeat: no-repeat;
            background-size: cover;
            background-attachment: fixed;
            font-family: Raleway, Open Sans, Droid Sans, Roboto,arial, sans-serif;
        }




        h1, h2, a, p
        {
            color: white;
            font-weight: 100;
        }
        .tinted h1, .tinted h2, .tinted a, .tinted p
        {
            color: black;
            text-shadow: 0 1px 1px rgba(255, 255, 255, 0.2);
        }

        h2
        {
            font-size: 14px;
        }

        p
        {
            margin: 20px;
        }
    </style>
    -->
</head>
<body>
<!--
<div id="box1" class="box blurred-bg tinted" style="display:none;overflow:hidden;padding:3px" >

    <iframe src="showInfo.jsp" frameborder="no" border="0" marginwidth="0" marginheight="0" id="prodcutDetailSrc"  scrolling="0"  width="100%" height="100%"

    ></iframe>
</div>
-->
<%
    response.getWriter().println("{}");
%>

<script type="text/javascript">
    $(function() {
        $( "#box1" ).dialog({
            modal:true
        });
    });
</script>
</body>
</html>
