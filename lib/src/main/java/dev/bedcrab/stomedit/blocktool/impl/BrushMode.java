package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BrushMode implements BlockToolMode {
    @Override
    public Block onUse(Block block, Point pos, Player player) {
        player.sendMessage(SEColorUtil.SPECIAL.text("not implemented!"));
        return block;
    }

    @Override
    public @Nullable ItemStack validateItem(Player player) {
        return null;
    }
}
