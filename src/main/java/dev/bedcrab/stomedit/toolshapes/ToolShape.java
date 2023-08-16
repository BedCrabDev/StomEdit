package dev.bedcrab.stomedit.toolshapes;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.toolshapes.impl.CubicShape;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.Collection;

public class ToolShape {
    public static class ShapeTagSerializer implements TagSerializer<Mode> {
        private final Player player;
        public ShapeTagSerializer(Player player) {
            this.player = player;
        }
        @Override
        public @Nullable ToolShape.Mode read(@NotNull TagReadable reader) {
            String type = reader.getTag(Tag.String("type"));
            if (type == null) return null;
            ToolShape.Mode mode = ToolShape.Mode.valueOf(type);
            for (Tag<?> tag : mode.getRequiredTags()) if (!reader.hasTag(tag)) {
                SEUtils.message(player, mode.getHelpMessage());
                return null;
            }
            return mode;
        }
        @Override
        public void write(@NotNull TagWritable writer, @NotNull ToolShape.Mode value) {
        }
    }
    public enum Mode implements ToolShapeMode {
        CUBIC(new CubicShape()),
        ;

        private ToolShapeMode modeHandler;
        Mode(ToolShapeMode modeHandler) {
            overrideHandler(modeHandler);
        }

        public void overrideHandler(ToolShapeMode modeHandler) {
            this.modeHandler = modeHandler;
        }

        @Override
        public Collection<Tag<?>> getRequiredTags() {
            return modeHandler.getRequiredTags();
        }

        @Override
        public ToolShapeIterator iter(TagReadable tags) {
            return modeHandler.iter(tags);
        }

        @Override
        public Component getHelpMessage() {
            return modeHandler.getHelpMessage();
        }

        @Override
        public void onRightClick(Player player, Pos pos, MutableNBTCompound nbt) {
            modeHandler.onRightClick(player, pos, nbt);
        }

        @Override
        public void onLeftClick(Player player, Pos pos, MutableNBTCompound nbt) {
            modeHandler.onLeftClick(player, pos, nbt);
        }
    }
}
