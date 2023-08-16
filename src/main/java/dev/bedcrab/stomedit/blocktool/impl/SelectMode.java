package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

public class SelectMode implements BlockToolMode {
    @Override
    public Block onUse(Block block, Point pos, Player player) {
        final ItemStack item = player.getItemInMainHand();
        NBTCompound shapeNBT = (NBTCompound) item.getTag(Tag.NBT("shape"));
        if (shapeNBT == null) shapeNBT = NBT.Compound(newNBT -> {
            ToolShape.Mode defaultMode = ToolShape.Mode.CUBIC;
            newNBT.set("type", NBT.String(defaultMode.name()));
            defaultMode.onRightClick(player, new Pos(pos), newNBT);
        });
        else {
            MutableNBTCompound newNBT = shapeNBT.toMutableCompound();
            ToolShape.Mode mode = ToolShape.Mode.valueOf(newNBT.getString("type"));
            mode.onRightClick(player, new Pos(pos), newNBT);
            shapeNBT = newNBT.toCompound();
        }
        player.getInventory().setItemStack(player.getHeldSlot(), item.withTag(Tag.NBT("shape"), shapeNBT));
        return block;
    }

    @Override
    public Block onLeftClick(Block block, Point pos, Player player) {
        final ItemStack item = player.getItemInMainHand();
        NBTCompound shapeNBT = (NBTCompound) item.getTag(Tag.NBT("shape"));
        if (shapeNBT == null) shapeNBT = NBT.Compound(newNBT -> {
            ToolShape.Mode defaultMode = ToolShape.Mode.CUBIC;
            newNBT.set("type", NBT.String(defaultMode.name()));
            defaultMode.onLeftClick(player, new Pos(pos), newNBT);
        });
        else {
            MutableNBTCompound newNBT = shapeNBT.toMutableCompound();
            ToolShape.Mode mode = ToolShape.Mode.valueOf(newNBT.getString("type"));
            mode.onLeftClick(player, new Pos(pos), newNBT);
            shapeNBT = newNBT.toCompound();
        }
        player.getInventory().setItemStack(player.getHeldSlot(), item.withTag(Tag.NBT("shape"), shapeNBT));
        return block;
    }

    public @Nullable ItemStack validateItem(Player player) {
        final ItemStack item = player.getItemInMainHand();
        if (!item.getTag(Tag.String("mode")).equalsIgnoreCase(BlockTool.Mode.SELECT.name())) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Use %% mode!", BlockTool.Mode.SELECT.name()));
            return null;
        }
        if (!item.hasTag(Tag.Structure("shape", new ToolShape.ShapeTagSerializer(player)))) return null;
        return item;
    }
}
