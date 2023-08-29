package dev.bedcrab.stomedit;

import cc.ddev.instanceguard.InstanceGuard;
import cc.ddev.instanceguard.region.Region;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface InstanceGuardProvider {
    @NotNull InstanceGuard getInstanceGuard();
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean hasLeveledPermissions(Player player, Region region, @NotNull Action act) {
        return !act.requiresOwner || region.isOwner(player);
    }

    enum Action {
        CREATE(false),
        DELETE(true),
        INSPECT(false),
        MODIFY_MEMBERS(true),
        MODIFY_OWNERS(true),
        ;

        public final boolean requiresOwner;
        Action(boolean requiresOwner) {
            this.requiresOwner = requiresOwner;
        }
    }
}
