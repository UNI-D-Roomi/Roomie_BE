package com.roomie.server.domain.roomie.domain;

import com.roomie.server.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "roomie")
public class Roomie extends BaseEntity {

    @Column(name = "hange_gage", nullable = false)
    private Double hungerGage;

    @Column(name = "last_feed_time", nullable = false)
    private LocalDateTime lastFeedTime;

    @Column(name = "is_ribbon", nullable = false)
    private Boolean isRibbon;

    @Column(name = "is_washing", nullable = true)
    private LocalDateTime washingStartTime;

}
