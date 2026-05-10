package com.example.snsanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public String analyzeVideo(byte[] fileBytes, String mimeType, List<String> selectedTags) throws Exception {
        log.info("Gemini API呼び出し開始 ({} bytes)", fileBytes.length);

        String base64Data = Base64.getEncoder().encodeToString(fileBytes);
        String requestBody = buildRequestBody(base64Data, mimeType, selectedTags);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(180))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        log.info("Gemini API レスポンスコード: {}", response.statusCode());

        if (response.statusCode() != 200) {
            log.error("Gemini API エラー: {}", response.body());
            throw new RuntimeException("Gemini API エラー (HTTP " + response.statusCode() + "): " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String content = root
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text")
                .asText();

        log.info("Gemini API 分析完了");
        return content;
    }

    private String buildRequestBody(String base64Data, String mimeType, List<String> selectedTags) throws Exception {

        String tagPrompt = "";
        String tagJsonExample = "";
        if (selectedTags != null && !selectedTags.isEmpty()) {
            StringBuilder tagList = new StringBuilder();
            StringBuilder tagJson = new StringBuilder();
            for (String tag : selectedTags) {
                tagList.append("- ").append(tag).append("\n");
                tagJson.append("    \"").append(tag).append("\": \"分析内容\",\n");
            }
            tagPrompt = "\n\nさらに以下の項目についても日本語で分析してください:\n" + tagList;
            tagJsonExample = ",\n  \"tagResults\": {\n" + tagJson + "  }";
        }

        String prompt = """
                あなたはSNSやショート動画の専門アナリストです。
                この動画を分析して、以下のJSON形式のみで回答してください。
                余分なテキストやマークダウンは含めないでください。

                【重要】
                - genre、targetAudience、postCaptionJaは日本語で書いてください
                - postCaptionは英語の投稿文として書いてください（SNSにそのまま使えるキャッチーな文章）
                - postCaptionJaはpostCaptionの日本語訳を書いてください
                - hashtagsは英語のハッシュタグをおすすめ5個だけ、#なしで書いてください
                """ + tagPrompt + """

                {
                  "genre": "動画のジャンル",
                  "targetAudience": "想定ターゲット（日本語）",
                  "postCaption": "英語の投稿文（絵文字含むSNS向けキャプション）",
                  "postCaptionJa": "上記の日本語訳",
                  "hashtags": ["hashtag1", "hashtag2", "hashtag3", "hashtag4", "hashtag5"]
                """ + tagJsonExample + "\n}";

        Map<String, Object> inlineData = new HashMap<>();
        inlineData.put("mime_type", mimeType);
        inlineData.put("data", base64Data);

        Map<String, Object> part1 = new HashMap<>();
        part1.put("text", prompt);

        Map<String, Object> part2 = new HashMap<>();
        part2.put("inline_data", inlineData);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", new Object[]{part1, part2});

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contents", new Object[]{content});

        return objectMapper.writeValueAsString(requestMap);
    }
}
