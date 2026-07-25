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

import java.util.Map;

/**
 * @author chrelyonly
 * 阿里云云效 devops hook
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook-api")
public class GithubWebHookController {

    /**
     * git hook
     */
    @PostMapping("/github")
    public R github(@RequestBody JSONObject body) {
        JSONObject repository = body.getJSONObject("repository");
        String repoName = repository.getString("name");
        log.info("收到仓库：{}", repoName);
        switch (repoName) {
            case "chrelyonly-music-system-view":
                // 执行前端部署
                MyHttpUtil.sendRequest("http://flow-openapi.aliyun.com/pipeline/webhook/gvOqSLtd1qN2Td9nxlxs",new JSONObject(), Method.POST);
                break;
            default:
                log.info("未知仓库");
        }
        String msg = MyTemplate.githubPushTemplate(body);
        // 打印或发送到机器人
        var bodyData = new JSONObject();
        bodyData.put("text",msg);
        bodyData.put("type","html");
        MyHttpUtil.sendRequest(MyServerConfig.SERVICE_WECHAT_NODE_URL + "/v1/message/text",bodyData, Method.POST);
        return R.success();
    }



}
