package com.ai_Interview.aiInterview.controller;

import com.ai_Interview.aiInterview.dto.EvaluationRequest;
import com.ai_Interview.aiInterview.dto.InterviewRequest;
import com.ai_Interview.aiInterview.dto.InterviewResult;
import com.ai_Interview.aiInterview.dto.InterviewStartResponse;
import com.ai_Interview.aiInterview.entity.Interview;
import com.ai_Interview.aiInterview.entity.QuestionAnswer;
import com.ai_Interview.aiInterview.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/interview")
public class InterviewController {
    @Autowired
    private InterviewService interviewService;

    @PostMapping("/start")
    public InterviewStartResponse startInterview(@RequestBody InterviewRequest request) {
        return interviewService.startInterview(request);
    }

    @PostMapping("/evaluate")
    public Object evaluateAnswer(@RequestBody EvaluationRequest request) {
        return interviewService.submitAnswer(request);
    }

    @GetMapping("/history")
    public List<Interview> getInterviewHistory() {
        return interviewService.getInterviewHistory();
    }
    @GetMapping("/history/{id}")
    public List<QuestionAnswer> getInterviewDetails(@PathVariable Long id) {
        return interviewService.getInterviewDetails(id);
    }
    @GetMapping("/result/{id}")
    public ResponseEntity<?> getResult(@PathVariable Long id) {

        InterviewResult result = interviewService.getInterviewResult(id);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Interview ID " + id + " not present in database.");
        }

        return ResponseEntity.ok(result);
    }


}
