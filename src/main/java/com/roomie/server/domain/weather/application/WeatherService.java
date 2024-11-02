package com.roomie.server.domain.weather.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    public String getCleaningAdvice() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String baseDate;
            String fcstDate;
            String baseTime = "0500";
            String fcstTime = "0500";

            if (now.getHour() < 9) {
                baseDate = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                fcstDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            } else {
                baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                fcstDate = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }

            // URL 생성
            String urlStr = String.format("%s?serviceKey=%s&pageNo=1&numOfRows=1000&dataType=JSON&base_date=%s&base_time=%s&nx=60&ny=127",
                    apiUrl, URLEncoder.encode(apiKey, StandardCharsets.UTF_8), baseDate, baseTime);
            URL url = new URL(urlStr);

            // HTTP 연결 설정
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            // 응답 코드 확인 및 스트림 설정
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            // 응답 읽기
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            // JSON 데이터 파싱 및 멘트 생성
            return generateCleaningAdvice(sb.toString(), fcstDate, fcstTime);

        } catch (IOException e) {
            logger.error("날씨 정보를 불러오는 중 오류가 발생했습니다: {}", e.getMessage());
            return "날씨 정보를 불러오는 중 오류가 발생했습니다.";
        }
    }

    private String generateCleaningAdvice(String data, String targetFcstDate, String targetFcstTime) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(data);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            StringBuilder advice = new StringBuilder();

            for (JsonNode item : items) {
                String category = item.path("category").asText();
                String fcstDate = item.path("fcstDate").asText();
                String fcstTime = item.path("fcstTime").asText();
                String fcstValue = item.path("fcstValue").asText();

                // 예보 날짜와 시간이 맞는 데이터만 처리
                if (targetFcstDate.equals(fcstDate) && targetFcstTime.equals(fcstTime)) {
                    switch (category) {
                        case "PTY":
                            int pty = Integer.parseInt(fcstValue);
                            if (pty == 1 || pty == 4) {
                                if (!advice.isEmpty()) advice.append("그리고 ");
                                advice.append("비가 오니까 빨래를 하긴 어려울 거 같아. ");
                            } else if (pty == 2 || pty == 3) {
                                if (!advice.isEmpty()) advice.append("그리고 ");
                                advice.append("눈이 오니 외출 후 청소에 신경 쓰면 좋겠어. ");
                            }
                            break;

                        case "SKY":
                            int sky = Integer.parseInt(fcstValue);
                            if (sky == 1) {
                                if (!advice.isEmpty()) advice.append("그리고 ");
                                advice.append("맑으니 환기하기 좋은 날이야! ");
                            } else if (sky == 3 || sky == 4) {
                                if (!advice.isEmpty()) advice.append("그리고 ");
                                advice.append("구름이 많아 실내 공기가 답답할 수 있어, 청소하고 환기해보는 건 어때? ");
                            }
                            break;

                        case "REH":
                            int reh = Integer.parseInt(fcstValue);
                            if (reh > 70) {
                                if (!advice.isEmpty()) advice.append("그리고 ");
                                advice.append("습도가 높아, 빨래가 잘 안 마를 수 있어. ");
                            } else if (reh < 30) {
                                if (!advice.isEmpty()) advice.append("그리고 ");
                                advice.append("건조하니 실내 먼지가 많이 쌓일 수 있어, 청소할 때 물걸레를 활용해봐! ");
                            }
                            break;

                        case "WSD":
                            double windSpeed = Double.parseDouble(fcstValue);
                            if (windSpeed > 10) {
                                if (!advice.isEmpty()) advice.append("그리고 ");
                                advice.append("바람이 강하니 먼지가 많이 들어올 수 있어, 청소 준비해! ");
                            }
                            break;
                    }
                }
            }

            return !advice.isEmpty() ? advice.toString() : "날씨가 청소에 큰 영향을 주지 않을 것 같아.";

        } catch (Exception e) {
            logger.error("날씨 데이터를 처리하는 중 오류가 발생했습니다: {}", e.getMessage());
            return "날씨 데이터를 처리하는 중 오류가 발생했습니다.";
        }
    }
}
