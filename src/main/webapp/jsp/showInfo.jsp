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
<%@ page language="java"
    pageEncoding="UTF-8" %>
<html>
<head>
    <title>showInfo</title>
</head>
<body>
<script type="text/javascript" src="../dist/jquery/jquery-3.5.1.js"></script>
<link rel="stylesheet" href="../css/showInfoStyle.css">
<!-- 在form中设置隐藏控件，用来存储JS中的值 -->
<form name="frmApp" action="a.jsp" id="frmAppId" mothed="post"/>
    <input id="test" type="hidden" name="transdata">
</form>
<div id="loadgif" style="position:absolute;display: none;top:45%;left:45%;z-index: 1001">
    　　<img alt="加载中..." src="../img/loading2.gif"/>
</div>


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
    <form  method="post" onsubmit="return false;">
        <table id="myTable" border="1">
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

                <td class="td-key"> <%=key%> </td>  <!--jsp页面直接插入标签，不在java里面出现html，只能使用普通局部变量，代码块里面的变量无法使用-->

                <%
                    }
                %>
                <!--<td><p>选择需要上传的图片</p></td>-->
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
                        if("type".equals(key) ||"gid".equals(key) ||"fcode".equals(key) ||"img_id".equals(key) ){
                            //为不能编辑的要素
                %>
                            <td class="td-value td-noedit" name=<%=key%>><%=value%></td>
                <%
                        }else {
                %>
                            <td class="td-value td-editable" name=<%=key%>> <%= value%></td>   <!--oninput="change()"-->
                <%
                        }
                    }
                %>
                <!--
                <td>
                    <form id="img-form" enctype="multipart/form-data">
                        <input id="subImgFile" type="file" name="image" formenctype="multipart/form-data">
                        <input id="subImgBtn" type="button" name="subImg" value="上传图片" onclick="uploadImg()">
                    </form>
                </td>
                -->
                <td>
                    <button onclick="deleteInfo()">删除该记录</button>
                </td>
                <td>
                    <button onclick="update()">更新该记录</button>    <!--a href="updateInfo.jsp?gid=<%=gid%>,intro=<%=propertyObj.optString("intro")%>,>"-->
                </td>
            </tr>
            <%
                }

            %>
            <!--
            <tr>
                <td>
                    <button><a href="insert.jsp">插入新纪录</a></button>
                </td>
            </tr>
            -->
        </table>
    </form>

    <div>
        <span><p>请上传你的图片</p></span>
        <form id="img-form" enctype="multipart/form-data" >
            <input id="subImgFile" type="file" name="image" formenctype="multipart/form-data">
            <input id="subImgBtn" type="button" name="subImg" value="上传图片" onclick="uploadImg()">
        </form>
    </div>   <!--目前已知，submit之后ServletFileUpload.isMultipartContent(request)为真，ajax有点问题？-->
<%
    PostgreUtil.closeDbConn(conn);

%>

