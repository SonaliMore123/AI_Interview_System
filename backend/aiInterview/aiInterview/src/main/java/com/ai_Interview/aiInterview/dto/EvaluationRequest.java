package com.ai_Interview.aiInterview.dto;

public class EvaluationRequest {
    private Long interviewId;

    private String question;

    private String answer;

    public EvaluationRequest() {
        //
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        //
    }
}
