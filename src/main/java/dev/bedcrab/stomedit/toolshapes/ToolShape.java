package dev.bedcrab.stomedit.toolshapes;

import com.extollit.collect.cache.WeakIterable;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.toolshapes.impl.CubicShape;
import dev.bedcrab.stomedit.toolshapes.impl.DiskShape;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentGroup;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;

import java.util.Collection;
import java.util.HashMap;

public class ToolShape {
    public static HashMap<Integer, ArgumentGroup> shapesParameters = new HashMap<>();
    public ToolShape() {}
    public void enable() {
        // reference the enum so it runs the enum constructor
        var temp = new WeakIterable<Mode>();
        temp.add(Mode.CUBIC);
        temp.add(Mode.DISK);
    }
    public enum Mode implements ToolShapeMode {
        CUBIC(new CubicShape()),
        DISK(new DiskShape()),
        ;

        private ToolShapeMode modeHandler;
        Mode(ToolShapeMode modeHandler) {
            overrideHandler(modeHandler);
            Collection<Argument<?>> parameters = modifiableParameters();
            shapesParameters.put(this.ordinal(),
                parameters.size() == 0 ? null : (ArgumentGroup) new ArgumentGroup("parameters", parameters.toArray(new Argument<?>[0])).setDefaultValue(new CommandContext(""))
            );
        }

        public void overrideHandler(ToolShapeMode modeHandler) {
            this.modeHandler = modeHandler;
        }

        @Override
        public Collection<Argument<?>> modifiableParameters() {
            return modeHandler.modifiableParameters();
        }

        @Override
        public Collection<Tag<?>> getRequiredParams() {
            return modeHandler.getRequiredParams();
        }

        @Override
        public ToolShapeIterator iter(TagReadable params) {
            return modeHandler.iter(params);
        }

        @Override
        public Component getHintMsg() {
            return modeHandler.getHintMsg();
        }

        @Override
        public void onRightClick(Player player, Pos pos, PlayerSession session) {
            modeHandler.onRightClick(player, pos, session);
        }

        @Override
        public void onLeftClick(Player player, Pos pos, PlayerSession session) {
            modeHandler.onLeftClick(player, pos, session);
        }
    }
}
