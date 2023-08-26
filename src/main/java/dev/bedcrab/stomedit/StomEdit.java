package dev.bedcrab.stomedit;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventHandler;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;

@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public final class StomEdit implements EventHandler<PlayerEvent> {
    public final static Tag<NBT> NBT_DATA_HOME = Tag.NBT("stomedit");
    public InstanceGuardProvider igProvider;
    public BlockTool bltool;
    public ToolShape toolShape;
    public SECommand.Manager commands;
    private final EventNode<Event> igNode;
    private final EventNode<PlayerEvent> seNode;
    public StomEdit(InstanceGuardProvider igProvider) {
        this.igProvider = igProvider;
        bltool = new BlockTool();
        toolShape = new ToolShape();
        commands = new SECommand.Manager(igProvider);
        igNode = EventNode.all("instanceguard");
        seNode = eventNode();
    }
    public void enable(EventNode<Event> rootNode, CommandManager manager) {
        igProvider.getInstanceGuard().enable(igNode);
        bltoolEnable();
        toolShape.enable(); // mandatory so it's not in its own method (look inside for why)
        commandsEnable(manager);
        rootNode.addChild(igNode);
        rootNode.addChild(seNode);
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
