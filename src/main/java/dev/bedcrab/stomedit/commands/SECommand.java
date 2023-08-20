package dev.bedcrab.stomedit.commands;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.impl.*;
import dev.bedcrab.stomedit.session.PlayerSession;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SECommand extends Command {

    public SECommand(@NotNull String name) {
        super("/"+name);
        setDefaultExecutor((sender, context) -> {
            SEUtils.message(sender, SEColorUtil.FAIL.format("Invalid syntax for /%%. Valid syntax:", context.getCommandName()));
            for (CommandSyntax syntax : getSyntaxes()) SEUtils.message(sender, SEUtils.commandToComp(context.getCommandName(), syntax));
        });
    }

    public class Syntax {
        public Syntax(@Nullable BlockTool.Mode bltoolMode, Executor executor, @NotNull Argument<?>... args) {
            boolean ignoreBLTool = bltoolMode == null;
            addSyntax((sender, ctx) -> {
                if (!(sender instanceof Player player)) return;
                try {
                    if (ignoreBLTool) executor.accept(player, ctx, PlayerSession.of(player));
                    else {
                        if (BlockTool.notBLToolItem(player.getItemInMainHand())) SEUtils.message(player, SEColorUtil.FAIL.text("You're not holding your block tool!"));
                        else {
                            PlayerSession session = bltoolMode.validate(player);
                            if (session != null) executor.accept(player, ctx, session);
                        }
                    }
                } catch (Exception e) {
                    new StomEditException(player, "An error occurred whilst executing your command!", e).sendMessage();
                }
            }, args);
        }
    }

    public interface Executor {
        void accept(Player player, CommandContext context, PlayerSession session) throws Exception;
    }

    public static class Manager {
        private final CommandManager manager;
        public Manager(CommandManager manager) {
            this.manager = manager;
        }

        public void enable() {
            manager.register(new DebugCommand());
            manager.register(new BlocktoolCommand());
            manager.register(new BLToolModeCommand());
            manager.register(new ToolShapeCommand());
            manager.register(new SetCommand());
        }
    }
}
