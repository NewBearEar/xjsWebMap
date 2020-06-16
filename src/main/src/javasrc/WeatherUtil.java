package javasrc;

import com.sun.deploy.net.HttpResponse;
import sun.net.www.http.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherUtil {
    public static String getWeatherInfo(String url) throws IOException {
        URL urlObj = new URL(url);
        InputStream inputStream = urlObj.openStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                inputStream,"GBK"));//"UTF-8"防止中文乱码
        String completeStr="",s;
        while ((s = in.readLine()) != null) {
            completeStr += s;
            System.out.println(completeStr);
        }
        return completeStr;
    }


    public static void main(String[] args) throws IOException {
        String url = "http://wthrcdn.etouch.cn/weather_mini?city=北京";
        String weatherInfo = getWeatherInfo(url);
        System.out.println(weatherInfo);
    }
}
