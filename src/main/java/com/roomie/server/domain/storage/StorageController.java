package com.roomie.server.domain.storage;

import com.roomie.server.global.config.security.SecurityService;
import com.roomie.server.global.config.security.userDetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storage")
@Tag(name = "Storage", description = "Storage API")
public class StorageController {

    private final StorageService uuidFileService;
    private final SecurityService securityService;

    @Operation(summary = "File 단순 업로드(다용도)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveFile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(name = "file") MultipartFile file
    ) {
        securityService.getUserByUserDetails(userDetails);

        return uuidFileService.saveFile(file);
    }


}
