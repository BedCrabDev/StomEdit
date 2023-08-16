package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ModifyMode implements BlockToolMode {
    @Override
    public Block onUse(Block block, Point pos, Player player) {
        final ItemStack item = player.getItemInMainHand();
        final Hashtable<String, Set<String>> blockProperties = SEUtils.getAllDefaultProperties(block);
        final List<String> keys = blockProperties.keySet().stream().toList();
        if (keys.size() == 0) {
            player.sendActionBar(SEColorUtil.FAIL.translatable("item.minecraft.debug_stick.empty", Component.text(block.name())));
            return block;
        }
        String currentProp = item.getTag(Tag.String("current_prop"));
        if (currentProp == null || !blockProperties.containsKey(currentProp)) currentProp = keys.get(0);
        final List<String> values = blockProperties.get(currentProp).stream().toList();
        final int valIndex = values.indexOf(block.getProperty(currentProp));

        String updatedVal;
        if (player.isSneaking() && valIndex-1 < 0) updatedVal = values.get(values.size()-1);
        else if (!player.isSneaking() && valIndex >= values.size()-1) updatedVal = values.get(0);
        else updatedVal = player.isSneaking() ? values.get(valIndex-1) : values.get(valIndex+1);

        player.sendActionBar(SEColorUtil.GENERIC.translatable("item.minecraft.debug_stick.update", Component.text(currentProp), Component.text(updatedVal)));
        return block.withProperty(currentProp, updatedVal);
    }

    @Override
    public Block onLeftClick(Block block, Point pos, Player player) {
        final ItemStack item = player.getItemInMainHand();
        final Hashtable<String, Set<String>> blockProperties = SEUtils.getAllDefaultProperties(block);
        final String currentProp = item.getTag(Tag.String("current_prop"));
        final List<String> keys = blockProperties.keySet().stream().toList();
        if (keys.size() == 0) {
            player.sendActionBar(SEColorUtil.FAIL.translatable("item.minecraft.debug_stick.empty", Component.text(block.name())));
            return block;
        }
        final int propIndex = keys.indexOf(currentProp);

        String updatedProp;
        if (currentProp == null || !blockProperties.containsKey(currentProp) || (!player.isSneaking() && propIndex >= keys.size()-1)) updatedProp = keys.get(0);
        else if (player.isSneaking() && propIndex-1 < 0) updatedProp = keys.get(keys.size()-1);
        else updatedProp = player.isSneaking() ? keys.get(propIndex-1) : keys.get(propIndex+1);

        player.getInventory().setItemStack(player.getHeldSlot(), item.withTag(Tag.String("current_prop"), updatedProp));
        player.sendActionBar(SEColorUtil.GENERIC.translatable("item.minecraft.debug_stick.select", Component.text(updatedProp), Component.text(block.getProperty(updatedProp))));
        return block;
    }

    @Override
    public @Nullable ItemStack validateItem(Player player) {
        final ItemStack item = player.getItemInMainHand();
        if (!item.getTag(Tag.String("mode")).equalsIgnoreCase(BlockTool.Mode.MODIFY.name())) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Use %% mode!", BlockTool.Mode.SELECT.name()));
            return null;
        }
        if (!item.hasTag(Tag.String("current_prop"))) {
            SEUtils.message(player, SEColorUtil.FAIL.text("Right click a block to select a property to modify"));
            return null;
        }
        return item;
    }
}
