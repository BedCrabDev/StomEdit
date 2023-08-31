package dev.bedcrab.stomedit.session;

import net.minestom.server.tag.Tag;

@SuppressWarnings("unused")
public interface SessionData {
    default <T> T getTag(PlayerSession session, Tag<T> tag) {
        return session.getTag(this.getClass(), tag);
    }
    default <T> void setTag(PlayerSession session, Tag<T> tag, T value) {
        session.setTag(this.getClass(), tag, value);
    }
}
