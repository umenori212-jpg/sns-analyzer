package com.example.snsanalyzer.model;

import java.util.List;

public class AnalysisResult {

    private String fileName;
    private String genre;
    private List<String> buzzElements;
    private List<String> improvements;
    private String targetAudience;
    private int overallScore;
    private String overallComment;
    private String errorMessage;
    private boolean success;
    private String postCaption;
    private String postCaptionJa; // 日本語訳
    private List<String> hashtags;

    public AnalysisResult() {}

    public String getFileName() { return fileName; }
    public void setFileName(String v) { this.fileName = v; }
    public String getGenre() { return genre; }
    public void setGenre(String v) { this.genre = v; }
    public List<String> getBuzzElements() { return buzzElements; }
    public void setBuzzElements(List<String> v) { this.buzzElements = v; }
    public List<String> getImprovements() { return improvements; }
    public void setImprovements(List<String> v) { this.improvements = v; }
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String v) { this.targetAudience = v; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int v) { this.overallScore = v; }
    public String getOverallComment() { return overallComment; }
    public void setOverallComment(String v) { this.overallComment = v; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String v) { this.errorMessage = v; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean v) { this.success = v; }
    public String getPostCaption() { return postCaption; }
    public void setPostCaption(String v) { this.postCaption = v; }
    public String getPostCaptionJa() { return postCaptionJa; }
    public void setPostCaptionJa(String v) { this.postCaptionJa = v; }
    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> v) { this.hashtags = v; }
}
