package com.roomie.server.global.domain.uuidFile.domain;

import com.roomie.server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "uuid_image")
public class UuidFile extends BaseEntity {

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_path")
    private FilePath filePath;

    @Column(name = "file_url")
    private String fileUrl;

}
