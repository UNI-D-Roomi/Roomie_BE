package com.roomie.server.domain.ai;

import com.roomie.server.domain.roomie.dto.CompareResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

@Service
public class GPTService {

    @Value("${openai.api_key}")
    private String apiKey;

    @Value("${openai.api_url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public CompareResponseDto compareImages(String compareType, String beforeUrl, String afterUrl) throws IOException {
        String beforeImage = encodeImageFromUrl(beforeUrl);
        String afterImage = encodeImageFromUrl(afterUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");

        String prompt;
        if ("ROOM".equalsIgnoreCase(compareType)) {
            prompt = "첫 번째 이미지를 설거지 전 상태의 기준으로 삼고, 두 번째 이미지가 첫 번째 이미지와 얼마나 비슷하게 깨끗하게 설거지되었는지 평가해주세요. " +
                    "두 번째 이미지가 기준 사진과 거의 동일하거나 더 깨끗하면 100점을 부여하고, 기준에서 벗어나는 정도에 따라 점수를 다음과 같이 매겨주세요: " +
                    "- 80~100점: 기준과 거의 비슷하게 깨끗함\n" +
                    "- 60~80점: 깨끗하지만 약간의 잔여물이 남아 있음\n" +
                    "- 40~60점: 비교적 더러워졌지만 추가적인 설거지가 필요한 정도\n" +
                    "- 20~40점: 상당한 잔여물이 남아 있고 설거지가 절실히 필요함\n" +
                    "- 10~20점: 매우 더럽고 설거지가 거의 되지 않아 상태를 알아보기가 어려운 정도\n" +
                    "- 5점: 전혀 알아볼 수 없을 정도로 매우 심하게 더러워져 기준과 극도로 큰 차이가 날 경우에만 부여\n" +
                    "첫 줄에는 숫자만 적고, 두 번째 줄에는 귀여운 게임 캐릭터가 말하듯이, 최대 20자 이내로 설거지 상태에 대해 간단하고 귀엽게 평가해 주세요. '첫 번째 사진'이나 '두 번째 사진' 같은 표현은 생략해 주세요.";
        } else if ("WASH".equalsIgnoreCase(compareType)) {
            prompt = "첫 번째 이미지를 설거지 전 상태의 기준으로 삼고, 두 번째 이미지가 첫 번째 이미지와 얼마나 비슷하게 깨끗하게 설거지되었는지 평가해주세요. " +
                    "두 번째 이미지가 기준 사진과 거의 동일하거나 더 깨끗하면 100점을 부여하고, 기준에서 벗어나는 정도에 따라 점수를 다음과 같이 매겨주세요: " +
                    "- 60~80점: 깨끗하지만 약간의 설거지 안된 식기류 등이 남아 있음\n" +
                    "- 40~60점: 비교적 더러워졌지만 추가적인 설거지가 필요한 정도\n" +
                    "- 20~40점: 상당한 잔여물이 남아 있고 설거지가 절실히 필요함\n" +
                    "- 10~20점: 매우 더럽고 설거지가 거의 되지 않아 상태를 알아보기가 어려운 정도\n" +
                    "- 5점: 전혀 알아볼 수 없을 정도로 매우 심하게 더러워져 기준과 극도로 큰 차이가 날 경우에만 부여\n" +
                    "첫 줄에는 숫자만 적고, 두 번째 줄에는 귀여운 게임 캐릭터가 말하듯이, 최대 20자 이내로 설거지 상태에 대해 간단하고 귀엽게 평가해 주세요.'첫 번째 사진'이나 '두 번째 사진' 같은 표현은 생략해 주세요.";
        } else {
            return new CompareResponseDto(0.0, "Invalid comparison type. Please use 'ROOM' or 'WASH'.");
        }

        JSONObject messageContent = new JSONObject().put("type", "text").put("text", prompt);

        JSONObject imageUrlContent1 = new JSONObject()
                .put("type", "image_url")
                .put("image_url", new JSONObject().put("url", "data:image/jpeg;base64," + beforeImage));

        JSONObject imageUrlContent2 = new JSONObject()
                .put("type", "image_url")
                .put("image_url", new JSONObject().put("url", "data:image/jpeg;base64," + afterImage));

        JSONObject messages = new JSONObject()
                .put("role", "user")
                .put("content", new JSONObject[] { messageContent, imageUrlContent1, imageUrlContent2 });

        JSONObject requestBody = new JSONObject()
                .put("model", "gpt-4o") // 속도가 느릴 경우 "gpt-4o-mini"로 변경
                .put("messages", new JSONObject[] { messages })
                .put("max_tokens", 300);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);


        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
        System.out.println("Response: " + response.getBody());

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String gptResponse = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");


        return parseGPTResponse(gptResponse);
    }

    private String encodeImageFromUrl(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()) {
            byte[] imageBytes = in.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }


    private CompareResponseDto parseGPTResponse(String gptResponse) {
        Double score = 0.0;
        String comment = "";

        try {

            String[] lines = gptResponse.split("\n");

            // 첫번째줄
            if (lines.length > 0) {
                score = Double.parseDouble(lines[0].trim());
            }

            // 두번째줄
            if (lines.length > 1) {
                comment = lines[1].trim();
            } else {
                comment = "No comment provided in the response.";
            }
        } catch (Exception e) {
            comment = "Error parsing GPT response: " + e.getMessage();
        }

        return new CompareResponseDto(score, comment);
    }

}
