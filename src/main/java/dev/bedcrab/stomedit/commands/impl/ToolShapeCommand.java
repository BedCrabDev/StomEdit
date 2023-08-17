package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class ToolShapeCommand extends SECommand {
    private final ArgumentEnum<ToolShape.Mode> shapeArg = ArgumentType.Enum("shape", ToolShape.Mode.class);
    public ToolShapeCommand() {
        super("toolshape", "<shape>", null);
        SE_addSyntax(this::call, shapeArg);
    }

    private void call(Player player, CommandContext context, ItemStack item) {
        NBTCompound shapeNBT = (NBTCompound) item.getTag(Tag.NBT("shape"));
        ToolShape.Mode shapeMode = context.get(shapeArg);
        if (shapeNBT == null) shapeNBT = NBT.Compound(newNBT -> newNBT.set("type", NBT.String(shapeMode.name())));
        else shapeNBT = shapeNBT.toMutableCompound().set("type", NBT.String(shapeMode.name())).toCompound();
        player.getInventory().setItemStack(player.getHeldSlot(), item.withTag(Tag.NBT("shape"), shapeNBT));
        SEUtils.message(player, SEColorUtil.GENERIC.format("Using shape %%", shapeMode.name()));
    }
}
