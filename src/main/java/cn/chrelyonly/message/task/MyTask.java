
package cn.chrelyonly.message.task;

import cn.chrelyonly.message.config.MyServerConfig;
import cn.chrelyonly.message.util.MyHttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MyTask {

    private static final int WARNING_DAYS = 30;
    private static final int TIMEOUT = 10000;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final List<String> DOMAINS = List.of(
            "https://api.chrelyonly.cn",
            "https://www.chrelyonly.cn",
            "https://dns.chrelyonly.cn",
            "https://docker.chrelyonly.cn"
    );

    @Scheduled(cron = "01 01 09 * * *")
    public void checkCertificates() {
        log.info("开始检查 HTTPS 证书，共 {} 个域名", DOMAINS.size());

        List<CertificateResult> results = new ArrayList<>();

        for (String domain : DOMAINS) {
            results.add(checkCertificate(domain));
        }

        long normal = results.stream().filter(r -> "正常".equals(r.status)).count();
        long warning = results.stream().filter(r -> "即将过期".equals(r.status)).count();
        long expired = results.stream().filter(r -> "已过期".equals(r.status)).count();
        long failed = results.stream().filter(r -> "检查失败".equals(r.status)).count();

        StringBuilder body = new StringBuilder();
        body.append("🍓 证书监控 | ")
                .append(LocalDateTime.now().format(FORMATTER)).append("\n");
        body.append("共 ").append(results.size())
                .append(" 个 | ✅").append(normal)
                .append(" ⚠️").append(warning)
                .append(" ❌").append(expired)
                .append(" 🚫").append(failed).append("\n\n");

        for (CertificateResult result : results) {
            String icon = switch (result.status) {
                case "正常" -> "✅";
                case "即将过期" -> "⚠️";
                case "已过期" -> "❌";
                default -> "🚫";
            };

            body.append(icon).append(" ")
                    .append(result.domain.replace("https://", ""))
                    .append(" | ");

            if (result.remainingDays != null) {
                body.append("剩余 ").append(result.remainingDays).append(" 天");
            } else {
                body.append("剩余未知");
            }

            body.append(" | 到期 ")
                    .append(result.expireTime)
                    .append("\n");

            if (!"正常".equals(result.status)) {
                body.append("   ").append(result.message).append("\n");
            }
        }

        String msg = body.toString();

        JSONObject bodyData = new JSONObject();
        bodyData.put("text", msg);
        bodyData.put("type", "text");
//        bodyData.put("wechatRoomId", "43910458776@chatroom");
        MyHttpUtil.sendRequest(MyServerConfig.SERVICE_WECHAT_NODE_URL + "/v1/message/text",bodyData, Method.POST);
        log.info("HTTPS 证书检查完成：正常 {}，即将过期 {}，已过期 {}，失败 {}",
                normal, warning, expired, failed);
    }


    /**
     * 检查单个 HTTPS 证书并返回结果
     */
    private CertificateResult checkCertificate(String url) {
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestMethod("HEAD");
            connection.connect();

            Certificate[] certificates = connection.getServerCertificates();
            X509Certificate certificate = null;

            for (Certificate item : certificates) {
                if (item instanceof X509Certificate x509
                        && x509.getBasicConstraints() < 0) {
                    certificate = x509;
                    break;
                }
            }

            if (certificate == null) {
                return new CertificateResult(url, "检查失败",
                        "-", null, "未找到服务器证书");
            }

            Instant now = Instant.now();
            Instant expire = certificate.getNotAfter().toInstant();
            long remainingDays = Duration.between(now, expire).toDays();

            String expireTime = LocalDateTime.ofInstant(
                    expire, ZoneId.systemDefault()
            ).format(FORMATTER);

            // 检查证书当前是否有效
            if (now.isBefore(certificate.getNotBefore().toInstant())) {
                return new CertificateResult(url, "检查失败",
                        expireTime, remainingDays, "证书尚未生效");
            }

            if (remainingDays < 0) {
                return new CertificateResult(url, "已过期",
                        expireTime, remainingDays, "证书已经过期");
            }

            if (remainingDays <= WARNING_DAYS) {
                return new CertificateResult(url, "即将过期",
                        expireTime, remainingDays, "请及时续期");
            }

            return new CertificateResult(url, "正常",
                    expireTime, remainingDays, "证书有效");

        } catch (Exception e) {
            log.error("证书检查失败：{}，原因：{}", url, e.getMessage());
            return new CertificateResult(url, "检查失败",
                    "-", null, e.getMessage() == null
                    ? e.getClass().getSimpleName() : e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 防止域名或异常信息中的特殊字符破坏 HTML 表格
     */
    private static String escapeHtml(String value) {
        if (value == null) {
            return "-";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 单个域名的证书检查结果
     */
    private static class CertificateResult {
        private final String domain;
        private final String status;
        private final String expireTime;
        private final Long remainingDays;
        private final String message;

        private CertificateResult(String domain, String status,
                                  String expireTime, Long remainingDays,
                                  String message) {
            this.domain = domain;
            this.status = status;
            this.expireTime = expireTime;
            this.remainingDays = remainingDays;
            this.message = message;
        }
    }
}
