package com.ai_Interview.aiInterview.dto;

public class InterviewStartResponse {

        private Long interviewId;
        private String questions;

        public InterviewStartResponse() {
        }

        public InterviewStartResponse(Long interviewId, String questions) {
            this.interviewId = interviewId;
            this.questions = questions;
        }

        public Long getInterviewId() {
            return interviewId;
        }

        public void setInterviewId(Long interviewId) {
            this.interviewId = interviewId;
        }

        public String getQuestions() {
            return questions;
        }

        public void setQuestions(String questions) {
            this.questions = questions;
        }
    }

