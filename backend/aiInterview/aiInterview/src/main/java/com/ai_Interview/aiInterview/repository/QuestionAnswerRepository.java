package com.ai_Interview.aiInterview.repository;

import com.ai_Interview.aiInterview.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    List<QuestionAnswer> findByInterviewId(Long interviewId);
}
