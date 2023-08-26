package dev.bedcrab.stomedit.blocktool;

import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.impl.BrushMode;
import dev.bedcrab.stomedit.blocktool.impl.ModifyMode;
import dev.bedcrab.stomedit.blocktool.impl.SelectMode;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.BLToolSessionData;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public final class BlockTool {
    public static final ItemStack ITEM = ItemStack.builder(Objects.requireNonNull(Material.fromNamespaceId("minecraft:debug_stick")))
        .displayName(SEColorUtil.SPECIAL.text("Block tool"))
        .set(Tag.Boolean("isBltool"), true)
        .build();
    public static boolean notBLToolItem(ItemStack item) {
        return !item.hasTag(Tag.Boolean("isBltool")) || !item.getTag(Tag.Boolean("isBltool"));
    }

    public BlockTool() {}
    public void enable(EventNode<PlayerEvent> rootNode) {
        rootNode.addListener(PlayerUseItemOnBlockEvent.class, this::onUse);
        rootNode.addListener(PlayerBlockBreakEvent.class, this::onLeftClick);
    }
    private void onUse(PlayerUseItemOnBlockEvent event) {
        Point targetPos = event.getPlayer().getTargetBlockPosition(5);
        if (targetPos == null) return;
        Block targetBlock = event.getInstance().getBlock(targetPos);
        try {
            PlayerSession session = PlayerSession.of(event.getPlayer());
            BlockTool.Mode mode = session.read(BLToolSessionData.class, BLToolSessionData.DEFAULT).parseMode();
            mode.onUse(targetBlock, targetPos, event.getPlayer(), session);
        } catch (Exception e) {
            if (e instanceof StomEditException se) se.sendMessage();
            else new StomEditException(event.getPlayer(), "Error occurred whilst handling event!", e).sendMessage();
        }
    }
    private void onLeftClick(PlayerBlockBreakEvent event) {
        event.setCancelled(true);
        Point targetPos = event.getPlayer().getTargetBlockPosition(5);
        if (targetPos == null) return;
        Block targetBlock = event.getInstance().getBlock(targetPos);
        try {
            PlayerSession session = PlayerSession.of(event.getPlayer());
            BlockTool.Mode mode = session.read(BLToolSessionData.class, BLToolSessionData.DEFAULT).parseMode();
            mode.onLeftClick(targetBlock, targetPos, event.getPlayer(), session);
        } catch (Exception e) {
            if (e instanceof StomEditException se) se.sendMessage();
            else new StomEditException(event.getPlayer(), "Error occurred whilst handling event!", e).sendMessage();
        }
    }

    public enum Mode implements BlockToolMode {

        BRUSH(new BrushMode()),
        MODIFY(new ModifyMode()),
        SELECT(new SelectMode()),
        ;

        private BlockToolMode modeHandler;
        Mode(BlockToolMode modeHandler) {
            overrideHandler(modeHandler);
        }

        public void overrideHandler(BlockToolMode newMode) {
            this.modeHandler = newMode;
        }

        @Override
        public void onUse(Block block, Point pos, Player player, PlayerSession session) {
            modeHandler.onUse(block, pos, player, session);
        }

        @Override
        public void onLeftClick(Block block, Point pos, Player player, PlayerSession session) {
            modeHandler.onLeftClick(block, pos, player, session);
        }

        @Override
        public PlayerSession validate(Player player) {
            return modeHandler.validate(player);
        }
    }
}

