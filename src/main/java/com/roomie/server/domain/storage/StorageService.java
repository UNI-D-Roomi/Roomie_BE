package com.roomie.server.domain.storage;

import com.roomie.server.domain.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorageService {

    private final AmazonS3Manager amazonS3Manager;
    private final MemberRepository memberRepository;


    public String saveFile(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String filePath = "file";

        return amazonS3Manager.uploadFile(generatePathKey(filePath, uuid), file);
    }

    private String generatePathKey(String filePath, String uuid) {
        return filePath + '/' + uuid;
    }
}
