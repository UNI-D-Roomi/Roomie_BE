package com.roomie.server.domain.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.roomie.server.domain.storage.config.AwsS3Config;
import com.roomie.server.global.exceptions.InternalServerException;
import com.roomie.server.global.util.StaticValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.roomie.server.global.exceptions.ErrorCode.FILE_DELETE_FAIL;
import static com.roomie.server.global.exceptions.ErrorCode.FILE_UPLOAD_FAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;
    private final AwsS3Config awsS3Config;

    public String uploadFile(String keyName, MultipartFile file) {
        // 목표 파일 크기 (예: 500KB)
        long targetSizeInKB = StaticValue.IMAGE_FILE_SIZE_LIMIT_IN_KB;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        double quality = 1.0;

        try {
            // 이미지 압축
            while (true) {
                outputStream.reset();
                Thumbnails.of(file.getInputStream())
                        .size(400, 400) // 필요한 경우 크기를 제한할 수 있습니다.
                        .outputQuality(quality)
                        .toOutputStream(outputStream);

                // 압축한 파일의 크기 체크
                long compressedSizeInKB = outputStream.size() / 1024;
                if (compressedSizeInKB <= targetSizeInKB) {
                    break;
                }

                // 품질을 줄여가며 목표 크기에 맞춰 압축
                quality -= 0.1;
                log.info("quality: {}, compressedSizeInKB: {}", quality, compressedSizeInKB);
                /*
                if (quality < 0.1) {
                    // 품질이 너무 낮아지지 않도록 제한
                    break;
                }

                 */
            }

            // 압축된 파일을 S3에 업로드
            byte[] compressedImageData = outputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedImageData);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(compressedImageData.length);
            objectMetadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(awsS3Config.getBucket(), keyName, inputStream, objectMetadata));
            return amazonS3.getUrl(awsS3Config.getBucket(), keyName).toString();

        } catch (IOException e) {
            throw new InternalServerException(FILE_UPLOAD_FAIL, "이미지 압축 중 에러 발생: " + e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException(FILE_UPLOAD_FAIL, "S3에 파일 업로드를 실패했습니다: " + e.getMessage());
        }
    }

    public void deleteFile(String keyName) {
        try {
            amazonS3.deleteObject(awsS3Config.getBucket(), keyName);
        } catch (Exception e) {
            throw new InternalServerException(FILE_DELETE_FAIL, "S3에 파일 삭제를 실패했습니다: " + e.getMessage());
        }
    }
}
