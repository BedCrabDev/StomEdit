package dev.bedcrab.stomedit.executor;

import dev.bedcrab.stomedit.SEUtils;
import org.jetbrains.annotations.NotNull;

public record BlockImage(SEUtils.BlockPos pos, int before, int after) {
    public @NotNull String serialize() {
        return pos.x()+" "+pos.y()+" "+pos.z()+" "+before+" "+after+" \n";
        //         x           y           z       before     after   end
        //[              COORDINATE              ][      IMAGE      ]
    }
}
