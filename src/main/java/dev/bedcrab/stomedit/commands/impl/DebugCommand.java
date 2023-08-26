package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.InstanceGuardProvider;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEdit;
import dev.bedcrab.stomedit.commands.SECommand;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class DebugCommand extends SECommand {
    public DebugCommand(InstanceGuardProvider igProvider) {
        super("debug");
        new Syntax(null, (player, context, session) -> {
            SEUtils.message(player, SEColorUtil.GENERIC.text("Player session data:"));
            ((NBTCompound) player.getTag(StomEdit.NBT_DATA_HOME)).forEach((s, nbt) -> player.sendMessage(" - "+s+" = "+nbt.toSNBT()));
            player.sendMessage("InstanceGuard region: "+igProvider.getInstanceGuard().getRegionManager().getRegion(player.getPosition(), player.getInstance()).getName());
        });
    }
}
