package com.roomie.server.domain.roomie.dto;

import com.roomie.server.domain.roomie.domain.Roomie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RoomieResponseDto {

    private Long id;

    private Double hungerGage;

    private String lastFeedTime;

    private Boolean isRibbon;

    private String beforeWashImageUrl;

    private LocalDateTime washingStartTime;

    public static RoomieResponseDto from(Roomie roomie) {
        return RoomieResponseDto.builder()
                .id(roomie.getId())
                .hungerGage(roomie.getHungerGage())
                .lastFeedTime(roomie.getBeforeWashImageUrl())
                .isRibbon(roomie.getIsRibbon())
                .beforeWashImageUrl(roomie.getBeforeWashImageUrl())
                .washingStartTime(roomie.getWashingStartTime())
                .build();
    }

}
