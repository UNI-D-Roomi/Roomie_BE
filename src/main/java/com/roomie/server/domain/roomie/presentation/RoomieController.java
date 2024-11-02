package com.roomie.server.domain.roomie.presentation;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.roomie.application.RoomieService;
import com.roomie.server.domain.roomie.dto.FeedRoomieResponseDto;
import com.roomie.server.domain.roomie.dto.HomeResponseDto;
import com.roomie.server.domain.roomie.dto.RoomieResponseDto;
import com.roomie.server.global.config.security.SecurityService;
import com.roomie.server.global.config.security.userDetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roomie")
@RequiredArgsConstructor
public class RoomieController {

    private final RoomieService roomieService;
    private final SecurityService securityService;

    @Operation(summary = "home 화면 조회")
    @GetMapping("/home")
    public HomeResponseDto getHome(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return roomieService.getHome(member);
    }

    @Operation(summary = "방 청소 먹이주기")
    @PostMapping("/feed/room")
    public FeedRoomieResponseDto feedWithRoomClean(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader String afterRoomImage
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return roomieService.feedWithRoomClean(member, afterRoomImage);
    }

    @Operation(summary = "설거지 전 사진 등록")
    @PostMapping("/feed/wash-dish/before")
    public RoomieResponseDto beforeWashDishes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader String beforeWashImage
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return roomieService.beforeWashDishes(member, beforeWashImage);
    }

    @Operation(summary = "설거지 후 사진등록, 평가 먹이주기")
    @PostMapping("/feed/wash-dish/after")
    public FeedRoomieResponseDto feedWithWashDishes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader String afterWashImage
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return roomieService.feedWithWashDishes(member, afterWashImage);
    }

    @Operation(summary = "Roomie Ribbon 구매")
    @PutMapping("/buy-riboon")
    public RoomieResponseDto buyRoomieRibbon(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return roomieService.buyRoomieRibbon(member);
    }



}
