package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.session.PlayerSession;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class BrushMode implements BlockToolMode {
    @Override
    public void onUse(Block block, Point pos, Player player, @NotNull PlayerSession data) {
        player.sendMessage(SEColorUtil.SPECIAL.text("not implemented!"));
    }

    @Override
    public void onLeftClick(Block block, Point pos, Player player, @NotNull PlayerSession data) {
        player.sendMessage(SEColorUtil.SPECIAL.text("not implemented!"));
    }

    @Override
    public PlayerSession validate(Player player) {
        return null;
    }
}
