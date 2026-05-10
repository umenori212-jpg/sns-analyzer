package com.example.snsanalyzer.service;

import com.example.snsanalyzer.model.AnalysisResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class VideoAnalyzerService {

    private static final Logger log = LoggerFactory.getLogger(VideoAnalyzerService.class);
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VideoAnalyzerService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public AnalysisResult analyze(MultipartFile file, List<String> selectedTags) {
        try {
            log.info("=== 分析開始: {} ===", file.getOriginalFilename());
            byte[] fileBytes = file.getBytes();
            String mimeType = detectMimeType(file.getOriginalFilename());
            String aiResponse = geminiService.analyzeVideo(fileBytes, mimeType, selectedTags);
            AnalysisResult result = parseResponse(aiResponse, file.getOriginalFilename());
            log.info("=== 分析完了: スコア{} ===", result.getOverallScore());
            return result;
        } catch (Exception e) {
            log.error("分析エラー: {}", e.getMessage(), e);
            AnalysisResult error = new AnalysisResult();
            error.setFileName(file.getOriginalFilename());
            error.setErrorMessage("分析中にエラーが発生しました: " + e.getMessage());
            error.setSuccess(false);
            error.setBuzzElements(new ArrayList<>());
            error.setImprovements(new ArrayList<>());
            error.setHashtags(new ArrayList<>());
            return error;
        }
    }

    private String detectMimeType(String filename) {
        if (filename == null) return "video/mp4";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".mp4"))  return "video/mp4";
        if (lower.endsWith(".mov"))  return "video/quicktime";
        if (lower.endsWith(".avi"))  return "video/avi";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png"))  return "image/png";
        return "video/mp4";
    }

    private AnalysisResult parseResponse(String aiResponse, String fileName) {
        try {
            String json = extractJson(aiResponse);
            JsonNode node = objectMapper.readTree(json);

            List<String> buzzElements = new ArrayList<>();
            node.path("buzzElements").forEach(n -> buzzElements.add(n.asText()));

            List<String> improvements = new ArrayList<>();
            node.path("improvements").forEach(n -> improvements.add(n.asText()));

            List<String> hashtags = new ArrayList<>();
            node.path("hashtags").forEach(n -> hashtags.add(n.asText()));

            AnalysisResult result = new AnalysisResult();
            result.setFileName(fileName);  // アップロード時のファイル名をそのまま使う
            result.setGenre(node.path("genre").asText("不明"));
            result.setOverallScore(node.path("overallScore").asInt(0));
            result.setOverallComment(node.path("overallComment").asText(""));
            result.setBuzzElements(buzzElements);
            result.setImprovements(improvements);
            result.setTargetAudience(node.path("targetAudience").asText("不明"));
            result.setPostCaption(node.path("postCaption").asText(""));
            result.setPostCaptionJa(node.path("postCaptionJa").asText(""));
            result.setHashtags(hashtags);
            result.setSuccess(true);
            return result;

        } catch (Exception e) {
            log.error("レスポンスのパースエラー: {}", e.getMessage());
            AnalysisResult error = new AnalysisResult();
            error.setFileName(fileName);
            error.setErrorMessage("AI分析結果の解析に失敗しました。");
            error.setSuccess(false);
            error.setBuzzElements(new ArrayList<>());
            error.setImprovements(new ArrayList<>());
            error.setHashtags(new ArrayList<>());
            return error;
        }
    }

    private String extractJson(String text) {
        if (text.contains("```json")) {
            int start = text.indexOf("```json") + 7;
            int end = text.lastIndexOf("```");
            if (end > start) return text.substring(start, end).trim();
        }
        if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.lastIndexOf("```");
            if (end > start) return text.substring(start, end).trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start != -1 && end != -1) return text.substring(start, end + 1);
        return text.trim();
    }
}
