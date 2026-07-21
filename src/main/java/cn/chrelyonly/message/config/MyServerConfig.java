package cn.chrelyonly.message.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 11725
 */
@Configuration
@Slf4j
public class MyServerConfig {
	public static String ACTIVE_TYPE;
	@Value("${spring.profiles.active:}")
	public void activeProfile(String active) {
		log.info("获取激活配置文件");
		MyServerConfig.ACTIVE_TYPE = active;
	}
	public static boolean isDev() {
		return "dev".equals(ACTIVE_TYPE);
	}
    public static String SERVICE_WECHAT_NODE_URL;
	@Value("${open.wechatNode.api.serviceUrl}")
	public void setServiceMusicUrl(String serviceUrl) {
		log.info("初始化微信消息接口插件");
		MyServerConfig.SERVICE_WECHAT_NODE_URL = serviceUrl;
	}

}
