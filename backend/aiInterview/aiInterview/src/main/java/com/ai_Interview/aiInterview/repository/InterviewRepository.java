package com.ai_Interview.aiInterview.repository;

import com.ai_Interview.aiInterview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
