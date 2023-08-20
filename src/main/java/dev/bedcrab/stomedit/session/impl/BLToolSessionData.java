package dev.bedcrab.stomedit.session.impl;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.session.SessionData;
import org.jetbrains.annotations.NotNull;

public record BLToolSessionData(int num, String currentProp) implements SessionData {
    public final static BLToolSessionData DEFAULT = new BLToolSessionData(BlockTool.Mode.SELECT.ordinal(), "");
    public BLToolSessionData withCurrentProp(String newCurrentProp) {
        return new BLToolSessionData(num, newCurrentProp);
    }
    public BLToolSessionData withMode(BlockTool.Mode newMode) {
        return new BLToolSessionData(newMode.ordinal(), currentProp);
    }
    public @NotNull BlockTool.Mode parseMode() {
        return BlockTool.Mode.values()[num];
    }
}
