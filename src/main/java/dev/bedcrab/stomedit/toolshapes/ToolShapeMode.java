package dev.bedcrab.stomedit.toolshapes;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.Collection;

public interface ToolShapeMode {
    Collection<Tag<?>> getRequiredTags();
    ToolShapeIterator iter(TagReadable tags);
    Component getHelpMessage();
    void onRightClick(Player player, Pos pos, MutableNBTCompound nbt);
    void onLeftClick(Player player, Pos pos, MutableNBTCompound nbt);
}
