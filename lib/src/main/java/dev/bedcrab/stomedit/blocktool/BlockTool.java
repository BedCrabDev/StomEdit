package dev.bedcrab.stomedit.blocktool;

import dev.bedcrab.stomedit.blocktool.impl.BrushMode;
import dev.bedcrab.stomedit.blocktool.impl.ModifyMode;
import dev.bedcrab.stomedit.blocktool.impl.SelectMode;
import dev.bedcrab.stomedit.SEColorUtil;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventHandler;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public final class BlockTool {
    public static final ItemStack ITEM = ItemStack.builder(Objects.requireNonNull(Material.fromNamespaceId("minecraft:debug_stick")))
        .displayName(SEColorUtil.SPECIAL.text("Block tool"))
        .build().withTag(Tag.String("mode"), BlockTool.Mode.MODIFY.name());

    public static boolean isBLToolItem(ItemStack item) {
        return item.hasTag(Tag.String("mode"));
    }

    private final EventNode<Event> parentNode;
    public BlockTool(EventNode<Event> parentNode) {
        this.parentNode = parentNode;
    }

    public void enable() {
        parentNode.addChild(new SEEventHandler().eventNode());
    }

    @SuppressWarnings("NonExtendableApiUsage")
    public static class SEEventHandler implements EventHandler<PlayerEvent> {
        private final EventNode<PlayerEvent> node = EventNode.event("blocktool_event", EventFilter.PLAYER, e -> isBLToolItem(e.getPlayer().getItemInMainHand()));
        public SEEventHandler() {
            node.addListener(PlayerUseItemOnBlockEvent.class, this::onUse);
            node.addListener(PlayerBlockBreakEvent.class, this::onLeftClick);
        }
        private void onUse(PlayerUseItemOnBlockEvent event) {
            Point targetPos = event.getPlayer().getTargetBlockPosition(5);
            if (targetPos == null) return;
            Block targetBlock = event.getInstance().getBlock(targetPos);
            BlockTool.Mode mode = BlockTool.Mode.valueOf(event.getPlayer().getItemInMainHand().getTag(Tag.String("mode")));
            Block updatedBlock = mode.onUse(targetBlock, targetPos, event.getPlayer());
            event.getInstance().setBlock(targetPos, updatedBlock);
        }
        private void onLeftClick(PlayerBlockBreakEvent event) {
            event.setCancelled(true);
            BlockTool.Mode mode = BlockTool.Mode.valueOf(event.getPlayer().getItemInMainHand().getTag(Tag.String("mode")));
            Point targetPos = event.getPlayer().getTargetBlockPosition(5);
            if (targetPos == null) return;
            Block targetBlock = event.getInstance().getBlock(targetPos);
            Block updatedBlock = mode.onLeftClick(targetBlock, targetPos, event.getPlayer());
            event.getInstance().setBlock(targetPos, updatedBlock);
        }
        @Override
        public @NotNull EventNode<PlayerEvent> eventNode() {
            return node;
        }
    }

    public enum Mode implements BlockToolMode {

        BRUSH(new BrushMode()),
        MODIFY(new ModifyMode()),
        SELECT(new SelectMode()),
        ;

        private final BlockToolMode modeHandler;
        Mode(BlockToolMode modeHandler) {
            this.modeHandler = modeHandler;
        }

        @Override
        public Block onUse(Block block, Point pos, Player player) {
            return modeHandler.onUse(block, pos, player);
        }

        @Override
        public Block onLeftClick(Block block, Point pos, Player player) {
            return modeHandler.onLeftClick(block, pos, player);
        }

        @Override
        public @Nullable ItemStack validateItem(Player player) {
            return modeHandler.validateItem(player);
        }
    }
}

