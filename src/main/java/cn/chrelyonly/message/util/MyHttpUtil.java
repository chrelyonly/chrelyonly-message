package cn.chrelyonly.message.util;

import cn.chrelyonly.message.config.MyServerConfig;
import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONObject;

import java.io.InputStream;

/**
 * 发送请求
 * @author 11725
 */
public class MyHttpUtil {
    /**
     * 发送请求
     * @param apiUrl  /xxx
     */
    public static JSONObject sendRequest(String apiUrl, JSONObject body, Method method) {
        // 发送请求并获取响应
        HttpRequest httpRequest;
        if (method == Method.GET){
            httpRequest = HttpRequest.get(apiUrl);
        }else{
            httpRequest = HttpRequest.post(apiUrl).body(body.toJSONString());
        }
        if (MyServerConfig.isDev()){
            httpRequest.setHttpProxy("192.168.10.47",18890);
        }
        try(HttpResponse execute = httpRequest
                .execute()){
            String stringRes = execute.body();
            return JSONObject.parseObject(stringRes);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(){{
                put("msg", "请求失败");
                put("code", 500);
                put("ret", 500);
            }};
        }
    }
}
