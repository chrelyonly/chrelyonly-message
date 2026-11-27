package cn.chrelyonly.message.controller;

import cn.chrelyonly.message.component.R;
import cn.chrelyonly.message.service.DevopsHookServiceImpl;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chrelyonly
 * 阿里云云效 devops hook
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook-api")
public class GithubWebHookController {

    private final DevopsHookServiceImpl devopsHookService;
    /**
     * git hook
     */
    @PostMapping("/github")
    public R github(@RequestBody JSONObject body) {
        JSONObject repository = body.getJSONObject("repository");
        String repoName = repository.getString("name");
        log.info("收到仓库：{}", repoName);
//        DevopsHook devopsHook = devopsHookService
//                .lambdaQuery()
//                .eq(DevopsHook::getRepoName, repoName)
//                .one();
//        if (devopsHook == null) {
//            // 打印或发送到机器人
//            var bodyData = new JSONObject();
//            bodyData.put("text",repoName + "-未配置流水线自动化hook");
//            MyHttpUtil.sendRequest(MyServerConfig.SERVICE_WECHAT_NODE_URL + "/v1/message/text",bodyData, Method.POST);
//            return R.success();
//        }
//                // 执行前端部署
//        MyHttpUtil.sendRequest(devopsHook.getUrl(),new JSONObject(), Method.POST);
//        String msg = MyTemplate.githubPushTemplate(body);
//        // 打印或发送到机器人
//        var bodyData = new JSONObject();
//        bodyData.put("text",msg);
//        bodyData.put("type","html");
//        MyHttpUtil.sendRequest(MyServerConfig.SERVICE_WECHAT_NODE_URL + "/v1/message/text",bodyData, Method.POST);
        return R.success();
    }



}
