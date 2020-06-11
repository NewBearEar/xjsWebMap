package servletsrc;

import javasrc.PostgreUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "UploadImageServlet",urlPatterns = "/uploadImg")
public class UploadImageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection testConn = PostgreUtil.getDbConn(url,user,passwd);  //连接数据库
        String sql = "insert into image_table(image_name,image) values (?,?) RETURNING img_id";
        //String getImgidSql = "select img_id from image_table where img_id= @@IDENTITY";
        PreparedStatement ps = null;
        PreparedStatement psgetImgid = null;
        int gid = 0;
        int img_id = 0;

        //表单
        String myFile = "";
        //核心Api
        //核心Api
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        //判断是否是muitipart/form-data类型
        if(!ServletFileUpload.isMultipartContent(request)) {
            //resp.getWriter().println("表单的enctype属性不是multipart/form-data类型");
            System.out.println("表单的enctype属性不是multipart/form-data类型");
            return;
        }
        System.out.println("multipart/form-data");
        //设置单个文件上传大小 8M
        fileUpload.setFileSizeMax(8*1024*1024);
        //设置总上传文件大小(有时候一次性上传多个文件，需要有一个上限,此处为60M)
        fileUpload.setSizeMax(60*1024*1024);
        //设置上传监听器[参数为自定义的监听器]
        // fileUpload.setProgressListener(new ListenerUploadProgress());
        //解析请求
        try {
            ps = testConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //psgetImgid = testConn.prepareStatement(getImgidSql);  //获取img_id

            List<FileItem> parseRequest = fileUpload.parseRequest(request);
            //获取数据
            for (FileItem fileItem : parseRequest) {
                //判断数据类型是不是普通的form表单字段
                if(!fileItem.isFormField()) {
                    //上传文件
                    String fileName = fileItem.getName();
                    InputStream fileStream = fileItem.getInputStream();
                    //向数据库写

                    ps.setBinaryStream(2, fileStream);
                    //psgetImgid.setBinaryStream(1,fileStream);

                    //定义保存的父路径（服务器部署相对的绝对路径）
                    String parentDir = this.getServletContext().getRealPath("/uploadImg");
                    //使用UUID+文件名的方式，避免文件重名
                    String realFileName = UUID.randomUUID().toString()+"-"+fileName;
                    //创建要保存的文件
                    File file = new File(parentDir,realFileName);
                    //判断文件夹是否存在
                    if(!file.getParentFile().exists()) {
                        //创建文件夹[多级文件夹]file.madir是创建单一文件夹
                        file.getParentFile().mkdirs();
                    }
                    //创建输出流
                    OutputStream out = new FileOutputStream(file);
                    //创建字节缓存
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    //一次读取1kb(1024byte),返回-1表明读取完毕
                    while((len = fileStream.read(buffer))!=-1) {
                        //一次写入1kb(1024byte)
                        out.write(buffer,0, len);
                    }
                    System.out.println(parentDir);
                    System.out.println(realFileName);
                    myFile = realFileName;
                    //冲刷流资源
                    out.flush();
                    //关闭流
                    out.close();
                    fileStream.close();
                }else {
                    //普通字段  //字段名
                    String fieldName = fileItem.getFieldName();
                    //字段值中文乱码
                    String fieldValue = fileItem.getString("UTF-8");
                    if("image_name".equals(fieldName)){
                        ps.setString(1,fieldValue);
                    }else if("gid".equals(fieldName)) {
                        gid = Integer.parseInt(fieldValue);
                    }

                    System.out.println(fieldName+":"+fieldValue);
                }
            }
            // 执行SQL
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            //ResultSet rs =  psgetImgid.executeQuery();
            while(rs.next()){
                img_id = rs.getInt(1);
            }
            ps.close();
        } catch (FileUploadException e) {
            e.printStackTrace();
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

        //更新城市对应的图片id
        String udImgidSql = "update all_city_county set img_id=? where gid=?";
        PreparedStatement psUdImgidbygid = null;
        try {
            psUdImgidbygid = testConn.prepareStatement(udImgidSql);
            psUdImgidbygid.setInt(1,img_id);
            psUdImgidbygid.setInt(2,gid);
            psUdImgidbygid.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("{}");
            return;
        }

        response.getWriter().print("{\"statusCode\":1}"); //成功返回1


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
