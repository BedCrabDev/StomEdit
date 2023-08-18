package dev.bedcrab.stomedit;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

public final class StomEdit {

    public BlockTool bltool;
    public SECommand.Manager commands;
    public StomEdit(EventNode<Event> parentEventNode, CommandManager commandManager) {
        this.bltool = new BlockTool(parentEventNode);
        this.commands = new SECommand.Manager(commandManager);
    }

    public void enable() {
        bltoolEnable();
        commandsEnable();
    }

    public void bltoolEnable() {
        bltool.enable();
    }

    public void commandsEnable() {
        commands.enable();
    }
}
