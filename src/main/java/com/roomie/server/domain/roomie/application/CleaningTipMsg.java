package com.roomie.server.domain.roomie.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CleaningTipMsg {

    FIRST(1, "청소 팁 1");

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
