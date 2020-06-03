package javasrc;

import org.json.JSONObject;

import java.util.Iterator;

public class JsonUtil {
    public static JSONObject combineJsonObj(JSONObject srcObj,JSONObject addObj){
        Iterator<String> itKyes = addObj.keys();  //迭代器
        String key,value;
        while(itKyes.hasNext()){
            key = itKyes.next();
            value = addObj.optString(key);
            srcObj.put(key,value);
        }
        return srcObj;
    }

}
