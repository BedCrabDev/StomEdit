package dev.bedcrab.stomedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.*;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class SEUtils {
    public static void message(@NotNull CommandSender sender, Component text) {
        sender.sendMessage(SEColorUtil.SPECIAL.text("[StomEdit]").appendSpace().append(text));
    }
    @Contract(pure = true)
    public static @NotNull String plural(int num, String one, String multi) {
        return num != 1 ? " "+multi : " "+one;
    }
    public static @NotNull Hashtable<String, Set<String>> getAllDefaultProperties(@NotNull Block block) {
        final Hashtable<String, Set<String>> blockProperties = new Hashtable<>();
        for (Block possibleState : block.possibleStates()) {
            for (Map.Entry<String, String> entry : possibleState.properties().entrySet()) {
                blockProperties.putIfAbsent(entry.getKey(), new HashSet<>());
                blockProperties.get(entry.getKey()).add(entry.getValue());
            }
        }
        return blockProperties;
    }
    public static @NotNull Component commandToComp(String name, @NotNull CommandSyntax syntax) {
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
    @Contract(pure = true)
    public static @NotNull NBTCompound emptyCompound() {
        return NBT.Compound(b -> b.set("_", NBT.Byte(Byte.MAX_VALUE)));
    }
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
    public record BlockPos(int x, int y, int z) {
        public BlockPos(@NotNull Point p) {
            this(p.blockX(), p.blockY(), p.blockZ());
        }
        @Contract(value = " -> new", pure = true)
        public @NotNull Pos pos() {
            return new Pos(x, y, z);
        }
        @Contract("_ -> new")
        public @NotNull BlockPos withX(int x) {
            return new BlockPos(x, y, z);
        }
        @Contract("_ -> new")
        public @NotNull BlockPos withY(int y) {
            return new BlockPos(x, y, z);
        }
        @Contract("_ -> new")
        public @NotNull BlockPos withZ(int z) {
            return new BlockPos(x, y, z);
        }
        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "["+x+", "+y+", "+z+"]";
        }
    }
}
