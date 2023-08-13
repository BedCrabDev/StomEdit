package dev.bedcrab.stomedit.blocktool;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BlockToolMode {
    Block onUse(Block block, Point pos, Player player);
    default Block onLeftClick(Block block, Point pos, Player player) {
        return block;
    }

    @Nullable ItemStack validateItem(Player player);
}
