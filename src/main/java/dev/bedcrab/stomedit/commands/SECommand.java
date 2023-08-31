package dev.bedcrab.stomedit.commands;

import dev.bedcrab.stomedit.InstanceGuardProvider;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.impl.*;
import dev.bedcrab.stomedit.executor.JobWorker;
import dev.bedcrab.stomedit.session.PlayerSession;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public abstract class SECommand extends Command {
    public final boolean isSub;
    public final @Nullable JobWorker worker;
    public SECommand(@NotNull String name) {
        this(null, name, false);
    }
    public SECommand(String name, boolean isSub) {
        this(null, name, isSub);
    }
    public SECommand(@Nullable JobWorker worker, @NotNull String name) {
        this(worker, name, false);
    }
    public SECommand(@Nullable JobWorker worker, String name, boolean isSub) {
        super(!isSub ? "/"+name : name);
        this.worker = worker;
        this.isSub = isSub;
        if (!isSub) setDefaultExecutor((sender, context) -> {
            sender.sendMessage(context.getMap().toString());
            SEUtils.message(sender, SEColorUtil.FAIL.format("Invalid syntax for %%. Valid syntax:", "/"+context.getCommandName()));
            for (CommandSyntax syntax : getSyntaxes()) SEUtils.message(sender, SEUtils.commandToComp("/"+context.getCommandName(), syntax));
        });
    }

    @Override
    public void addSubcommand(@NotNull Command command) {
        super.addSubcommand(command);
    }

    @Override
    public @Nullable CommandExecutor getDefaultExecutor() {
        Collection<Command> subCommands = getSubcommands();
        setDefaultExecutor((sender, context) -> {
            SEUtils.message(sender, SEColorUtil.FAIL.format("Invalid syntax for %%. Valid syntax:", "/"+context.getCommandName()));
            Component prefix = Component.text(" - ");
            for (CommandSyntax syntax : getSyntaxes()) SEUtils.message(sender, prefix.append(SEUtils.commandToComp("/"+context.getCommandName(), syntax)));
            for (Command sub : subCommands) for (CommandSyntax syntax : sub.getSyntaxes()) {
                SEUtils.message(sender, prefix.append(SEUtils.commandToComp("/"+context.getCommandName()+" "+sub.getName(), syntax)));
            }
        });
        return super.getDefaultExecutor();
    }

    public class Syntax {
        public Syntax(@Nullable BlockTool.Mode bltoolMode, Executor executor, @Nullable Argument<?>... args) {
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
            }, Arrays.stream(args).filter(Objects::nonNull).toArray(Argument[]::new));
        }
    }

    @FunctionalInterface
    public interface Executor {
        void accept(Player player, CommandContext context, @NotNull PlayerSession session) throws Exception;
    }

    public static class Manager {
        private final InstanceGuardProvider igProvider;
        private final JobWorker worker;
        public Manager(JobWorker worker, InstanceGuardProvider igProvider) {
            this.worker = worker;
            this.igProvider = igProvider;
        }

        public void enable(@NotNull CommandManager manager) {
            manager.register(new DebugCommand(igProvider));
            manager.register(new BlocktoolCommand());
            manager.register(new BLToolModeCommand());
            manager.register(new ToolShapeCommand());
            if (igProvider != null) manager.register(new RegionCommand(igProvider));
            manager.register(new SetCommand(worker));
        }
    }
}
