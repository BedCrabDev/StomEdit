package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.BLToolData;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ModifyMode implements BlockToolMode {
    @Override
    public void onUse(Block block, Point pos, Player player, @NotNull PlayerSession session) {
        final Hashtable<String, Set<String>> blockProperties = SEUtils.getAllDefaultProperties(block);
        final List<String> keys = blockProperties.keySet().stream().toList();
        if (keys.size() == 0) {
            player.sendActionBar(SEColorUtil.FAIL.translatable("item.minecraft.debug_stick.empty", Component.text(block.name())));
            return;
        }
        String currentProp = session.read(BLToolData.class, BLToolData::defaultFunc).currentProp();
        if (currentProp == null || !blockProperties.containsKey(currentProp)) currentProp = keys.get(0);
        final List<String> values = blockProperties.get(currentProp).stream().toList();
        final int valIndex = values.indexOf(block.getProperty(currentProp));

        String updatedVal;
        if (player.isSneaking() && valIndex-1 < 0) updatedVal = values.get(values.size()-1);
        else if (!player.isSneaking() && valIndex >= values.size()-1) updatedVal = values.get(0);
        else updatedVal = player.isSneaking() ? values.get(valIndex-1) : values.get(valIndex+1);

        player.sendActionBar(SEColorUtil.GENERIC.translatable("item.minecraft.debug_stick.update", Component.text(currentProp), Component.text(updatedVal)));
        player.getInstance().setBlock(pos, block.withProperty(currentProp, updatedVal));
    }

    @Override
    public void onLeftClick(Block block, Point pos, Player player, @NotNull PlayerSession session) {
        final Hashtable<String, Set<String>> blockProperties = SEUtils.getAllDefaultProperties(block);
        final List<String> keys = blockProperties.keySet().stream().toList();
        if (keys.size() == 0) {
            player.sendActionBar(SEColorUtil.FAIL.translatable("item.minecraft.debug_stick.empty", Component.text(block.name())));
            return;
        }
        final BLToolData bltoolData = session.read(BLToolData.class, BLToolData::defaultFunc);
        final int propIndex = keys.indexOf(bltoolData.currentProp());
        String updatedProp;
        if (bltoolData.currentProp() == null || !blockProperties.containsKey(bltoolData.currentProp()) || (!player.isSneaking() && propIndex >= keys.size()-1)) updatedProp = keys.get(0);
        else if (player.isSneaking() && propIndex-1 < 0) updatedProp = keys.get(keys.size()-1);
        else updatedProp = player.isSneaking() ? keys.get(propIndex-1) : keys.get(propIndex+1);
        player.sendActionBar(SEColorUtil.GENERIC.translatable("item.minecraft.debug_stick.select", Component.text(updatedProp), Component.text(block.getProperty(updatedProp))));
        session.write(bltoolData.withCurrentProp(updatedProp));
    }

    @Override
    public PlayerSession validate(Player player) {
        PlayerSession session = PlayerSession.of(player);
        BLToolData bltoolData = session.read(BLToolData.class, BLToolData::defaultFunc);
        if (bltoolData.num() != BlockTool.Mode.MODIFY.ordinal()) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Use %% mode!", BlockTool.Mode.MODIFY.name()));
            return null;
        }
        return session;
    }
}
