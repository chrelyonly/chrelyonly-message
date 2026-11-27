//package cn.chrelyonly.message.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GitHubWebhookService {
//    // ==================== 配置区域 ====================
//
//    // GitHub Personal Access Token
//    private static final String GITHUB_TOKEN = "";
//
//    // Webhook 接收地址
//    private static final String WEBHOOK_URL = "https://message.chrelyonly.cn/webhook-api/github";
//
//    // Webhook 密钥：需要与接收端配置一致
//    private static final String WEBHOOK_SECRET = "chrelyonly";
//
//    private static final String API_URL = "https://api.github.com";
//
//    // ==================== HTTP 客户端 ====================
//
//    private static final HttpClient CLIENT = HttpClient.newBuilder()
//            .connectTimeout(Duration.ofSeconds(15))
//            .build();
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    public static void main(String[] args) throws Exception {
//
//        System.out.println("====================================");
//        System.out.println("       Strawberry Hook GitHub");
//        System.out.println("====================================");
//
//        // 1. 获取当前 GitHub 用户
//        JsonNode user = requestJson("GET", "/user", null);
//
//        String username = user.path("login").asText();
//
//        System.out.println("GitHub 用户：" + username);
//
//        // 2. 获取全部个人仓库
//        List<JsonNode> repositories = getRepositories(username);
//
//        System.out.println();
//        System.out.println("共获取到 " + repositories.size() + " 个个人仓库");
//        System.out.println();
//
//        // 4. 批量设置 Webhook
//        System.out.println();
//        System.out.println("开始批量配置 Webhook...");
//        System.out.println();
//
//        int success = 0;
//        int failed = 0;
//        int skipped = 0;
//
//        for (JsonNode repo : repositories) {
//
//            String owner = repo.path("owner").path("login").asText();
//            String repoName = repo.path("name").asText();
//            String fullName = repo.path("full_name").asText();
//
//            System.out.println("处理仓库：" + fullName);
//
//            try {
//                int result = configureWebhook(owner, repoName);
//
//                if (result == 1) {
//                    success++;
//                } else if (result == 0) {
//                    skipped++;
//                }
//
//            } catch (Exception e) {
//                failed++;
//                System.err.println("配置失败：" + fullName);
//                System.err.println("错误原因：" + e.getMessage());
//            }
//        }
//        // 5. 输出统计
//        System.out.println();
//        System.out.println("====================================");
//        System.out.println("             执行完成");
//        System.out.println("====================================");
//        System.out.println("仓库总数：" + repositories.size());
//        System.out.println("成功数量：" + success);
//        System.out.println("跳过数量：" + skipped);
//        System.out.println("失败数量：" + failed);
//        System.out.println("====================================");
//    }
//
//    /**
//     * 获取个人账号拥有的全部仓库
//     */
//    private static List<JsonNode> getRepositories(String username)
//            throws Exception {
//
//        List<JsonNode> repositories = new ArrayList<>();
//
//        int page = 1;
//
//        while (true) {
//
//            String path = "/user/repos"
//                    + "?visibility=all"
//                    + "&affiliation=owner"
//                    + "&per_page=100"
//                    + "&sort=updated"
//                    + "&page=" + page;
//
//            JsonNode result = requestJson("GET", path, null);
//
//            if (!result.isArray() || result.isEmpty()) {
//                break;
//            }
//
//            for (JsonNode repo : result) {
//
//                // 只保留当前个人账号自己拥有的仓库
//                String owner = repo.path("owner")
//                        .path("login").asText();
//
//                if (username.equalsIgnoreCase(owner)) {
//                    repositories.add(repo);
//                }
//            }
//
//            if (result.size() < 100) {
//                break;
//            }
//
//            page++;
//        }
//
//        return repositories;
//    }
//
//    /**
//     * 查询、创建或更新 Webhook
//     *
//     * @return 1 成功，0 跳过
//     */
//    private static int configureWebhook(String owner, String repoName)
//            throws Exception {
//
//        String basePath = "/repos/" + owner + "/" + repoName + "/hooks";
//
//        // 1. 查询当前仓库已有的 Webhook
//        JsonNode hooks = requestJson(
//                "GET",
//                basePath + "?per_page=100",
//                null
//        );
//
//        JsonNode existingHook = null;
//
//        if (hooks.isArray()) {
//            for (JsonNode hook : hooks) {
//
//                String url = hook.path("config")
//                        .path("url").asText();
//
//                if (WEBHOOK_URL.equals(url)) {
//                    existingHook = hook;
//                    break;
//                }
//            }
//        }
//
//        // 2. 构建 Webhook 配置
//        ObjectNode body = MAPPER.createObjectNode();
//
//        body.put("name", "web");
//        body.put("active", true);
//
//        ArrayNode events = body.putArray("events");
//        events.add("push");
//
//        ObjectNode config = body.putObject("config");
//        config.put("url", WEBHOOK_URL);
//        config.put("content_type", "json");
//        config.put("secret", WEBHOOK_SECRET);
//        config.put("insecure_ssl", "0");
//
//        if (existingHook != null) {
//
//            long hookId = existingHook.path("id").asLong();
//
//            System.out.println("  已存在 Webhook，准备更新 ID：" + hookId);
//
//
//            // 3. 已存在则更新
//            requestJson(
//                    "PATCH",
//                    basePath + "/" + hookId,
//                    body
//            );
//
//            System.out.println("  更新成功");
//            return 1;
//
//        } else {
//
//            System.out.println("  不存在 Webhook，准备创建");
//
//
//
//            // 4. 不存在则创建
//            requestJson(
//                    "POST",
//                    basePath,
//                    body
//            );
//
//            System.out.println("  创建成功");
//            return 1;
//        }
//    }
//
//    /**
//     * 通用 GitHub REST API 请求
//     */
//    private static JsonNode requestJson(
//            String method,
//            String path,
//            JsonNode body
//    ) throws Exception {
//
//        HttpRequest.Builder builder = HttpRequest.newBuilder()
//                .uri(URI.create(API_URL + path))
//                .timeout(Duration.ofSeconds(30))
//                .header("Authorization", "Bearer " + GITHUB_TOKEN)
//                .header("Content-Type", "application/json");
//
//        String json = body == null
//                ? ""
//                : MAPPER.writeValueAsString(body);
//
//        switch (method) {
//            case "GET":
//                builder.GET();
//                break;
//
//            case "POST":
//                builder.POST(HttpRequest.BodyPublishers.ofString(json));
//                break;
//
//            case "PATCH":
//                builder.method(
//                        "PATCH",
//                        HttpRequest.BodyPublishers.ofString(json)
//                );
//                break;
//
//            case "DELETE":
//                builder.DELETE();
//                break;
//
//            default:
//                throw new IllegalArgumentException("不支持的 HTTP 方法：" + method);
//        }
//
//        HttpResponse<String> response = CLIENT.send(
//                builder.build(),
//                HttpResponse.BodyHandlers.ofString()
//        );
//
//        int status = response.statusCode();
//
//        if (status < 200 || status >= 300) {
//            throw new RuntimeException(
//                    "GitHub API 请求失败，HTTP " + status
//                            + "，响应：" + response.body()
//            );
//        }
//
//        if (response.body() == null || response.body().isBlank()) {
//            return MAPPER.createObjectNode();
//        }
//
//        return MAPPER.readTree(response.body());
//    }
//}
