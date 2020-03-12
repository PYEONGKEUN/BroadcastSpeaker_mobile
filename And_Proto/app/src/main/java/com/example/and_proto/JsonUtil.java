package com.example.and_proto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class JsonUtil {

    /*
    * Map을 Json으로 변환
    * @param map Map<String,Object>
    * @return JSONObject
    * */
    public static JSONObject getJsonStringFromMap(Map<String, Object> map ) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for( Map.Entry<String, Object> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            jsonObject.put(key, value);
        }

        return jsonObject;
    }
}
