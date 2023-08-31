package dev.bedcrab.stomedit.session.impl;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.session.SessionData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;

public record BLToolData(int num, String currentProp) implements SessionData {
    public static @NotNull NBT defaultFunc(@NotNull String missing) {
        return switch (missing) {
            case "num" -> NBT.Int(BlockTool.Mode.SELECT.ordinal());
            case "currentProp" -> NBT.String("");
            default -> throw new IllegalStateException("Unexpected value: " + missing);
        };
    }
    @Contract("_ -> new")
    public @NotNull BLToolData withCurrentProp(String newCurrentProp) {
        return new BLToolData(num, newCurrentProp);
    }
    @Contract("_ -> new")
    public @NotNull BLToolData withMode(BlockTool.@NotNull Mode newMode) {
        return new BLToolData(newMode.ordinal(), currentProp);
    }
    public @NotNull BlockTool.Mode parseMode() {
        return BlockTool.Mode.values()[num];
    }
}
