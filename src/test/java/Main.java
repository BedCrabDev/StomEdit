import dev.bedcrab.stomedit.StomEdit;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;

public class Main {
    public static StomEdit se;
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer container = instanceManager.createInstanceContainer(DimensionType.OVERWORLD);
        container.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.STONE));
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        CommandManager commandManager = MinecraftServer.getCommandManager();
        se = new StomEdit(globalEventHandler, commandManager);
        se.enable();

        globalEventHandler.addListener(PlayerLoginEvent.class, e -> {
            final Player player = e.getPlayer();
            e.setSpawningInstance(container);
            player.setRespawnPoint(new Pos(0, 42, 0));
            player.setGameMode(GameMode.CREATIVE);
        });
        server.start("0.0.0.0", 25565);
    }
}
