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
        if (hintMsg != null) SEUtils.message(player, SEColorUtil.FAIL.text("Hint: ").append(hintMsg));
        Throwable cause = getCause();
        int i = 0;
        while (cause != null) {
            String margin = " ".repeat(i);
            SEUtils.message(player, SEColorUtil.FAIL.format(margin+"Caused by: %%", cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage()));
            if (cause instanceof StomEditException seException && seException.hintMsg != null) SEUtils.message(player, SEColorUtil.FAIL.text(margin+"Hint: ").append(seException.hintMsg));
            cause = cause.getCause();
            i++;
        }
    }
}
