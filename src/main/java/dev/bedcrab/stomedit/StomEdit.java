package dev.bedcrab.stomedit;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.PlayerSessionImpl;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventHandler;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.function.Function;

@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public final class StomEdit implements EventHandler<PlayerEvent> {
    public final static Tag<NBT> NBT_DATA_HOME = Tag.NBT("stomedit");
    public static Function<Player, PlayerSession> sessionGetter = PlayerSessionImpl::new;
    public InstanceGuardProvider igProvider;
    public BlockTool bltool;
    public ToolShape toolShape;
    public SECommand.Manager commands;
    private final EventNode<PlayerEvent> seNode;
    private final EventNode<Event> igNode;
    public StomEdit(@Nullable InstanceGuardProvider igProvider) {
        this.igProvider = igProvider;
        bltool = new BlockTool();
        toolShape = new ToolShape();
        commands = new SECommand.Manager(igProvider);
        seNode = eventNode();
        igNode = igProvider != null ? EventNode.all("instanceguard") : null;
    }
    public void enable(@NotNull EventNode<Event> rootNode, @NotNull CommandManager manager) {
        if (igProvider != null) igProvider.getInstanceGuard().enable(igNode);
        bltoolEnable();
        toolShape.enable(); // mandatory so it's not in its own method (look inside for why)
        commandsEnable(manager);
        rootNode.addChild(seNode);
        if (igNode != null) rootNode.addChild(igNode);
    }
    public void bltoolEnable() { bltool.enable(seNode); }
    public void commandsEnable(CommandManager manager) { commands.enable(manager); }
    @Override
    public @NotNull EventNode<PlayerEvent> eventNode() {
        return EventNode.event("stomedit", EventFilter.PLAYER, e -> {
            if (BlockTool.notBLToolItem(e.getPlayer().getItemInMainHand())) return false;
            if (!e.getPlayer().isCreative()) {
                SEUtils.message(e.getPlayer(), SEColorUtil.FAIL.text("Use creative mode!"));
                return false;
            }
            return true;
        });
    }
}
