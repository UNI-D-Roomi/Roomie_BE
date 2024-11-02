package com.roomie.server.domain.roomie.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CleaningTipMsg {

    FIRST(1, "나무젓가락에 신문지를 말아서 물에 적시면 창틀을 청소할 때 좋아요!"),
    SECOND(2, "먹다 남은 소주로 전자레인지, 냉장고를 청소하면 소독효과가 있어요!"),
    THIRD(3, "베이킹소다와 식초를 넣어서 싱크대를 배수구를 청소할 수 있어요!"),
    FOURTH(4, "메이크업 브러쉬를 사용해 각종 틈새를 청소할 수 있어요!")
    ;

    private final Integer id;
    private final String msg;

    public static Integer getNumOfCleaningTipMsg() {
        return CleaningTipMsg.values().length;
    }

    public static String getMsgById(Integer id) {
        for (CleaningTipMsg msg : CleaningTipMsg.values()) {
            if (msg.getId().equals(id)) {
                return msg.getMsg();
            }
        }
        return null;
    }
}
