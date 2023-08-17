package dev.bedcrab.stomedit.toolshapes.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import dev.bedcrab.stomedit.toolshapes.ToolShapeMode;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class DiskShape implements ToolShapeMode {
    @Override
    public Collection<Tag<?>> getRequiredTags() {
        return Arrays.asList(
            Tag.Structure("origin", Pos.class),
            Tag.Integer("radius"),
            Tag.Boolean("vertical")
        );
    }

    @Override
    public ToolShapeIterator iter(TagReadable tags) {
        return new ShapeIterator(tags.getTag(Tag.Structure("origin", Pos.class)), tags.getTag(Tag.Integer("radius")), tags.getTag(Tag.Boolean("vertical")));
    }

    @Override
    public Component getHelpMessage() {
        return SEColorUtil.FAIL.format("Set %% and %% to specify a selection!", "ORIGIN (lclick)", "RADIUS (rclick)");
    }

    @Override
    public void onRightClick(Player player, Pos pos, MutableNBTCompound nbt) {
        if (!nbt.contains("origin")) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Set %% first, or use //toolshape DISK <radius> to set the radius!", "ORIGIN (lclick)"));
            return;
        }
        Pos origin = Objects.requireNonNull(Tag.Structure("origin", Pos.class).read(nbt));
        int radius = (int) origin.distance(pos);
        double yDiff = Math.max(origin.y(), pos.y()) - Math.min(origin.y(), pos.y());
        Tag.Integer("radius").write(nbt, radius);
        boolean vertical = radius < yDiff && yDiff > 2;
        Tag.Boolean("vertical").write(nbt, vertical);
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %% (%%)", "RADIUS", radius + (radius != 0 ? " blocks" : " block"), vertical ? "vertically" : "horizontally"));
    }

    @Override
    public void onLeftClick(Player player, Pos pos, MutableNBTCompound nbt) {
        Tag.Structure("origin", Pos.class).write(nbt, pos);
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %%", Component.text("ORIGIN"), SEUtils.pointToComp(pos)));
    }

    //TODO: turn into actual iterator
    public static class ShapeIterator implements ToolShapeIterator {
        private int count;
        private final Iterator<Pos> posIterator;

        public ShapeIterator(Pos origin, int r, boolean vertical) {
            ArrayList<Pos> list = new ArrayList<>();

            // FAWE code vvv
            final int ceilR = (int) (double) r;
            final double invR = 1. / r;

            int px = origin.blockX();
            int py = origin.blockY();
            int pz = origin.blockZ();

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
                    list.add(new Pos(xx, vertical ? zz : py, vertical ? pz : zz));
                    list.add(new Pos(x_x, vertical ? zz : py, vertical ? pz : zz));
                    list.add(new Pos(xx, vertical ? z_z : py, vertical ? pz : z_z));
                    list.add(new Pos(x_x, vertical ? z_z : py, vertical ? pz : z_z));
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
        public Pos next() {
            count++;
            return posIterator.next();
        }
    }
}