<script type="text/javascript">
    $(".td-editable").attr("contentEditable", true);   //为所有editable的类设置可编辑

    function deleteInfo(){
        var message = confirm("确定要删除这条记录吗？");  //确认框是否删除
        if(true == message){
            var fieldNames = document.getElementById('myTable').rows[0].cells;  //获取一行htmlcolletion
            var values = document.getElementById('myTable').rows[1].cells;  //获取一行htmlcolletion
            //console.log(fieldNames);  //
            //console.log(values);
            var submitKeyValue = {};
            for (var i=0;i<fieldNames.length-2;i++){    //这里减2为了排除删除更新按钮
                var fieldName = fieldNames[i].innerHTML.replace(/^\s*|\s*$/g, '');   //一列的内容
                //console.log(fieldName);
                var value = values[i].innerHTML.replace(/^\s*|\s*$/g, '');
                submitKeyValue[fieldName] = value;  //添加对象   为啥这个里创建出来就是json格式的js对象呢？ajax需要去掉key的双引号
            }
            console.log(submitKeyValue);
            //console.log(JSON.stringify(submitKeyValue));
            //console.log({'name':"123"});

            //ajax删除数据
            $("#loadgif").show();
            $.ajax({  //AJAX向Servlet发送get请求，请求后台数据库数据
                url: "../deleteInfo", // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
                dataType: "json",   //传输对于jsp页面，只能要求text数据？ 若写json，则即使是200请求成功，也会调用error
                data: submitKeyValue,  //不能是json格式的对象，而必须是非json格式的对象，否则后端无法直接从名字读取
                type: "post",
                success: function (deleteStatus) {  //回调函数，更新map
                    $("#loadgif").hide();
                    console.log(deleteStatus);
                    if($.isEmptyObject(deleteStatus)) {   //判断返回对象是否为空对象
                        alert("删除失败！");
                    }else {
                        if(1==deleteStatus.statusCode){
                            alert("删除成功！");
                            location.reload();
                        }else {
                            alert("删除失败！");
                        }
                    }

                },
                error: function () {  //请求失败的回调方法
                    $("#loadgif").hide();
                    alert("请求失败，请重试");
                }
            });


        }else {

        }
    }


    function update() {
        var fieldNames = document.getElementById('myTable').rows[0].cells;  //获取一行htmlcolletion
        var values = document.getElementById('myTable').rows[1].cells;  //获取一行htmlcolletion
        //console.log(fieldNames);  //
        //console.log(values);
        var submitKeyValue = {};
        for (var i=0;i<fieldNames.length-2;i++){    //这里减2为了排除删除更新按钮
            var fieldName = fieldNames[i].innerHTML;   //一列的内容
            //console.log(fieldName);
            var value = values[i].innerHTML;
            submitKeyValue[fieldName] = value;  //添加对象   为啥这个里创建出来就是json格式的js对象呢？ajax需要去掉key的双引号
        }
        console.log(submitKeyValue);
        //console.log(JSON.stringify(submitKeyValue));
        //console.log({'name':"123"});

        var keyValueNodquo = dislodgeDquo(submitKeyValue);  //无key双引号的js对象
        console.log(keyValueNodquo);

        $("#loadgif").show();
        $.ajax({  //AJAX向Servlet发送get请求，请求后台数据库数据
            url: "../update", // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
            dataType: "json",   //传输对于jsp页面，只能要求text数据？ 若写json，则即使是200请求成功，也会调用error
            contentType: "application/x-www-form-urlencoded",
            data: keyValueNodquo,  //不能是json格式的对象，而必须是非json格式的对象，否则后端无法直接从名字读取
            type: "get",
            success: function (updateStatus) {  //回调函数，更新map
                $("#loadgif").hide();
                console.log(updateStatus);
                if($.isEmptyObject(updateStatus)) {   //判断返回对象是否为空对象
                    alert("更新失败！");
                }else {
                    if(1==updateStatus.statusCode){
                        alert("更新成功！");
                    }else {
                        alert("更新失败！");
                    }
                }

            },
            error: function () {  //请求失败的回调方法
                $("#loadgif").hide();
                alert("请求失败，请重试");
            }
        });

        //alert(x[0].innerHTML);  //每一列
    }
    function dislodgeDquo(jsonObj) {//去除js对象中key的双引号
        // 待处理的json对象
        // json的值将被临时储存在这个变量中
        var keyValue = "";
        // 处理好的json字符串
        var jsonStr = "";
        for (var key in jsonObj) {
            //console.log(jsonObj[key].replace(/^\s*|\s*$/g, ''));
            keyValue += key + ':"'+ jsonObj[key].replace(/^\s*|\s*$/g, '') + '",';   //去除字符串两头空格
        }
        // 去除最后一个逗号
        keyValue = keyValue.substring(0,keyValue.length - 1);
        jsonStr = "{" + keyValue + "}";
        console.log(jsonStr);
        var jsonObjNodquo = eval('('+jsonStr+')');  //key无双引号的js对象  JSON.parse只能处理严格的JSON格式，而eval更宽泛
        return jsonObjNodquo;
    }

    function uploadImg() {

        var fieldNames = document.getElementById('myTable').rows[0].cells;  //获取一行htmlcolletion
        var values = document.getElementById('myTable').rows[1].cells;  //获取一行htmlcolletion
        var imageNameKey = "";
        var imageNameVal = "";
        //console.log(fieldNames);  //
        //console.log(values);
        var imgkey = $("#subImgFile").attr("name");
        var imgval = $("#subImgFile").val();
        var imgfile = $("#subImgFile")[0].files[0];  //获取file
        if(""==imgval || null==imgfile){
            alert("请先选择上传的图片");
            return;
        }

        var submitKeyValue = {};
        //var form=document.querySelector("#img-form");
        //将获得的表单元素作为参数，对formData进行初始化
        //console.log(form);
        var subData = new FormData($("#img-form")[0]);   //根据表单创建对象
        //var subData = new FormData();
        //subData.append(imgkey,imgfile); //向表单对象添加内容
        console.log(subData.get("image"));

        for (var i=0;i<fieldNames.length-2;i++){
            var fieldName = fieldNames[i].innerHTML.replace(/^\s*|\s*$/g, '');   //一列的内容  去除两头空格  innerHTML会多出一个空格？
            //console.log(fieldName);
            var value = values[i].innerHTML.replace(/^\s*|\s*$/g, '');
            //console.log(fieldName);
            //console.log(value);
            if("image_name"==fieldName || "gid"==fieldName){
                submitKeyValue[fieldName] = value;  //只添加图片名
                subData.append(fieldName,value);  //向表单对象添加图片名
            }
        }
        submitKeyValue[imgkey] = imgval;
        console.log(subData.get("gid"));
        //console.log(subData.keys());

        $("#loadgif").show();
        //ajax上传图片
        $.ajax({  //AJAX向Servlet发送get请求，请求后台数据库数据
            url: "../uploadImg", // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
            dataType: "json",   //传输对于jsp页面，只能要求text数据？ 若写json，则即使是200请求成功，也会调用error   //并不是，而是因为请求json就必须满足json格式（全部字符双引号，数字无引号）
            data: subData,  //必须是FormData 保证ServletFileUpload.isMultipartContent(request)为true
            type: "post",
            processData:false,//*必须
            contentType:false,//*必须
            success: function (uploadImgStatus) {  //回调函数，更新map
                $("#loadgif").hide();
                console.log(uploadImgStatus);
                if($.isEmptyObject(uploadImgStatus)) {   //判断返回对象是否为空对象
                    alert("更新失败！");
                }else {
                    if(1==uploadImgStatus.statusCode){
                        alert("更新成功！");
                    }else {
                        alert("更新失败！");
                    }
                }

            },
            error: function () {  //请求失败的回调方法
                $("#loadgif").hide();
                alert("请求失败，请重试");
            }
        });

    }
</script>

</body>
</html>
