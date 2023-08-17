package dev.bedcrab.stomedit;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class SEUtils {
    public static void message(CommandSender sender, Component text) {
        sender.sendMessage(SEColorUtil.SPECIAL.text("[StomEdit]").appendSpace().append(text));
    }
    public static void exceptionMessage(Exception e, CommandSender player, String message) {
        SEUtils.message(player, SEColorUtil.FAIL.text(message));
        SEUtils.message(player, SEColorUtil.FAIL.format("Error message: %%", e.getMessage()));
        SEUtils.message(player, SEColorUtil.FAIL.format("Error cause: %%", e.getCause().toString()));
    }
    public static Hashtable<String, Set<String>> getAllDefaultProperties(Block block) {
        final Hashtable<String, Set<String>> blockProperties = new Hashtable<>();
        for (Block possibleState : block.possibleStates()) {
            for (Map.Entry<String, String> entry : possibleState.properties().entrySet()) {
                blockProperties.putIfAbsent(entry.getKey(), new HashSet<>());
                blockProperties.get(entry.getKey()).add(entry.getValue());
            }
        }
        return blockProperties;
    }
    public static Component pointToComp(Point point) {
        return SEColorUtil.GENERIC.format("%%, %%, %%", String.valueOf(point.x()), String.valueOf(point.y()), String.valueOf(point.z()));
    }
}
