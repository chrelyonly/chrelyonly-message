package cn.chrelyonly.message.controller;

import cn.chrelyonly.message.component.R;
import cn.chrelyonly.message.config.MyServerConfig;
import cn.chrelyonly.message.template.MyTemplate;
import cn.chrelyonly.message.util.MyHttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author chrelyonly
 * 阿里云云效 devops hook
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ali-devops-api")
public class AliDevopsController {

    /**
     * devops构建回调
     */
    @PostMapping("/devopsBuildCallBack")
    public R devopsBuildCallBack(@RequestBody JSONObject body) {
        String msg = MyTemplate.aliDevopsTemplate(body);
        // 打印或发送到机器人
        var bodyData = new JSONObject();
        body.put("text",msg);
        body.put("type","html");
        MyHttpUtil.sendRequest(MyServerConfig.SERVICE_WECHAT_NODE_URL + "/v1/message/text",bodyData, Method.POST);
        return R.success();
    }
}
