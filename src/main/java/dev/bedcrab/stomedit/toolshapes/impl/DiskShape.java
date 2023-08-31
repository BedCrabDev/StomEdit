package dev.bedcrab.stomedit.toolshapes.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.ToolShapeData;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import dev.bedcrab.stomedit.toolshapes.ToolShapeMode;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.*;


@SuppressWarnings("UnstableApiUsage")
public class DiskShape implements ToolShapeMode {
    @Override
    public Collection<Argument<?>> shapeVariables() {
        return List.of(
            ArgumentType.Integer("radius"),
            ArgumentType.Boolean("vertical")
        );
    }

    @Override
    public Collection<Tag<?>> getRequiredParams() {
        return List.of(
            Tag.Structure("origin", SEUtils.BlockPos.class),
            Tag.Integer("radius"),
            Tag.Boolean("vertical")
        );
    }

    @Override
    public ToolShapeIterator iter(TagReadable params) {
        return new ShapeIterator(
            params.getTag(Tag.Structure("origin", SEUtils.BlockPos.class)),
            params.getTag(Tag.Integer("radius")),
            params.getTag(Tag.Boolean("vertical"))
        );
    }

    @Override
    public Component getHintMsg() {
        return SEColorUtil.HINT.format("Set %% and %% to specify a selection!", "ORIGIN (lclick)", "RADIUS (rclick)");
    }

    @Override
    public void onRightClick(@NotNull Player player, SEUtils.BlockPos bPos, @NotNull PlayerSession session) {
        ToolShapeData data = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        MutableNBTCompound nbt = data.params().toMutableCompound();

        if (!nbt.contains("origin")) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Set %% first, or use //toolshape DISK <radius> to set the radius!", "ORIGIN (lclick)"));
            return;
        }
        SEUtils.BlockPos origin = Objects.requireNonNull(Tag.Structure("origin", SEUtils.BlockPos.class).read(nbt));
        int radius = (int) origin.pos().distance(bPos.pos());
        boolean vertical = origin.x() == bPos.x() && origin.z() == bPos.z() && Math.max(origin.y(), bPos.y()) - Math.min(origin.y(), bPos.y()) > 1;
        Tag.Integer("radius").write(nbt, radius);
        Tag.Boolean("vertical").write(nbt, vertical);

        session.write(data.withParams(nbt));
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %% (%%)", "RADIUS", radius + (radius != 0 ? " blocks" : " block"), vertical ? "vertically" : "horizontally"));
    }

    @Override
    public void onLeftClick(@NotNull Player player, SEUtils.BlockPos bPos, @NotNull PlayerSession session) {
        ToolShapeData data = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        MutableNBTCompound nbt = data.params().toMutableCompound();
        Tag.Structure("origin", SEUtils.BlockPos.class).write(nbt, bPos);
        session.write(data.withParams(nbt));
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %%", "ORIGIN", bPos.toString()));
    }

    //TODO: turn into actual iterator
    public static class ShapeIterator implements ToolShapeIterator {
        private int count;
        private final Iterator<SEUtils.BlockPos> posIterator;

        public ShapeIterator(SEUtils.BlockPos origin, int r, boolean vertical) {
            ArrayList<SEUtils.BlockPos> list = new ArrayList<>();

            // FAWE code vvv
            final int ceilR = (int) (double) r;
            final double invR = 1. / r;

            int px = origin.x();
            int py = origin.y();
            int pz = origin.z();

            double xSqr, zSqr, distanceSq;
            double nextXn = 0, nextZn;
            double xn, zn;
            int xx, x_x, zz, z_z;
            // x + x , x - x , z + z , z - z
            // whoever wrote this is a psychopath

            x:
            for (int x = 0; x <= ceilR; ++x) {
                xn = nextXn;
                nextXn = (x + 1) * invR;
                nextZn = 0;
                xSqr = xn * xn;
                xx = px + x;
                x_x = px - x;
                for (int z = 0; z <= ceilR; ++z) {
                    zn = nextZn;
                    zSqr = zn * zn;
                    distanceSq = xSqr + zSqr;
                    if (distanceSq > 1) {
                        if (z == 0) break x;
                        break;
                    }
                    nextZn = (z + 1) * invR;

                    zz = (vertical ? py : pz) + z;
                    z_z = (vertical ? py : pz) - z;
                    // x -> south or north
                    // z -> east or west
                    //TODO: add "face" tag
                    list.add(new SEUtils.BlockPos(xx, vertical ? zz : py, vertical ? pz : zz));
                    list.add(new SEUtils.BlockPos(x_x, vertical ? zz : py, vertical ? pz : zz));
                    list.add(new SEUtils.BlockPos(xx, vertical ? z_z : py, vertical ? pz : z_z));
                    list.add(new SEUtils.BlockPos(x_x, vertical ? z_z : py, vertical ? pz : z_z));
                }
            }
            this.posIterator = list.iterator();
        }

        @Override
        public int count() {
            return count;
        }

        @Override
        public boolean hasNext() {
            return posIterator.hasNext();
        }

        @Override
        public SEUtils.BlockPos next() {
            count++;
            return posIterator.next();
        }
    }
}
