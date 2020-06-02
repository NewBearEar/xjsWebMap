package javasrc;

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
    public static ArrayList<String> parseResult2String(ResultSet resultSet){
        //要求sql用的是json_build_object进行查询得到的ResultSet
        String jsonString = null;
        ArrayList<String> jsonStringList = new ArrayList<String>();
        try {
            //尝试输出json
            //String filePath = "E:\\1.json";
            //FileOutputStream fos = new FileOutputStream(filePath);
            while(resultSet.next()){
                jsonString = resultSet.getString("json_build_object");   //
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
}
