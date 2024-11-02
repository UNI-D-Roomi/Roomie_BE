package com.roomie.server.domain.roomie.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomieMsg {

    FIRST(1, "루미 팁 1");

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
