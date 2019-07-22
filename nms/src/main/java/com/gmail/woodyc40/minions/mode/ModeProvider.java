package com.gmail.woodyc40.minions.mode;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ModeProvider {
    @Nullable
    Mode provideMode(ModeType typeName);
}
