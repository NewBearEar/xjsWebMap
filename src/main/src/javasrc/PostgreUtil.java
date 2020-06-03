package javasrc;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class PostgreUtil extends DatabaseUtil { //postgis连接工具类
    public static void main(String[] args) throws IOException {
        //尝试连接，工具类不需要实现main
        /*String url = "jdbc:postgresql://47.94.150.127:5432/chn_test";
        String user = "postgres";
        String passwd = "xiong123";
        Connection testConn = getDbConn(url,user,passwd);
        String sql = "select json_build_object(\n" +
                "               'type', 'FeatureCollection',\n" +
                "               'features', json_agg(ST_AsGeoJSON(t.*)::json)\n" +
                "           )\n" +
                "from xianch_point as t;";
        ResultSet queryResult = getResultSet(testConn,sql);
        ArrayList<String> jsonStringList = parseResult2String(queryResult);
        System.out.println(jsonStringList.get(0));
        closeDbConn(testConn);*/

        //试试上传图片
        String url = "jdbc:postgresql://47.94.150.127:5432/chn_test";
        String user = "postgres";
        String passwd = "xiong123";
        Connection testConn = getDbConn(url,user,passwd);
        String sql = "insert into all_city_county(image_name,image) values(?,?)";
        PreparedStatement ps = null;

        try {
            ps = testConn.prepareStatement(sql);

            // 设置图片名称
            ps.setString(1, "defaultImage");

            // 设置图片文件
            File file = new File("D:\\xWebMap/src/main/webapp/img/defaultImg.jpg");
            FileInputStream inputStream = new FileInputStream(file);
            ps.setBinaryStream(2, inputStream, (int) file.length());

            // 执行SQL
            ps.execute();
            ps.close();

            System.out.println(" 已上传");

        } catch (SQLException e) {
            System.err.println("SQL " + sql + " 错误");
        } catch (FileNotFoundException e) {
            System.err.println("图片 "+ " 没有找到");
        }

    }


    public static Connection getDbConn(String url,String user,String passwd){
        Connection connection = null;

        try{

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url,user,passwd);
            System.out.println("是否成功连接pg数据库"+connection);

        }catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return connection;

    }

    public static ResultSet getResultSet(Connection connection,String sql){
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
           /* //测试
            while(resultSet.next()){
                String jsonName = resultSet.getString("json_build_object");
                System.out.println(jsonName);
            }*/
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return resultSet;
    }
    //public static
    public static ArrayList<String> parseResult2String(ResultSet resultSet){//写死了很难受
        //要求sql用的是json_build_object进行查询得到的ResultSet
        String jsonString = null;
        ArrayList<String> jsonStringList = new ArrayList<String>();
        try {
            //尝试输出json
            //String filePath = "E:\\1.json";
            //FileOutputStream fos = new FileOutputStream(filePath);
            while(resultSet.next()){
                jsonString = resultSet.getString(1);   //json_build_object  //列索引从1开始
                jsonStringList.add(jsonString);
                //System.out.println(jsonString);
            }
            //fos.write(jsonString.getBytes());
            //fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return jsonStringList;
        }
    }

    public static void closeDbConn(Connection conn){
        try {
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getPropertyArrayStr(Connection connection,String searchKey,String searchValue) throws SQLException {
        //精准查询返回所有属性，包括类型和坐标
        String tableName = "all_city_county";
        //查询基础语句
        String sqlBase = "select json_agg(ST_AsGeoJSON(t.*)::json)\n" +
                "from "+tableName+" as t ";
        String sql = "";
       // boolean k = !("".equals(searchTxt)); //为什么在if判断中加上这个或判断就恒为真了？

        if(searchValue!=null ) {
            //判断查询参数是否为空，利用“”字符串常量调用equals方法，避免searchTxt为null调用equals抛出异常，equals会处理参数为null的情况
            //这里不能使用逻辑运算符==或！=比较，因为只比较了引用是否相等，注意String Pool
            sql = sqlBase + "where "+searchKey+" = " + "\'" + searchValue + "\'";    //先支持城市名称的模糊查询试试  //模糊查询只匹配后面的
            System.out.println(sql);   //看看sql语句
        }else{
            return "未能查询到属性信息";  //
        }
        ResultSet queryResult = PostgreUtil.getResultSet(connection, sql);
        String jsonArrayString = null;

        //只有一行
        try {
            while (queryResult.next()) {
                jsonArrayString = queryResult.getString(1);  //只有一行一列的ArrayString转String,索引从1开始
            }
            if(jsonArrayString == null){
                return "未能查询到属性信息";
            }
        }catch (Exception e){
            e.printStackTrace();;
            throw e;
        }
        JSONArray jsonArray = new JSONArray(jsonArrayString);
        JSONObject jsonObject = null;
        JSONObject attrObject = null;
        JSONArray attrArray = new JSONArray();
        for(int i = 0;i<jsonArray.length();i++){
            jsonObject = jsonArray.getJSONObject(i);
            JSONObject srcObj = jsonObject.getJSONObject("properties");
            JSONObject addObj = jsonObject.getJSONObject("geometry");
            //合并geometry和properties作为输出的属性
            attrObject = JsonUtil.combineJsonObj(srcObj,addObj);
            attrArray.put(attrObject); //添加到输出attr数组
        }
        String attrArrayStr = attrArray.toString();
        //System.out.println(attrArrayStr);
        return  attrArrayStr;

    }
    public static String getPropertyArrayStr(Connection connection,String searchNameValue) throws SQLException {
        //重载，只能按照字段名等于 name 的来查询
        String keyEqualsName = "name";
        return getPropertyArrayStr(connection,keyEqualsName,searchNameValue);
    }
}

