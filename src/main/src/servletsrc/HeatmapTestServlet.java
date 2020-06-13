package servletsrc;

import javasrc.PostgreUtil;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "HeatmapTestServlet",urlPatterns = "/heatmapTestData")
public class HeatmapTestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        int statusCode = 0;    //用于描述是否成功，返回给客户端，也可以考虑用枚举类型,0为不匹配，1为成功
        String urlBase = "jdbc:postgresql://47.94.150.127:5432/";
        String dbName = "chn_test";
        String url = urlBase + dbName;
        String user = "postgres";
        String passwd = "xiong123";
        Connection conn = PostgreUtil.getDbConn(url,user,passwd);
        PreparedStatement ppStatement = null;
        /*String heatmapDataSql = "select json_build_object(\n" +
                "                           'type', 'FeatureCollection',\n" +
                "                             'features', json_agg(ST_AsGeoJSON(j.*)::json)\n" +
                "                       )\n" +
                "                    from\n" +
                "                        (select name,value,geom from ((\n" +
                "                            select \"left\"(text(t.adcode93),2) as adcode93_left2,count(*) as value from\n" +
                "                                (\n" +
                "                                    select name,a.adcode93,geom,gid,adclass from all_city_county as a left join\n" +
                "                                    (\n" +
                "                                        select adcode93,adclass from res2_4m) as b on a.adcode93 = b.adcode93\n" +
                "                                ) as t\n" +
                "                            group by \"left\"(text(t.adcode93),2)\n" +
                "                        ) as d right join res1_4m as capital_table on d.adcode93_left2 = \"left\"(text(capital_table.adcode93),2))\n" +
                "                                                         as statistic_fin) as j;"; */   //这个没有归一化
        String heatmapDataSql = "select json_build_object(\n" +
                "    'type', 'FeatureCollection',\n" +
                "    'features', json_agg(ST_AsGeoJSON(j.*)::json)\n" +
                "    )\n" +
                "    from\n" +
                "    (select name,geom,((noneStandard_t.value-min)*1.0/(max-min)) as value from\n" +
                "        (with process_t as (select name,joined_t.value,geom from (\n" +
                "            (\n" +
                "                select \"left\"(text(t.adcode93),2) as adcode93_left2,count(*) as value from\n" +
                "                (\n" +
                "                    select name,a.adcode93,geom,gid,adclass from all_city_county as a left join\n" +
                "                    (\n" +
                "                        select adcode93,adclass from res2_4m\n" +
                "                    ) as b on a.adcode93 = b.adcode93\n" +
                "                ) as t\n" +
                "                group by \"left\"(text(t.adcode93),2)\n" +
                "            ) as d right join res1_4m as capital_table on d.adcode93_left2 = \"left\"(text(capital_table.adcode93),2))\n" +
                "            as joined_t\n" +
                "                            )\n" +
                "        select name,geom,\n" +
                "               process_t.value,\n" +
                "               (select max(process_t.value) from process_t) as max,\n" +
                "               (select min(process_t.value) from process_t) as min\n" +
                "        from process_t\n" +
                "        )\n" +
                "    as noneStandard_t) as j;";        //省会对应省内城市数量归一化数据的geojson
        try{
            ppStatement = conn.prepareStatement(heatmapDataSql);
            ResultSet rs = ppStatement.executeQuery();
            String resultJsonStr = null;
            while(rs.next()){
                resultJsonStr = rs.getString(1);  //只有一行
            }
            if(! "".equals(resultJsonStr)){
                response.getWriter().println(resultJsonStr);
            }else {
                response.getWriter().println("{}");
            }

        }catch (SQLException e){
            e.printStackTrace();
            response.getWriter().println("{}");
            return;
        }
        PostgreUtil.closeDbConn(conn);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
