package cn.chrelyonly.message.controller;

import cn.chrelyonly.message.component.R;
import cn.chrelyonly.message.config.MyServerConfig;
import cn.chrelyonly.message.service.DevopsHookServiceImpl;
import cn.chrelyonly.message.util.MyHttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {
    @GetMapping("/test")
    public R test(){
        var body = new JSONObject();
        body.put("text","测试喵~🐱");
        MyHttpUtil.sendRequest(MyServerConfig.SERVICE_WECHAT_NODE_URL + "/v1/message/text",body, Method.POST);
        return R.data("1");
    }

    private final DevopsHookServiceImpl devopsHookService;
    @GetMapping("/test1")
    public R test1(){
        var body = new JSONObject();
        body.put("text","测试喵~🐱");
        return R.data(devopsHookService.list());
    }

}
