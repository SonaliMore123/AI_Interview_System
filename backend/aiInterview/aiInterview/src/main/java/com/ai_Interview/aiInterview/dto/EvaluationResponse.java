package com.ai_Interview.aiInterview.dto;

public class EvaluationResponse {
    private Double score;

    private String feedback;

    public EvaluationResponse() {
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
