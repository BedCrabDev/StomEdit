package dev.bedcrab.stomedit.blocktool;

import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.session.PlayerSession;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockToolMode {
    PlayerSession validate(Player player);
    void onUse(Block block, Point pos, Player player, @NotNull PlayerSession session) throws StomEditException;
    void onLeftClick(Block block, Point pos, Player player, @NotNull PlayerSession session) throws StomEditException;
}
