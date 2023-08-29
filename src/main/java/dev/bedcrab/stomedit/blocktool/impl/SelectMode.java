package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.BLToolData;
import dev.bedcrab.stomedit.session.impl.ToolShapeData;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class SelectMode implements BlockToolMode {
    @Override
    public void onUse(Block block, Point pos, Player player, @NotNull PlayerSession session) throws StomEditException {
        ToolShapeData data = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        try {
            data.parseMode().onRightClick(player, new Pos(pos), session);
        } catch (Exception e) {
            throw new StomEditException(player, "An error occurred whilst handling toolshape nbt!", e);
        }
    }

    @Override
    public void onLeftClick(Block block, Point pos, Player player, @NotNull PlayerSession session) throws StomEditException {
        ToolShapeData toolshapeData = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        try {
            toolshapeData.parseMode().onLeftClick(player, new Pos(pos), session);
        } catch (Exception e) {
            throw new StomEditException(player, "An error occurred whilst handling toolshape nbt!", e);
        }
    }

    public PlayerSession validate(Player player) {
        PlayerSession session = PlayerSession.of(player);
        BLToolData bltoolData = session.read(BLToolData.class, BLToolData::defaultFunc);
        if (bltoolData.num() != BlockTool.Mode.SELECT.ordinal()) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Use %% mode!", BlockTool.Mode.SELECT.name()));
            return null;
        }
        ToolShapeData toolshapeData = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        try {
            toolshapeData.validateParams();
        } catch (Exception e) {
            throw new StomEditException(player, "Shape data is invalid!", e, toolshapeData.parseMode().getHintMsg());
        }
        return session;
    }
}
