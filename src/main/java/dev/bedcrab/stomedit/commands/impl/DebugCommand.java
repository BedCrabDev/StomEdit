package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.StomEdit;
import dev.bedcrab.stomedit.commands.SECommand;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class DebugCommand extends SECommand {
    public DebugCommand() {
        super("debug");
        new Syntax(null, (player, context, session) -> ((NBTCompound) player.getTag(StomEdit.NBT_DATA_HOME)).forEach((s, nbt) -> player.sendMessage(s+" = "+nbt.toSNBT())));
    }
}
