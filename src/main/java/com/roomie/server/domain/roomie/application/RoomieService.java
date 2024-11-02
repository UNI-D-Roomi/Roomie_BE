package com.roomie.server.domain.roomie.application;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.domain.repository.MemberRepository;
import com.roomie.server.domain.roomie.domain.Roomie;
import com.roomie.server.domain.roomie.domain.repository.RoomieRepository;
import com.roomie.server.domain.roomie.dto.CompareResponseDto;
import com.roomie.server.domain.roomie.dto.FeedRoomieResponseDto;
import com.roomie.server.domain.roomie.dto.RoomieResponseDto;
import com.roomie.server.global.exceptions.BadRequestException;
import com.roomie.server.global.exceptions.ErrorCode;
import com.roomie.server.global.util.StaticValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RoomieService {

    private final MemberRepository memberRepository;
    private final RoomieRepository roomieRepository;

    public RoomieResponseDto getCurrentRoomie(Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = member.getRoomie();

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        if (roomie.getLastFeedTime() == null) {
            roomie.setLastFeedTime(LocalDateTime.now());
        } else {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastFeedTime = roomie.getLastFeedTime();

            Duration timeDifference = Duration.between(lastFeedTime, now);

            Long differenceToSeconds = timeDifference.getSeconds();

            Long hungerMinusAmount = differenceToSeconds / StaticValue.HUNGER_MINUS_SECOND_STRIDE;

            double roomieHungerGage = roomie.getHungerGage() - hungerMinusAmount;

            roomie.setHungerGage(
                    Math.max(roomieHungerGage, 0.0)
            );
        }

        roomieRepository.save(roomie);

        return RoomieResponseDto.from(roomie);
    }

    @Transactional
    public FeedRoomieResponseDto feedWithRoomClean(Member member, String afterImageUrl) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = member.getRoomie();

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        // TODO: GPT 연결
        CompareResponseDto compareResponseDto = null;

        if (compareResponseDto.getScore() >= StaticValue.SCORE_CUT_OFF) {
            roomie.setHungerGage(100.0);
            roomie.setLastFeedTime(LocalDateTime.now());
        }


        roomie.setHungerGage(100.0);
        roomie.setLastFeedTime(LocalDateTime.now());
        roomie.setBeforeWashImageUrl(null);
        roomie.setIsRibbon(false);
        roomie.setWashingStartTime(null);

        roomieRepository.save(roomie);

        return FeedRoomieResponseDto.from(roomie, StaticValue.ROOM_FEED_COMMENT + "\n" + compareResponseDto.getComment());
    }

    @Transactional
    public RoomieResponseDto beforeWashDishes(Member member, String beforeImageUrl) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = member.getRoomie();

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        roomie.setBeforeWashImageUrl(beforeImageUrl);
        roomie.setWashingStartTime(LocalDateTime.now());

        roomieRepository.save(roomie);

        return RoomieResponseDto.from(roomie);
    }

    @Transactional
    public FeedRoomieResponseDto feedWithWashDishes(Member member, String afterImageUrl) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = member.getRoomie();

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        if (roomie.getWashingStartTime() == null || roomie.getBeforeWashImageUrl() == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "설거지를 하지 않았습니다.");
        }

        // TODO: GPT 연결
        CompareResponseDto compareResponseDto = null;

        if (compareResponseDto.getScore() >= StaticValue.SCORE_CUT_OFF) {
            roomie.setHungerGage(100.0);
            roomie.setLastFeedTime(LocalDateTime.now());
            roomie.setBeforeWashImageUrl(null);
            roomie.setWashingStartTime(null);
        }

        roomieRepository.save(roomie);

        return FeedRoomieResponseDto.from(roomie, StaticValue.WASH_DISHES_FEED_COMMENT + "\n" + compareResponseDto.getComment());
    }

    @Transactional
    public RoomieResponseDto buyRoomieRibbon(Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = member.getRoomie();

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        if (member.getPoints() < StaticValue.RIBBON_COST) {
            throw new BadRequestException(ErrorCode.NOT_ENOUGH_POINTS, "포인트가 부족합니다.");
        }

        roomie.setIsRibbon(true);

        roomieRepository.save(roomie);

        return RoomieResponseDto.from(roomie);
    }




}
