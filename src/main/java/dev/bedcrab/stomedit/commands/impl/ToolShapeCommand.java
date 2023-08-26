package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.ToolShapeSessionData;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentGroup;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.Map;

public class ToolShapeCommand extends SECommand {
    public ToolShapeCommand() {
        super("toolshape");
        for (Map.Entry<Integer, ArgumentGroup> entry : ToolShape.shapesParameters.entrySet()) {
            new Syntax(null, this::call, ArgumentType.Literal(ToolShape.Mode.values()[entry.getKey()].name().toLowerCase()), entry.getValue());
        }
    }

    private void call(Player player, CommandContext context, PlayerSession session) {
        ToolShapeSessionData data = session.read(ToolShapeSessionData.class, ToolShapeSessionData.DEFAULT);
        ToolShape.Mode newMode = null;
        for (Object v : context.getMap().values()) if (v instanceof String s) {
            try { newMode = ToolShape.Mode.valueOf(s.toUpperCase()); } catch (Exception ignored) {}
        }
        if (newMode == null) throw new IllegalArgumentException("Invalid shape!");
        data = data.withMode(newMode);
        SEUtils.message(player, SEColorUtil.GENERIC.format("Using shape %%", newMode.name()));

        CommandContext params = context.get("parameters");
        if (params == null) {
            session.write(data);
            return;
        }
        MutableNBTCompound nbt = NBTCompound.EMPTY.toMutableCompound();
        Tag.View(new SEUtils.ArgumentsToTagSerializer()).write(nbt, params);
        data = data.withParams(nbt);
        session.write(data);
    }
}
