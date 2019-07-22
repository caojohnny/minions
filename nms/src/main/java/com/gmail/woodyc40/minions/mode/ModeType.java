package com.gmail.woodyc40.minions.mode;

public enum ModeType {
    NONE {
        @Override
        public ModeType getNext() {
            return MINING;
        }
    },
    MINING {
        @Override
        public ModeType getNext() {
            return NONE;
        }
    };

    public abstract ModeType getNext();
}
