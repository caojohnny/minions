package com.gmail.woodyc40.minions.menu;

import com.gmail.woodyc40.minions.Minion;

public interface MenuFactory {
    MinionsMenu newMinionsMenu(Minion minion);
}
