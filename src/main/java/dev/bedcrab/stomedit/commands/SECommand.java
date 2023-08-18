package dev.bedcrab.stomedit.commands;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.impl.BlocktoolCommand;
import dev.bedcrab.stomedit.commands.impl.SetCommand;
import dev.bedcrab.stomedit.commands.impl.ToolShapeCommand;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public abstract class SECommand extends Command {

    public SECommand(@NotNull String name)  {
        super("/"+name);
        setDefaultExecutor((sender, context) -> {
            SEUtils.message(sender, SEColorUtil.FAIL.format("Invalid syntax for /%%. Valid syntax:", context.getCommandName()));
            for (CommandSyntax syntax : getSyntaxes()) SEUtils.message(sender, SEUtils.commandToComp(context.getCommandName(), syntax));
        });
    }

    public class Syntax {
        private final Executor executor;
        private final Argument<?>[] args;
        private final BlockTool.Mode bltoolMode;
        private boolean ignoreBLTool = false;
        public Syntax(BlockTool.Mode bltoolMode, Executor executor, @NotNull Argument<?>... args) {
            this.executor = executor;
            this.args = args;
            this.bltoolMode = bltoolMode;
            add();
        }
        public Syntax(BlockTool.Mode bltoolMode, BiConsumer<Player, CommandContext> executor, @NotNull Argument<?>... args) {
            this.executor = (player, context, item) -> executor.accept(player, context);
            this.args = args;
            this.bltoolMode = bltoolMode;
            this.ignoreBLTool = true;
            add();
        }
        private void add() {
            addSyntax((sender, ctx) -> {
                if (!(sender instanceof Player player)) return;
                if (ignoreBLTool) executor.accept(player, ctx, null);
                else {
                    if (BlockTool.notBLToolItem(player.getItemInMainHand())) return;
                    if (bltoolMode == null) executor.accept(player, ctx, player.getItemInMainHand());
                    else {
                        ItemStack item = bltoolMode.validateItem(player);
                        if (item != null) executor.accept(player, ctx, item);
                    }
                }
            }, args);
        }
    }

    public interface Executor {
        void accept(Player player, CommandContext context, ItemStack item);
    }

    public static class Manager {
        private final CommandManager manager;
        public Manager(CommandManager manager) {
            this.manager = manager;
        }

        public void enable() {
            manager.register(new BlocktoolCommand());
            manager.register(new ToolShapeCommand());
            manager.register(new SetCommand());
        }
    }
}
