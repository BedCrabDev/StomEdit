package dev.bedcrab.stomedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentBoolean;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
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
        return SEColorUtil.GENERIC.format("%%, %%, %%", String.valueOf(point.x()), String.valueOf(point.y()), String.valueOf(point.z()));
    }
    public static Component commandToComp(String name, CommandSyntax syntax) {
        TextComponent.Builder text = Component.text();
        text.append(SEColorUtil.GENERIC.text("/"+name));
        for (Argument<?> arg : syntax.getArguments()) {
            text.appendSpace();
            text.append(SEColorUtil.GENERIC.format(arg instanceof ArgumentLiteral ? "%%" : arg.isOptional() ? "[%%]" : "<%%>", arg.getId()));
        }
        return text.build();
    }

    //TODO: use this for modifiable parameters
    public static class ArgumentToTagSerializer implements TagSerializer<Argument<?>> {
        private final CommandContext context;
        public ArgumentToTagSerializer(CommandContext context) {
            this.context = context;
        }

        @Nullable
        @Override
        public Argument<?> read(@NotNull TagReadable reader) {
            return null;
        }

        @Override
        public void write(@NotNull TagWritable writer, @NotNull Argument<?> value) {
            Object val = context.get(value);
            if (value instanceof ArgumentString) writer.setTag(Tag.String(value.getId()), (String) val);
            else if (value instanceof ArgumentInteger) writer.setTag(Tag.Integer(value.getId()), (int) val);
            else if (value instanceof ArgumentBoolean) writer.setTag(Tag.Boolean(value.getId()), (boolean) val);
        }
    }
}
