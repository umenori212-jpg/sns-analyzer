package com.example.snsanalyzer.controller;

import com.example.snsanalyzer.model.AnalysisResult;
import com.example.snsanalyzer.service.VideoAnalyzerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class VideoController {

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);
    private final VideoAnalyzerService analyzerService;

    public VideoController(VideoAnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(
            @RequestParam("video") MultipartFile video,
            @RequestParam(value = "selectedTags", required = false, defaultValue = "") String selectedTags,
            Model model) {

        if (video.isEmpty()) {
            model.addAttribute("error", "ファイルを選択してください。");
            return "index";
        }

        // オリジナルのファイル名をそのまま取得（変えない）
        String filename = video.getOriginalFilename();
        if (filename == null) {
            model.addAttribute("error", "ファイル名が取得できませんでした。");
            return "index";
        }

        String lower = filename.toLowerCase();
        boolean isVideo = lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".avi");
        boolean isImage = lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");

        if (!isVideo && !isImage) {
            model.addAttribute("error", "MP4・MOV・AVI・JPG・PNGのみ対応しています。");
            return "index";
        }

        List<String> tagList = Collections.emptyList();
        if (!selectedTags.isBlank()) {
            tagList = Arrays.asList(selectedTags.split(","));
        }

        log.info("ファイル受信: {} ({} MB)", filename,
                String.format("%.1f", video.getSize() / 1024.0 / 1024.0));

        AnalysisResult result = analyzerService.analyze(video, tagList);
        model.addAttribute("result", result);
        return "result";
    }
}
