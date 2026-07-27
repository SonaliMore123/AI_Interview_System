package com.ai_Interview.aiInterview.service;

import com.ai_Interview.aiInterview.dto.*;
import com.ai_Interview.aiInterview.entity.Interview;
import com.ai_Interview.aiInterview.entity.QuestionAnswer;
import com.ai_Interview.aiInterview.repository.InterviewRepository;
import com.ai_Interview.aiInterview.repository.QuestionAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewService {
    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private GeminiService geminiService;

    public InterviewStartResponse startInterview(InterviewRequest request) {

        Interview interview = new Interview();
        interview.setRole(request.getRole());

        interviewRepository.save(interview);

        String questions = geminiService.generateQuestions(request.getRole());

        return new InterviewStartResponse(
                interview.getId(),
                questions
        );
    }
    public EvaluationResponse submitAnswer(EvaluationRequest request) {
        EvaluationResponse response = geminiService.evaluateAnswer(
                request.getQuestion(),
                request.getAnswer());

        QuestionAnswer qa = new QuestionAnswer();

        qa.setInterviewId(request.getInterviewId());
        qa.setQuestion(request.getQuestion());
        qa.setAnswer(request.getAnswer());
        qa.setScore(response.getScore());
        qa.setFeedback(response.getFeedback());

        questionAnswerRepository.save(qa);

        Interview interview = interviewRepository.findById(request.getInterviewId()).orElseThrow();

        interview.setTotalScore(interview.getTotalScore() + response.getScore());

        interviewRepository.save(interview);

        return response;
    }
    public List<Interview> getInterviewHistory() {
        return interviewRepository.findAll();
    }
    public List<QuestionAnswer> getInterviewDetails(Long interviewId) {
        return questionAnswerRepository.findByInterviewId(interviewId);
    }
    public InterviewResult getInterviewResult(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElse(null);

        if (interview == null) {
            return null;
        }

        List<QuestionAnswer> answers =
                questionAnswerRepository.findByInterviewId(interviewId);

        InterviewResult result = new InterviewResult();

        result.setTotalScore(interview.getTotalScore());
        result.setAnsweredQuestions(answers.size());

        if (!answers.isEmpty()) {
            result.setAverageScore(interview.getTotalScore() / answers.size());
        } else {
            result.setAverageScore(0.0);
        }

        if (result.getAverageScore() >= 8) {
            result.setOverallFeedback("Excellent performance. You have strong technical knowledge.");
        } else if (result.getAverageScore() >= 6) {
            result.setOverallFeedback("Good performance. Improve your explanations with more examples.");
        } else {
            result.setOverallFeedback("Keep practicing Java fundamentals and interview questions.");
        }

        return result;
    }
    //
}
