package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;

public class SelectMode implements BlockToolMode {
    @Override
    public Block onUse(Block block, Point pos, Player player) {
        final ItemStack item = player.getItemInMainHand();
        player.getInventory().setItemStack(player.getHeldSlot(), item.withTag(Tag.NBT("to"), NBT.IntArray(pos.blockX(), pos.blockY(), pos.blockZ())));
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% selection set to %%", "TO", pos.toString()));
        return block;
    }

    @Override
    public Block onLeftClick(Block block, Point pos, Player player) {
        final ItemStack item = player.getItemInMainHand();
        player.getInventory().setItemStack(player.getHeldSlot(), item.withTag(Tag.NBT("from"), NBT.IntArray(pos.blockX(), pos.blockY(), pos.blockZ())));
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% selection set to %%", "FROM", pos.toString()));
        return block;
    }

    public @Nullable ItemStack validateItem(Player player) {
        final ItemStack item = player.getItemInMainHand();
        if (!item.getTag(Tag.String("mode")).equalsIgnoreCase(BlockTool.Mode.SELECT.name())) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Use %% mode!", BlockTool.Mode.SELECT.name()));
            return null;
        }
        if (!item.hasTag(Tag.NBT("from")) && !item.hasTag(Tag.NBT("to"))) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Set %% and %% selections to select an area", "FROM (rclick)", "TO (lclick)"));
            return null;
        }
        if (!item.hasTag(Tag.NBT("from"))) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Set %% selection!", "FROM (rclick)"));
            return null;
        }
        if (!item.hasTag(Tag.NBT("to"))) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Set %% selection!", "TO (lclick)"));
            return null;
        }
        return item;
    }
}
