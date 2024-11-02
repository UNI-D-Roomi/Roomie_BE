package com.roomie.server.global.domain.uuidFile.application;

import com.roomie.server.global.config.aws.s3.AmazonS3Manager;
import com.roomie.server.global.domain.uuidFile.domain.FilePath;
import com.roomie.server.global.domain.uuidFile.domain.UuidFile;
import com.roomie.server.global.domain.uuidFile.domain.repository.UuidFileRepository;
import com.roomie.server.global.exceptions.ErrorCode;
import com.roomie.server.global.exceptions.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UuidFileService {

    private final UuidFileRepository uuidFileRepository;

    private final AmazonS3Manager amazonS3Manager;

    public UuidFile findUuidFileById(Long id) {
        return uuidFileRepository.findById(id).orElseThrow(() -> new InternalServerException(ErrorCode.ROW_DOES_NOT_EXIST));
    }

    @Transactional
    public UuidFile saveFile(MultipartFile file, FilePath filePath) {
        String uuid = UUID.randomUUID().toString();

        String fileS3Url = amazonS3Manager.uploadFile(generatePathKey(filePath, uuid), file);

        UuidFile uuidFile = UuidFile.builder()
                .uuid(uuid)
                .filePath(filePath)
                .fileUrl(fileS3Url)
                .build();

        uuidFileRepository.save(uuidFile);

        return uuidFile;
    }

    @Transactional
    public void deleteFile(UuidFile uuidFile) {
        amazonS3Manager.deleteFile(generatePathKey(uuidFile.getFilePath(), uuidFile.getUuid()));
        uuidFileRepository.delete(uuidFile);
    }

    private String generatePathKey(FilePath filePath, String uuid) {
        return filePath.getPath() + '/' + uuid;
    }

}
