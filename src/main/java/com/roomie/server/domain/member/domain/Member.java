package com.roomie.server.domain.member.domain;

import com.roomie.server.domain.roomie.domain.Roomie;
import com.roomie.server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "point", nullable = true)
    @Builder.Default
    private Integer points = 0;

    @Setter
    @Column(name = "room_image_url", nullable = true)
    private String roomImageUrl;

    @JoinColumn(name = "roomie_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Roomie roomie;

    public static Member of(
            String loginId,
            String encodedPassword,
            String name,
            Roomie roomie
    ) {
        return Member.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .name(name)
                .roomie(roomie)
                .build();
    }

}
