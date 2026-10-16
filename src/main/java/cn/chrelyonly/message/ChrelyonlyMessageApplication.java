package cn.chrelyonly.message;

import cn.chrelyonly.message.config.MyNacosConfig;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ChrelyonlyMessageApplication {
    private static final String appName = "chrelyonly-message";
    public static void main(String[] args) throws NacosException {
        // 获取命令行传递的 spring.profiles.active
        // 默认环境
        String profile = "dev";
        // 从启动参数读取
        for (String arg : args) {
            if (arg.startsWith("--spring.profiles.active=")) {
                profile = arg.substring("--spring.profiles.active=".length());
                break;
            }
        }
        var defaultProps = MyNacosConfig.init(appName,profile);

        SpringApplication app = new SpringApplication(ChrelyonlyMessageApplication.class);
        app.setDefaultProperties(defaultProps);
        app.run(args);
    }

}
