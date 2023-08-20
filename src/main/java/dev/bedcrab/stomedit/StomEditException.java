package dev.bedcrab.stomedit;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

public class StomEditException extends RuntimeException {
    private final Player player;
    private final Component hintMsg;
    public StomEditException(Player player, String message, Throwable cause) {
        super(message, cause, false, false);
        this.player = player;
        this.hintMsg = null;
    }
    public StomEditException(Player player, String message, Throwable cause, Component hintMsg) {
        super(message, cause, false, false);
        this.player = player;
        this.hintMsg = hintMsg;
    }

    public void sendMessage() {
        SEUtils.message(player, SEColorUtil.FAIL.text(getMessage()));
        if (getCause() == null) return;
        if (hintMsg != null) SEUtils.message(player, SEColorUtil.FAIL.text("Hint: ").append(hintMsg));
        SEUtils.message(player, SEColorUtil.FAIL.format("Cause: %%", getCause().getMessage()));
        Throwable nextCause = getCause().getCause();
        int i = 0;
        while (nextCause != null) {
            i++;
            SEUtils.message(player, SEColorUtil.FAIL.format(" ".repeat(i)+"Caused by: %%", nextCause.getMessage() == null ? nextCause.getClass().getSimpleName() : nextCause.getMessage()));
            nextCause = nextCause.getCause();
        }
    }
}
