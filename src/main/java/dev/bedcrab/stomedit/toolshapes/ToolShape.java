package dev.bedcrab.stomedit.toolshapes;

import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.toolshapes.impl.CubicShape;
import dev.bedcrab.stomedit.toolshapes.impl.DiskShape;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentGroup;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;

import java.util.Collection;
import java.util.HashMap;

public class ToolShape {
    public static HashMap<String, ArgumentGroup> shapesParameters = new HashMap<>();
    public enum Mode implements ToolShapeMode {
        CUBIC(new CubicShape()),
        DISK(new DiskShape())
        ;

        private ToolShapeMode modeHandler;
        Mode(ToolShapeMode modeHandler) {
            overrideHandler(modeHandler);
            Collection<Argument<?>> parameters = modifiableParameters();
            if (parameters.size() > 0) shapesParameters.put(this.name(), new ArgumentGroup("parameters", parameters.toArray(new Argument<?>[0])));
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
