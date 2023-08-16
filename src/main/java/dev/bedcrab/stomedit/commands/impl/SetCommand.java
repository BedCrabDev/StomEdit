package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagHandler;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class SetCommand extends SECommand {
    private final ArgumentBlockState blockArg = ArgumentType.BlockState("block");
    public SetCommand() {
        super("set", "<block>", BlockTool.Mode.SELECT);
        SE_addSyntax(this::call, blockArg);
    }

    private void call(Player player, CommandContext context, ItemStack item) {
        ToolShape.Mode shapeMode = item.getTag(Tag.Structure("shape", new ToolShape.ShapeTagSerializer(player)));
        if (shapeMode == null) return;
        ToolShapeIterator iter = shapeMode.iter(TagHandler.fromCompound((NBTCompound) item.getTag(Tag.NBT("shape"))));
        Block block = context.get(blockArg);
        iter.fill(player.getInstance(), () -> block);
        SEUtils.message(player, SEColorUtil.GENERIC.format("Filled %% with %%", iter.count()+(iter.count() > 0 ? " blocks" : " block"), block.name()));
    }
}
