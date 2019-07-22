package com.gmail.woodyc40.minions.mode;

import com.gmail.woodyc40.minions.Minion;

public interface Mode {
    void init(Minion minion);

    void tick(Minion minion);

    void complete(Minion minion);

    ModeType getType();
}
