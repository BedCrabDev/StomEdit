package dev.bedcrab.stomedit.toolshapes;

import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.session.PlayerSession;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ToolShapeMode {
    Collection<Argument<?>> shapeVariables();
    Collection<Tag<?>> getRequiredParams();
    ToolShapeIterator iter(TagReadable params);
    Component getHintMsg();
    void onRightClick(@NotNull Player player, Pos pos, @NotNull PlayerSession session) throws StomEditException;
    void onLeftClick(@NotNull Player player, Pos pos, @NotNull PlayerSession session) throws StomEditException;
}
