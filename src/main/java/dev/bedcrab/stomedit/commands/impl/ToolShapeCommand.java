package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.ToolShapeSessionData;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentGroup;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.Map;

public class ToolShapeCommand extends SECommand {
    private final ArgumentEnum<ToolShape.Mode> shapeArg = ArgumentType.Enum("shape", ToolShape.Mode.class);
    public ToolShapeCommand() {
        super("toolshape");
        new Syntax(null, this::call, shapeArg);
        for (Map.Entry<String, ArgumentGroup> entry : ToolShape.shapesParameters.entrySet()) {
            new Syntax(null, (player, context, session) -> System.out.println("called"), ArgumentType.Literal(entry.getKey()), entry.getValue());
        }
    }

    private void call(Player player, CommandContext context, PlayerSession session) {
        ToolShapeSessionData toolshapeData = session.read(ToolShapeSessionData.class, ToolShapeSessionData.DEFAULT);
        ToolShape.Mode newMode = context.get(shapeArg);
        if (newMode == null) for (Object v : context.getMap().values()) {
            if (v instanceof String) {
                try {
                    newMode = ToolShape.Mode.valueOf((String) v);
                } catch (Exception ignored) {}
            }
        }
        if (newMode == null) throw new IllegalArgumentException("Invalid shape!");
        toolshapeData = toolshapeData.withMode(newMode);
        session.write(toolshapeData);
        SEUtils.message(player, SEColorUtil.GENERIC.format("Using shape %%", newMode.name()));
    }

    private void callWithParams(Player player, CommandContext context, PlayerSession session) {
        //TODO: implement (ex: //toolshape DISK <radius> <vertical>)
    }
}
