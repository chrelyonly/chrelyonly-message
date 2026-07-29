package cn.chrelyonly.message.controller;

import cn.chrelyonly.message.component.R;
import cn.chrelyonly.message.config.MyServerConfig;
import cn.chrelyonly.message.template.MyTemplate;
import cn.chrelyonly.message.util.MyHttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chrelyonly
 * 消息api
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/music-api")
public class MusicApiController {

    /**
     * 发送消息api
     */
    @PostMapping("/message")
    public R message(@RequestBody JSONObject body) {
        String msg = MyTemplate.musicCollectionTemplate(body);
        // 打印或发送到机器人
        var bodyData = new JSONObject();
        bodyData.put("text",msg);
        bodyData.put("type","html");
        MyHttpUtil.sendRequest(MyServerConfig.SERVICE_WECHAT_NODE_URL + "/v1/message/text",bodyData, Method.POST);
        return R.success();
    }
}
