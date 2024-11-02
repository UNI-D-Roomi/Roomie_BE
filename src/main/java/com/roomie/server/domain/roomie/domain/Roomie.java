package com.roomie.server.domain.roomie.domain;

import com.roomie.server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "roomie")
public class Roomie extends BaseEntity {

    @Setter
    @Column(name = "hange_gage", nullable = false)
    @Builder.Default
    private Double hungerGage = 100.0;

    @Setter
    @Column(name = "last_feed_time", nullable = true)
    private LocalDateTime lastFeedTime;

    @Setter
    @Column(name = "is_ribbon", nullable = false)
    @Builder.Default
    private Boolean isRibbon = false;

    @Setter
    @Column(name = "before_wash_image_url", nullable = true)
    private String beforeWashImageUrl;

    @Setter
    @Column(name = "is_washing", nullable = true)
    private LocalDateTime washingStartTime;

    public static Roomie of() {
        return Roomie.builder()
                .build();
    }

}
