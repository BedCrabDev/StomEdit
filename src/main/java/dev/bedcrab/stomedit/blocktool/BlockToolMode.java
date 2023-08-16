package dev.bedcrab.stomedit.blocktool;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BlockToolMode {
    @Nullable ItemStack validateItem(Player player);
    Block onUse(Block block, Point pos, Player player);
    Block onLeftClick(Block block, Point pos, Player player);
}
