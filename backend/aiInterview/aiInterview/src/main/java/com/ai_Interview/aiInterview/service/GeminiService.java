package com.ai_Interview.aiInterview.service;

import com.ai_Interview.aiInterview.dto.EvaluationResponse;
import com.ai_Interview.aiInterview.dto.InterviewResult;
import com.ai_Interview.aiInterview.entity.Interview;
import com.ai_Interview.aiInterview.entity.QuestionAnswer;
import com.ai_Interview.aiInterview.repository.InterviewRepository;
import com.ai_Interview.aiInterview.repository.QuestionAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateQuestions(String role) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        String prompt =
                "Generate 5 interview questions for a " + role +
                        " fresher. Return only the questions.";

        try {

            Map<String, Object> text = new HashMap<>();
            text.put("text", prompt);

            Map<String, Object> part = new HashMap<>();
            part.put("parts", List.of(text));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", List.of(part));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response.getBody());

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (HttpClientErrorException.TooManyRequests e) {

            System.out.println("Gemini quota exceeded. Returning dummy questions.");

            return """
                    1. What is Java?
                    2. Explain OOP concepts.
                    3. Difference between ArrayList and LinkedList?
                    4. What is Exception Handling?
                    5. Explain JVM, JRE and JDK.
                    """;
        } catch (Exception e) {

            e.printStackTrace();

            return """
                    1. What is Java?
                    2. Explain OOP concepts.
                    3. Difference between ArrayList and LinkedList?
                    4. What is Exception Handling?
                    5. Explain JVM, JRE and JDK.
                    """;
        }

    }

    // ===========================
    // Evaluate Answer
    // ===========================

    public EvaluationResponse evaluateAnswer(String question, String answer) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        String prompt = """
                Evaluate the following interview answer.
                
                Question:
                %s
                
                Answer:
                %s
                
                Give a score between 0 and 10.
                
                Return ONLY in this format:
                
                Score: <number>
                
                Feedback: <Maximum 2 short sentences>
                """.formatted(question, answer);

        try {

            Map<String, Object> text = new HashMap<>();
            text.put("text", prompt);

            Map<String, Object> part = new HashMap<>();
            part.put("parts", List.of(text));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", List.of(part));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response.getBody());

            String result = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            EvaluationResponse evaluation = new EvaluationResponse();

            evaluation.setScore(extractScore(result));

            String feedback = result;

            if (result.contains("Feedback:")) {

                feedback = result.substring(
                        result.indexOf("Feedback:") + 9
                ).trim();
            }

            evaluation.setFeedback(feedback);

            return evaluation;

        } catch (HttpClientErrorException.TooManyRequests e) {

            System.out.println("Gemini quota exceeded. Returning dummy evaluation.");

            EvaluationResponse response = new EvaluationResponse();

            response.setScore(8.5);

            response.setFeedback(
                    "Good answer. Add more technical details and one practical example."
            );

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            EvaluationResponse response = new EvaluationResponse();

            response.setScore(7.5);

            response.setFeedback(
                    "Answer received. Improve explanation and provide a real-world example."
            );

            return response;

        }

    }

    // ===========================
    // Extract Score
    // ===========================

    private Double extractScore(String result) {

        try {

            String[] lines = result.split("\n");

            for (String line : lines) {

                if (line.toLowerCase().startsWith("score")) {

                    String value =
                            line.substring(line.indexOf(":") + 1).trim();

                    if (value.contains("/")) {

                        value = value.substring(
                                0,
                                value.indexOf("/")
                        ).trim();
                    }

                    return Double.parseDouble(value);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0.0;
    }

}
