package com.roomie.server.domain.roomie.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomieMsg {

    FIRST(1, "루미는 더러운 곳에서 힘이 빠져요 :("),
    SECOND(2, "루미를 오래 키울 수록 루미는 당신을 더 사랑해요!"),
    THIRD(3, "루미는 깨끗한 기운을 먹고 자라요!")
    ;

    private final Integer id;
    private final String msg;

    public static Integer getNumOfRoomieMsg() {
        return RoomieMsg.values().length;
    }

    public static String getMsgById(Integer id) {
        for (RoomieMsg msg : RoomieMsg.values()) {
            if (msg.getId().equals(id)) {
                return msg.getMsg();
            }
        }
        return null;
    }
}
