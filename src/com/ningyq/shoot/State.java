package com.ningyq.shoot;

/**
 * @author nuanyang
 * @date 2022/12/08
 */
public enum State {
    START(0),
    RUNNING(1),
    PAUSE(2),
    GAME_OVER(3);

    int value;

    State(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
