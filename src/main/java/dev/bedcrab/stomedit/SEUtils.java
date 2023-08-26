package dev.bedcrab.stomedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.*;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class SEUtils {
    public static void message(CommandSender sender, Component text) {
        sender.sendMessage(SEColorUtil.SPECIAL.text("[StomEdit]").appendSpace().append(text));
    }
    public static Hashtable<String, Set<String>> getAllDefaultProperties(Block block) {
        final Hashtable<String, Set<String>> blockProperties = new Hashtable<>();
        for (Block possibleState : block.possibleStates()) {
            for (Map.Entry<String, String> entry : possibleState.properties().entrySet()) {
                blockProperties.putIfAbsent(entry.getKey(), new HashSet<>());
                blockProperties.get(entry.getKey()).add(entry.getValue());
            }
        }
        return blockProperties;
    }
    public static Component pointToComp(Point point) {
        return SEColorUtil.GENERIC.text("["+point.blockX() +", "+point.blockY() +", "+point.blockZ()+"]");
    }
    public static Component commandToComp(String name, CommandSyntax syntax) {
        TextComponent.Builder text = Component.text();
        text.append(SEColorUtil.GENERIC.text(name));
        for (Argument<?> arg : syntax.getArguments()) {
            if (arg instanceof ArgumentGroup group) {
                text.appendSpace();
                if (arg.isOptional()) text.append(SEColorUtil.GENERIC.text("["));
                for (int i = 0; i < group.group().size(); i++) {
                    Argument<?> gArg = group.group().get(i);
                    if (gArg instanceof ArgumentLiteral) text.append(SEColorUtil.GENERIC.text(gArg.getId()));
                    else text.append(SEColorUtil.GENERIC.format(gArg.isOptional() ? "[%%]" : "<%%>", gArg.getId()));
                    if (i != group.group().size()-1) text.appendSpace();
                }
                if (arg.isOptional()) text.append(SEColorUtil.GENERIC.text("]"));
                continue;
            }
            text.appendSpace();
            if (arg instanceof ArgumentLiteral) text.append(SEColorUtil.GENERIC.text(arg.getId()));
            else text.append(SEColorUtil.GENERIC.format(arg.isOptional() ? "[%%]" : "<%%>", arg.getId()));
        }
        return text.build();
    }

    //TODO: use this for modifiable parameters
    public static class ArgumentsToTagSerializer implements TagSerializer<CommandContext> {
        @Nullable
        @Override
        public CommandContext read(@NotNull TagReadable reader) {
            return null;
        }

        @Override
        public void write(@NotNull TagWritable writer, @NotNull CommandContext ctx) {
            for (Map.Entry<String, Object> entry : ctx.getMap().entrySet()) {
                String id = entry.getKey(); Object val = entry.getValue();
                if (val instanceof String) writer.setTag(Tag.String(id), (String) val);
                else if (val instanceof Integer) writer.setTag(Tag.Integer(id), (int) val);
                else if (val instanceof Boolean) writer.setTag(Tag.Boolean(id), (boolean) val);
            }
        }
    }
}
