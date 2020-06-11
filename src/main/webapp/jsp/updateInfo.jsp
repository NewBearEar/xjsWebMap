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

        .blurred-bg
        {
            background-image: url("../img/defaultImg.jpg");
            background-repeat: no-repeat;
            -moz-background-size: cover;
            -o-background-size: cover;
            -webkit-background-size: cover;
            background-size: cover;
            background-attachment: fixed;
        }
        .blurred-bg.tinted
        {
            background-image: -moz-linear-gradient(90deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), url("../img/defaultImg.jpg");
            background-image: -webkit-linear-gradient(90deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), url("../img/defaultImg.jpg");
            background-image: linear-gradient(0deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), url("../img/defaultImg.jpg");
        }
        .blurred-bg.shaded
        {
            background-image: -moz-linear-gradient(90deg, rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.2)), url("../img/defaultImg.jpg");
            background-image: -webkit-linear-gradient(90deg, rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.2)), url("../img/defaultImg.jpg");
            background-image: linear-gradient(0deg, rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.2)), url("../img/defaultImg.jpg");
        }

        .box
        {
            width: 500px;
            height: 300px;
            left: -webkit-calc( 50% - 250px );
            top: 20%;
            position: absolute;
            border-radius: 5px;
            -moz-box-shadow: 0 20px 30px rgba(0, 0, 0, 0.6);
            -webkit-box-shadow: 0 20px 30px rgba(0, 0, 0, 0.6);
            box-shadow: 0 20px 30px rgba(0, 0, 0, 0.6);
            border: 1px solid rgba(255, 255, 255, 0.3);
            padding: 20px;
            text-align: center;
            -moz-box-sizing: border-box;
            -webkit-box-sizing: border-box;
            box-sizing: border-box;
            text-shadow: 0 1px 1px rgba(0, 0, 0, 0.4);
        }
        .box:active
        {
            cursor: move;
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

</head>
<body>
<!--
<div id="box" class="box blurred-bg tinted" style="display:none;overflow:hidden;padding:3px" >

    <iframe src="showInfo.jsp" frameborder="no" border="0" marginwidth="0" marginheight="0" id="prodcutDetailSrc"  scrolling="0"  width="100%" height="100%"

    ></iframe>

</div>-->
<div id="box" class="box blurred-bg tinted">
    <h1>
        Blurred Background</h1>
    <h2>
        By <a href="http://www.w2bc.com">Ariona, Rian</a></h2>
    <p>
        Drag this box to move around</p>
</div>

<%
    //response.getWriter().println("{}");
%>

<script type="text/javascript">
    $(function() {
        var box = $( "#box" );

        var x, y;//存储div的坐标
        var isDrop = false;//移动状态的判断鼠标按下才能移动

        box[0].onmousedown = function(e) {
            var e = e || window.event;//要用event这个对象来获取鼠标的位置
            x = e.clientX - box.offsetLeft;
            y = e.clientY - box.offsetTop;
            isDrop = true;//设为true表示可以移动
        }
        document.onmousemove = function(e) {
            //是否为可移动状态
            if(isDrop) {
                var e = e || window.event;

                var moveX = e.clientX - x;//得到距离左边移动距离
                var moveY = e.clientY - y;//得到距离上边移动距离

                box[0].style.left = moveX + "px";
                box[0].style.top = moveY + "px";
            }else{
                return ;
            }

        }
        document.onmouseup = function() {
            isDrop = false;//设置为false不可移动
        }

    });

</script>
</body>
</html>
