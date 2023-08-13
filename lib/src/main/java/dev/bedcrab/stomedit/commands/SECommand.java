package dev.bedcrab.stomedit.commands;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public abstract class SECommand extends Command {

    public final String syntax;
    private final BlockTool.Mode bltoolMode;
    public SECommand(@NotNull String name, String syntax, @Nullable BlockTool.Mode bltoolMode)  {
        super("/"+name);
        this.syntax = "// " +  name + " " + syntax;
        this.bltoolMode = bltoolMode;
        setDefaultExecutor((s, c) -> SECommandManager.invalidCommand(s, c , this.syntax));
    }

    public void SE_addSyntax(@NotNull SECommandExecutor executor, @NotNull Argument<?>... args) {
        super.addSyntax((sender, ctx) -> {
            if (!(sender instanceof Player player) || bltoolMode == null || !BlockTool.isBLToolItem(player.getItemInMainHand())) return;
            ItemStack item = bltoolMode.validateItem(player);
            if (item != null) executor.accept(player, ctx, item);
        }, args);
    }

    public void SE_addSyntax(@NotNull BiConsumer<Player, CommandContext> executor, @NotNull Argument<?>... args) {
        super.addSyntax((sender, ctx) -> {
            if (!(sender instanceof Player player)) return;
            executor.accept(player, ctx);
        }, args);
    }
}
