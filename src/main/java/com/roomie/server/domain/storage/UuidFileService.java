package com.roomie.server.domain.storage;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.domain.repository.MemberRepository;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import com.roomie.server.global.dtoMapper.MemberDtoMapper;
import com.roomie.server.global.exceptions.BadRequestException;
import com.roomie.server.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UuidFileService {

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
