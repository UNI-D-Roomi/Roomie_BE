package com.roomie.server.domain.roomie.dto;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.roomie.domain.Roomie;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class HomeResponseDto {

    @Schema(description = "회원 식별자", example = "1")
    private Long memberId;

    @Schema(description = "아이디", example = "testId123")
    private String loginId;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "포인트", example = "100")
    private Integer points;

    @Schema(description = "방 이미지 URL", example = "https://roomie.com/room/1")
    private String roomImageUrl;

    @Schema(description = "Roomie id", example = "1")
    private Long roomieId;

    @Schema(description = "Roomie 배고픔 게이지")
    private Double hungerGage;

    @Schema(description = "마지막 먹이 준 시간")
    private String lastFeedTime;

    @Schema(description = "리본 여부")
    private Boolean isRibbon;

    @Schema(description = "설거지 전 이미지 URL")
    private String beforeWashImageUrl;

    @Schema(description = "설거지 시작 시")
    private LocalDateTime washingStartTime;

    public static HomeResponseDto from(Member member, Roomie roomie) {
        return HomeResponseDto.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .points(member.getPoints())
                .roomImageUrl(member.getRoomImageUrl())
                .roomieId(roomie.getId())
                .hungerGage(roomie.getHungerGage())
                .lastFeedTime(roomie.getBeforeWashImageUrl())
                .isRibbon(roomie.getIsRibbon())
                .beforeWashImageUrl(roomie.getBeforeWashImageUrl())
                .washingStartTime(roomie.getWashingStartTime())
                .build();
    }

}
