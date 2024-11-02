package com.roomie.server.domain.roomie.application;

import com.roomie.server.domain.ai.GPTService;
import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.domain.repository.MemberRepository;
import com.roomie.server.domain.roomie.domain.Roomie;
import com.roomie.server.domain.roomie.domain.repository.RoomieRepository;
import com.roomie.server.domain.roomie.dto.CompareResponseDto;
import com.roomie.server.domain.roomie.dto.FeedRoomieResponseDto;
import com.roomie.server.domain.roomie.dto.HomeResponseDto;
import com.roomie.server.domain.roomie.dto.RoomieResponseDto;
import com.roomie.server.global.exceptions.BadRequestException;
import com.roomie.server.global.exceptions.ErrorCode;
import com.roomie.server.global.util.StaticValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoomieService {

    private final MemberRepository memberRepository;
    private final RoomieRepository roomieRepository;
    private final GPTService gptService;

    public HomeResponseDto getHome(Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = getRoomie(member);

        Random random = new Random(System.currentTimeMillis());

        /*
        0: 청소 팁
        1: feed 횟수 기반 메세지
        2: 루미 랜덤 메세지
        3: 날씨 기반 메세지
         */
        int msgCategoryRand = random.nextInt(4);// 0 <= msgRand < 4
        int msgRand;

        String roomieTalkMsg = "루미 메세지 테스트\n루미 메세지 테스트";

        switch (msgCategoryRand) {
            case 0:
                msgRand = random.nextInt(CleaningTipMsg.getNumOfCleaningTipMsg());
                roomieTalkMsg = CleaningTipMsg.getMsgById(msgRand);
                break;
            case 1:
                Long feedCount = roomie.getNumOfFeed();
                roomieTalkMsg = "루미에게 총 " + feedCount + "번 밥을 주었어요! 루미가 당신에게 호감이 생기고 있어요!";
                break;
            case 2:
                msgRand = random.nextInt(RoomieMsg.getNumOfRoomieMsg());
                roomieTalkMsg = RoomieMsg.getMsgById(msgRand);
                break;
            case 3:
                roomieTalkMsg = "날씨 기반 메세지\n날씨 기반 메세지";
                break;
        }

        return HomeResponseDto.from(member, roomie, roomieTalkMsg);
    }

    public RoomieResponseDto getCurrentRoomie(Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = getRoomie(member);

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        if (roomie.getLastFeedTime() == null) {
            roomie.setLastFeedTime(LocalDateTime.now());
        } else {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastFeedTime = roomie.getLastFeedTime();

            Duration timeDifference = Duration.between(lastFeedTime, now);

            long differenceToSeconds = timeDifference.getSeconds();

            Double hungerMinusAmount = (double) differenceToSeconds / StaticValue.HUNGER_MINUS_SECOND_STRIDE;

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

        Roomie roomie = getRoomie(member);

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        try {
            CompareResponseDto compareResponseDto = gptService.compareImages("ROOM", member.getRoomImageUrl(), afterImageUrl);

            String comment;

            if (compareResponseDto.getScore() >= StaticValue.SCORE_CUT_OFF) {
                roomie.setHungerGage(100.0);
                roomie.setLastFeedTime(LocalDateTime.now());
                member.setPoints(member.getPoints() + compareResponseDto.getScore().intValue());

                roomie.setNumOfFeed(roomie.getNumOfFeed() + 1);

                roomieRepository.save(roomie);
                memberRepository.save(member);

                comment = StaticValue.CLEAN_COMMENT + "\n" + compareResponseDto.getComment();
            } else {
                comment = StaticValue.DIRTY_COMMENT + "\n" + compareResponseDto.getComment();
            }

            return FeedRoomieResponseDto.from(roomie, compareResponseDto.getScore(), comment);
        } catch (IOException e) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER, "GPT 서비스에 문제가 발생했습니다.");
        }
    }

    @Transactional
    public RoomieResponseDto beforeWashDishes(Member member, String beforeImageUrl) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = getRoomie(member);

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

        Roomie roomie = getRoomie(member);

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        if (roomie.getWashingStartTime() == null || roomie.getBeforeWashImageUrl() == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "설거지를 하지 않았습니다.");
        }

        try {
            CompareResponseDto compareResponseDto = gptService.compareImages("WASH", roomie.getBeforeWashImageUrl(), afterImageUrl);

            String comment;

            if (compareResponseDto.getScore() >= StaticValue.SCORE_CUT_OFF) {
                roomie.setHungerGage(100.0);
                roomie.setLastFeedTime(LocalDateTime.now());
                roomie.setBeforeWashImageUrl(null);
                roomie.setWashingStartTime(null);

                roomie.setNumOfFeed(roomie.getNumOfFeed() + 1);

                member.setPoints(member.getPoints() + compareResponseDto.getScore().intValue());

                roomieRepository.save(roomie);
                memberRepository.save(member);

                comment = StaticValue.CLEAN_COMMENT + "\n" + compareResponseDto.getComment();
            } else {
                comment = StaticValue.DIRTY_COMMENT + "\n" + compareResponseDto.getComment();
            }

            return FeedRoomieResponseDto.from(roomie, compareResponseDto.getScore(), comment);
        } catch (IOException e) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER, "GPT 서비스에 문제가 발생했습니다.");
        }
    }

    @Transactional
    public RoomieResponseDto buyRoomieRibbon(Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Roomie roomie = getRoomie(member);

        if (roomie == null) {
            throw new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.");
        }

        if (member.getRoomie().getIsRibbon()) {
            throw new BadRequestException(ErrorCode.ROW_ALREADY_EXIST, "이미 리본을 가지고 있습니다.");
        }

        if (member.getPoints() < StaticValue.RIBBON_COST) {
            throw new BadRequestException(ErrorCode.NOT_ENOUGH_POINTS, "포인트가 부족합니다.");
        }

        member.setPoints(member.getPoints() - StaticValue.RIBBON_COST);
        roomie.setIsRibbon(true);

        memberRepository.save(member);
        roomieRepository.save(roomie);

        return RoomieResponseDto.from(roomie);
    }


    private Roomie getRoomie(Member member) {
        return roomieRepository.findById(member.getRoomie().getId()).orElseThrow(
                () -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 Roomie를 찾을 수 없습니다.")
        );
    }

}
