package dev.bedcrab.stomedit.blocktool.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.blocktool.BlockToolMode;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.BLToolSessionData;
import dev.bedcrab.stomedit.session.impl.ToolShapeSessionData;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;

public class SelectMode implements BlockToolMode {
    @Override
    public void onUse(Block block, Point pos, Player player, PlayerSession session) throws StomEditException {
        ToolShapeSessionData toolshapeData = session.read(ToolShapeSessionData.class, ToolShapeSessionData.DEFAULT);
        try {
            toolshapeData.parseMode().onRightClick(player, new Pos(pos), session);
        } catch (Exception e) {
            throw new StomEditException(player, "An error occurred whilst handling toolshape nbt!", e);
        }
    }

    @Override
    public void onLeftClick(Block block, Point pos, Player player, PlayerSession session) throws StomEditException {
        ToolShapeSessionData toolshapeData = session.read(ToolShapeSessionData.class, ToolShapeSessionData.DEFAULT);
        try {
            toolshapeData.parseMode().onLeftClick(player, new Pos(pos), session);
        } catch (Exception e) {
            throw new StomEditException(player, "An error occurred whilst handling toolshape nbt!", e);
        }
    }

    public PlayerSession validate(Player player) {
        PlayerSession session = PlayerSession.of(player);
        BLToolSessionData bltoolData = session.read(BLToolSessionData.class, BLToolSessionData.DEFAULT);
        if (bltoolData.num() != BlockTool.Mode.SELECT.ordinal()) {
            SEUtils.message(player, SEColorUtil.FAIL.format("Use %% mode!", BlockTool.Mode.SELECT.name()));
            return null;
        }
        ToolShapeSessionData toolshapeData = session.read(ToolShapeSessionData.class, ToolShapeSessionData.DEFAULT);
        try {
            toolshapeData.validateParams();
        } catch (Exception e) {
            throw new StomEditException(player, "Shape data is invalid!", e, toolshapeData.parseMode().getHintMsg());
        }
        return session;
    }
}
