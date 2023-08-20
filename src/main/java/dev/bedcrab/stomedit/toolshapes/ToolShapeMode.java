package dev.bedcrab.stomedit.toolshapes;

import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.session.PlayerSession;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;

import java.util.Collection;

public interface ToolShapeMode {
    Collection<Argument<?>> modifiableParameters();
    Collection<Tag<?>> getRequiredParams();
    ToolShapeIterator iter(TagReadable params);
    Component getHintMsg();
    void onRightClick(Player player, Pos pos, PlayerSession session) throws StomEditException;
    void onLeftClick(Player player, Pos pos, PlayerSession session) throws StomEditException;
}
