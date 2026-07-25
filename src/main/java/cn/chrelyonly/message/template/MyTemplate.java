package cn.chrelyonly.message.template;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyTemplate {

    public static String aliDevopsTemplate(JSONObject body){
        log.info("接收到DevOps回调: {}", body.toJSONString());

        // 提取任务信息
        JSONObject task = body.getJSONObject("task");
        String pipelineName = task.getString("pipelineName");
        String stageName = task.getString("stageName");
        String taskName = task.getString("taskName");
        String buildNumber = task.getString("buildNumber");
        String statusName = task.getString("statusName");
        String pipelineUrl = task.getString("pipelineUrl");
        String executorName = task.getString("executorName");

        // 提交信息
        JSONArray sources = body.getJSONArray("sources");
        JSONObject source = sources != null && !sources.isEmpty() ? sources.getJSONObject(0) : new JSONObject();
        JSONObject data = source.getJSONObject("data");
        String branch = data != null ? data.getString("branch") : "-";
        JSONArray commitMsg = data != null ? data.getJSONArray("commitMsg") : new JSONArray();

        // 判断状态颜色卡片类名
        String statusClass = "status-running"; // 默认蓝
        if ("SUCCEEDED".equalsIgnoreCase(statusName) || "SUCCESS".equalsIgnoreCase(statusName) || "成功".equals(statusName)) {
            statusClass = "status-success"; // 绿
        } else if ("FAILED".equalsIgnoreCase(statusName) || "FAIL".equalsIgnoreCase(statusName) || "失败".equals(statusName)) {
            statusClass = "status-failed"; // 红
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">")
                .append("<style>")
                .append("* { margin:0; padding:0; box-sizing:border-box; }")
                .append("body { background:#eef3f8; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif; padding:30px; display:flex; justify-content:center; }")
                .append(".card { width:700px; background:#fff; border-radius:24px; overflow:hidden; box-shadow:0 15px 35px rgba(0,0,0,0.08); }")

                // Header
                .append(".header { padding:28px 30px; background:linear-gradient(135deg, #096dd9, #1890ff); color:white; display:flex; justify-content:space-between; align-items:center; }")
                .append(".header-left { display:flex; align-items:center; gap:12px; }")
                .append(".header-icon { font-size:32px; }")
                .append(".title { font-size:24px; font-weight:800; }")
                .append(".sub-title { font-size:13px; opacity:0.85; margin-top:2px; }")

                // Status Badge
                .append(".status-badge { padding:6px 16px; border-radius:20px; font-weight:bold; font-size:14px; box-shadow:0 4px 10px rgba(0,0,0,0.1); }")
                .append(".status-success { background:#f6ffed; color:#52c41a; border:1px solid #b7eb8f; }")
                .append(".status-failed { background:#fff2f0; color:#ff4d4f; border:1px solid #ffccc7; }")
                .append(".status-running { background:#e6f7ff; color:#1890ff; border:1px solid #91d5ff; }")

                // Grid Info
                .append(".grid-container { display:grid; grid-template-columns: repeat(2, 1fr); gap:15px; padding:25px 25px 15px; }")
                .append(".info-item { background:#f8fafc; border:1px solid #f0f0f0; border-radius:14px; padding:14px 18px; }")
                .append(".info-label { font-size:13px; color:#8c8c8c; margin-bottom:4px; }")
                .append(".info-value { font-size:15px; font-weight:700; color:#262626; word-break:break-all; }")
                .append(".info-value.highlight { color:#1890ff; }")
                .append(".branch-tag { font-size:12px; background:#e6f7ff; color:#096dd9; padding:2px 8px; border-radius:6px; font-family:Consolas, monospace; }")

                // Commit list
                .append(".commit-section { padding:10px 25px 20px; }")
                .append(".section-title { font-size:15px; font-weight:700; color:#262626; margin-bottom:12px; }")
                .append(".commit-list { background:#fafafa; border:1px solid #f0f0f0; border-radius:16px; padding:10px 16px; }")
                .append(".commit-item { padding:12px 0; border-bottom:1px dashed #e8e8e8; }")
                .append(".commit-item:last-child { border-bottom:none; }")
                .append(".commit-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:6px; }")
                .append(".commit-msg { font-size:14px; font-weight:600; color:#1f1f1f; }")
                .append(".commit-author { font-size:12px; color:#595959; background:#e8e8e8; padding:2px 8px; border-radius:6px; }")
                .append(".commit-meta { display:flex; gap:15px; font-size:12px; color:#8c8c8c; font-family:Consolas, monospace; }")
                .append(".commit-id { color:#1890ff; }")

                // Button & Footer
                .append(".btn-link { display:block; text-align:center; margin:0 25px 20px; padding:12px; background:#1890ff; color:white; text-decoration:none; border-radius:12px; font-weight:bold; font-size:14px; }")
                .append(".footer { text-align:center; padding:16px; color:#bfbfbf; font-size:13px; border-top:1px solid #f0f0f0; background:#fafafa; }")
                .append("</style></head><body>")

                .append("<div class=\"card\">")
                // Header
                .append("<div class=\"header\"><div class=\"header-left\"><div class=\"header-icon\">🚀</div><div>")
                .append("<div class=\"title\">DevOps 流水线通知</div>")
                .append("<div class=\"sub-title\">构建任务状态更新</div></div></div>")
                .append("<div class=\"status-badge ").append(statusClass).append("\">").append(statusName).append("</div></div>")

                // Grid Info
                .append("<div class=\"grid-container\">")
                .append("<div class=\"info-item\"><div class=\"info-label\">🍕 流水线名称</div><div class=\"info-value\">").append(pipelineName).append("</div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">🍿 构建编号</div><div class=\"info-value highlight\">#").append(buildNumber).append("</div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">🍔 阶段</div><div class=\"info-value\">").append(stageName).append("</div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">🍟 任务</div><div class=\"info-value\">").append(taskName).append("</div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">🥚 执行人</div><div class=\"info-value\">").append(executorName).append("</div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">🌿 分支</div><div class=\"info-value\"><span class=\"branch-tag\">").append(branch).append("</span></div></div>")
                .append("</div>");

        // Commit List
        if (commitMsg != null && !commitMsg.isEmpty()) {
            html.append("<div class=\"commit-section\">")
                    .append("<div class=\"section-title\">📝 代码提交记录</div>")
                    .append("<div class=\"commit-list\">");

            for (int i = 0; i < commitMsg.size(); i++) {
                JSONObject commit = commitMsg.getJSONObject(i);
                String commitId = commit.getString("commitId");
                String commitMsgStr = URLDecoder.decode(commit.getString("commitMsg"), StandardCharsets.UTF_8);
                String commitTime = commit.getString("commitTime");
                String commitAuthor = URLDecoder.decode(commit.getString("commitAuthor"), StandardCharsets.UTF_8);

                html.append("<div class=\"commit-item\">")
                        .append("<div class=\"commit-header\">")
                        .append("<div class=\"commit-msg\">🍧 ").append(commitMsgStr).append("</div>")
                        .append("<div class=\"commit-author\">👤 ").append(commitAuthor).append("</div>")
                        .append("</div>")
                        .append("<div class=\"commit-meta\">")
                        .append("<div>提交ID: <span class=\"commit-id\">").append(commitId).append("</span></div>")
                        .append("<div>时间: ").append(commitTime).append("</div>")
                        .append("</div>")
                        .append("</div>");
            }
            html.append("</div></div>");
        }

        // 跳转按钮
        if (pipelineUrl != null && !pipelineUrl.isEmpty()) {
            html.append("<a href=\"").append(pipelineUrl).append("\" class=\"btn-link\" target=\"_blank\">🔗 点击查看流水线详情</a>");
        }

        // Footer
        html.append("<div class=\"footer\">Powered By DevOps CI/CD Center 🛠️</div>")
                .append("</div></body></html>");

        return html.toString();
    }



    /**
     * GitHub Push Webhook HTML 消息模板
     */
    public static String githubPushTemplate(JSONObject body) {
        log.info("接收到 GitHub Webhook 回调: {}", body.toJSONString());

        // 1. 提取仓库信息
        JSONObject repository = body.getJSONObject("repository");
        String repoName = repository != null ? repository.getString("name") : "-";
        String repoUrl = repository != null ? repository.getString("html_url") : "";

        // 2. 提取分支信息 (如 "refs/heads/master" -> "master")
        String ref = body.getString("ref");
        String branch = (ref != null && ref.startsWith("refs/heads/"))
                ? ref.replace("refs/heads/", "")
                : (ref != null ? ref : "-");

        // 3. 提取推送人信息
        JSONObject pusher = body.getJSONObject("pusher");
        String pusherName = pusher != null ? pusher.getString("name") : "-";

        // 4. 提取 Commit 列表 & 统计
        JSONArray commits = body.getJSONArray("commits");
        int commitCount = commits != null ? commits.size() : 0;

        // 状态判定：GitHub Push 成功触发即代表推送成功
        String statusName = "提交代码回调操作";
        String statusClass = "status-success"; // 绿色卡片

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">")
                .append("<style>")
                .append("* { margin:0; padding:0; box-sizing:border-box; }")
                .append("body { background:#eef3f8; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif; padding:30px; display:flex; justify-content:center; }")
                .append(".card { width:700px; background:#fff; border-radius:24px; overflow:hidden; box-shadow:0 15px 35px rgba(0,0,0,0.08); }")

                // Header
                .append(".header { padding:28px 30px; background:linear-gradient(135deg, #24292e, #404448); color:white; display:flex; justify-content:space-between; align-items:center; }")
                .append(".header-left { display:flex; align-items:center; gap:12px; }")
                .append(".header-icon { font-size:32px; }")
                .append(".title { font-size:24px; font-weight:800; }")
                .append(".sub-title { font-size:13px; opacity:0.85; margin-top:2px; }")

                // Status Badge
                .append(".status-badge { padding:6px 16px; border-radius:20px; font-weight:bold; font-size:14px; box-shadow:0 4px 10px rgba(0,0,0,0.1); }")
                .append(".status-success { background:#f6ffed; color:#52c41a; border:1px solid #b7eb8f; }")

                // Grid Info
                .append(".grid-container { display:grid; grid-template-columns: repeat(2, 1fr); gap:15px; padding:25px 25px 15px; }")
                .append(".info-item { background:#f8fafc; border:1px solid #f0f0f0; border-radius:14px; padding:14px 18px; }")
                .append(".info-label { font-size:13px; color:#8c8c8c; margin-bottom:4px; }")
                .append(".info-value { font-size:15px; font-weight:700; color:#262626; word-break:break-all; }")
                .append(".info-value.highlight { color:#1890ff; }")
                .append(".branch-tag { font-size:12px; background:#e6f7ff; color:#096dd9; padding:2px 8px; border-radius:6px; font-family:Consolas, monospace; }")

                // Commit list
                .append(".commit-section { padding:10px 25px 20px; }")
                .append(".section-title { font-size:15px; font-weight:700; color:#262626; margin-bottom:12px; }")
                .append(".commit-list { background:#fafafa; border:1px solid #f0f0f0; border-radius:16px; padding:10px 16px; }")
                .append(".commit-item { padding:12px 0; border-bottom:1px dashed #e8e8e8; }")
                .append(".commit-item:last-child { border-bottom:none; }")
                .append(".commit-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:6px; }")
                .append(".commit-msg { font-size:14px; font-weight:600; color:#1f1f1f; }")
                .append(".commit-author { font-size:12px; color:#595959; background:#e8e8e8; padding:2px 8px; border-radius:6px; }")
                .append(".commit-meta { display:flex; gap:15px; font-size:12px; color:#8c8c8c; font-family:Consolas, monospace; }")
                .append(".commit-id { color:#1890ff; }")

                // Button & Footer
                .append(".btn-link { display:block; text-align:center; margin:0 25px 20px; padding:12px; background:#24292e; color:white; text-decoration:none; border-radius:12px; font-weight:bold; font-size:14px; }")
                .append(".footer { text-align:center; padding:16px; color:#bfbfbf; font-size:13px; border-top:1px solid #f0f0f0; background:#fafafa; }")
                .append("</style></head><body>")

                .append("<div class=\"card\">")
                // Header
                .append("<div class=\"header\"><div class=\"header-left\"><div class=\"header-icon\">🐙</div><div>")
                .append("<div class=\"title\">GitHub Push Web hook 事件通知</div>")
                .append("<div class=\"sub-title\">代码仓库变更提醒</div></div></div>")
                .append("<div class=\"status-badge ").append(statusClass).append("\">").append(statusName).append("</div></div>")

                // Grid Info
                .append("<div class=\"grid-container\">")
                .append("<div class=\"info-item\"><div class=\"info-label\">📦 仓库名称</div><div class=\"info-value\">").append(repoName).append("</div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">🌿 目标分支</div><div class=\"info-value\"><span class=\"branch-tag\">").append(branch).append("</span></div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">👤 推送者</div><div class=\"info-value\">").append(pusherName).append("</div></div>")
                .append("<div class=\"info-item\"><div class=\"info-label\">🔢 提交数量</div><div class=\"info-value highlight\">").append(commitCount).append(" 次</div></div>")
                .append("</div>");

        // Commit List
        if (commits != null && !commits.isEmpty()) {
            html.append("<div class=\"commit-section\">")
                    .append("<div class=\"section-title\">📝 详细提交记录</div>")
                    .append("<div class=\"commit-list\">");

            for (int i = 0; i < commits.size(); i++) {
                JSONObject commit = commits.getJSONObject(i);
                String fullCommitId = commit.getString("id");
                // 截取前 7 位 short commit hash
                String shortCommitId = (fullCommitId != null && fullCommitId.length() >= 7)
                        ? fullCommitId.substring(0, 7)
                        : fullCommitId;

                String commitMsgStr = commit.getString("message");
                String commitTime = commit.getString("timestamp");

                // 格式化时间显示（如 2026-07-25T16:41:31+08:00 提取时间）
                if (commitTime != null && commitTime.contains("T")) {
                    commitTime = commitTime.replace("T", " ").replaceAll("\\+.*", "");
                }

                JSONObject author = commit.getJSONObject("author");
                String authorName = author != null ? author.getString("name") : "-";

                html.append("<div class=\"commit-item\">")
                        .append("<div class=\"commit-header\">")
                        .append("<div class=\"commit-msg\">📌 ").append(commitMsgStr != null ? commitMsgStr : "").append("</div>")
                        .append("<div class=\"commit-author\">👤 ").append(authorName).append("</div>")
                        .append("</div>")
                        .append("<div class=\"commit-meta\">")
                        .append("<div>Commit ID: <span class=\"commit-id\">").append(shortCommitId).append("</span></div>")
                        .append("<div>时间: ").append(commitTime).append("</div>")
                        .append("</div>")
                        .append("</div>");
            }
            html.append("</div></div>");
        }

        // 跳转按钮
        if (repoUrl != null && !repoUrl.isEmpty()) {
            html.append("<a href=\"").append(repoUrl).append("\" class=\"btn-link\" target=\"_blank\">🐙 前往 GitHub 查看项目仓库</a>");
        }

        // Footer
        html.append("<div class=\"footer\">Powered By GitHub Webhook Center 🛠️</div>")
                .append("</div></body></html>");

        return html.toString();
    }
}
