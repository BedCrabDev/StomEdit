package dev.bedcrab.stomedit.commands.impl;

import cc.ddev.instanceguard.flag.DefaultFlagValue;
import cc.ddev.instanceguard.flag.FlagManager;
import cc.ddev.instanceguard.flag.FlagValue;
import cc.ddev.instanceguard.region.Region;
import cc.ddev.instanceguard.region.RegionManager;
import dev.bedcrab.stomedit.InstanceGuardProvider;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.ToolShapeSessionData;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import dev.bedcrab.stomedit.toolshapes.impl.CubicShape;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentGroup;
import net.minestom.server.command.builder.arguments.ArgumentLoop;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class RegionCommand extends SECommand {
    private final InstanceGuardProvider igProvider;
    public RegionCommand(InstanceGuardProvider igProvider) {
        super("region");
        this.igProvider = igProvider;
        addSubcommand(new DefineCommand());
        addSubcommand(new DeleteCommand());
        addSubcommand(new ListCommand());
        addSubcommand(new FlagCommand());
        addSubcommand(new MembersCommand());
        addSubcommand(new InspectCommand());
    }

    private static CubicShape.ShapeIterator validateToolshape(PlayerSession session) {
        ToolShapeSessionData data = session.read(ToolShapeSessionData.class, ToolShapeSessionData.DEFAULT);
        if (data.parseMode() != ToolShape.Mode.CUBIC) throw new RuntimeException("InstanceGuard requires a cubic selection!");
        return (CubicShape.ShapeIterator) data.parseIter();
    }

    private static void checkExists(Player player, InstanceGuardProvider igProvider, String name, BiConsumer<Region, RegionManager> consumer) throws StomEditException {
        RegionManager manager = igProvider.getInstanceGuard().getRegionManager();
        Region region = manager.getRegion(name, player.getInstance());
        if (region == null) throw new NullPointerException("Region does not exist!");
        consumer.accept(region, manager);
    }


    class DefineCommand extends SECommand {
        private final ArgumentString nameArg = ArgumentType.String("name");
        private final ArgumentLoop<EntityFinder> ownersArg = (ArgumentLoop<EntityFinder>) ArgumentType.Loop("owners",
            ArgumentType.Entity("player").onlyPlayers(true)
        ).setDefaultValue(List.of());
        public DefineCommand() {
            super("define", true);
            new Syntax(BlockTool.Mode.SELECT, (player, context, session) -> {
                CubicShape.ShapeIterator iterator = validateToolshape(session);
                RegionManager manager = igProvider.getInstanceGuard().getRegionManager();
                String name = context.get(nameArg);
                List<UUID> owners = context.get(ownersArg).stream().map(f -> {
                    Player owner = f.findFirstPlayer(player);
                    if (owner == null) throw new RuntimeException("Player not found!");
                    return owner.getUuid();
                }).toList();
                if (!owners.contains(player.getUuid())) throw new RuntimeException("Region creator must be an owner!");
                if (manager.getRegion(name, player.getInstance()) != null) throw new RuntimeException("Already exists!");
                Region region = new Region(name, player.getInstance(), iterator.minPos, iterator.maxPos);
                region.getOwners().addAll(owners);
                if (!igProvider.hasLeveledPermissions(player, region, InstanceGuardProvider.Action.CREATE)) throw new RuntimeException("Denied!");
                manager.getRegions().add(region);
                SEUtils.message(player, SEColorUtil.GENERIC.format(
                    "Created region `%%` (%% - %%)",
                    Component.text(region.getName()),
                    SEUtils.pointToComp(region.getMinLocation()),
                    SEUtils.pointToComp(region.getMaxLocation())
                ));
            }, nameArg, ownersArg);
        }
    }

    class DeleteCommand extends SECommand {
        private final ArgumentString regionArg = ArgumentType.String("region");
        public DeleteCommand() {
            super("delete", true);
            new Syntax(null, (player, context, session) -> {
                String name = context.get(regionArg);
                checkExists(player, igProvider, name, (region, regionManager) -> {
                    if (!igProvider.hasLeveledPermissions(player, region, InstanceGuardProvider.Action.DELETE)) throw new RuntimeException("Denied!");
                    regionManager.removeRegion(region);
                });
                SEUtils.message(player, SEColorUtil.GENERIC.format("Deleted region `%%`", name));
            }, regionArg);
        }
    }

    class ListCommand extends SECommand {
        public ListCommand() {
            super("list", true);
            new Syntax(null, (player, context, session) -> {
                List<Region> regions = igProvider.getInstanceGuard().getRegionManager().getRegions().stream().filter(r -> r.getInstance() == player.getInstance()).toList();
                boolean plural = regions.size() != 1;
                SEUtils.message(player, SEColorUtil.GENERIC.format(
                    "There "+(plural ? "are" : "is")+" %% "+"region"+(plural ? "s" : ""),
                    String.valueOf(regions.size())
                ));
                for (Region region : regions) {
                    if (!igProvider.hasLeveledPermissions(player, region, InstanceGuardProvider.Action.INSPECT)) continue;
                    SEUtils.message(player, SEColorUtil.GENERIC.format(
                        " - `%%` from: %% to: %%",
                        Component.text(region.getName()),
                        SEUtils.pointToComp(region.getMinLocation()),
                        SEUtils.pointToComp(region.getMaxLocation())
                    ));
                }
            });
        }
    }

    class FlagCommand extends SECommand {
        private final FlagManager manager = igProvider.getInstanceGuard().getFlagManager();
        private final ArgumentString regionArg = ArgumentType.String("region");
        private final ArgumentGroup flagArgs = ArgumentType.Group("flag_args",
            ArgumentType.Word("flag")
                .from(manager.getCustomFlags().keySet().stream().filter(f -> !f.endsWith("_group")).toArray(String[]::new)),
            ArgumentType.Word("value")
                .from(Arrays.stream(DefaultFlagValue.values()).map(f -> f.name().toLowerCase()).filter(v -> !v.endsWith("ers")).toArray(String[]::new)),
            ArgumentType.Word("group")
                .from(Arrays.stream(DefaultFlagValue.values()).map(Enum::name).filter(v -> v.endsWith("ERS")).toArray(String[]::new))
                .setDefaultValue("%null%")
        );
        public FlagCommand() {
            super("flag", true);
            new Syntax(null, (player, context, session) -> {
                String name = context.get(regionArg);
                CommandContext args = context.get(flagArgs);
                String flag = args.get("flag");
                FlagValue<?> value = DefaultFlagValue.valueOf(((String) args.get("value")).toUpperCase()).getValue();
                FlagValue<?> group = args.get("group").equals("%null%") ? manager.getDefaultValue(flag) : DefaultFlagValue.valueOf(args.get("group")).getValue();
                checkExists(player, igProvider, name, (region, regionManager) -> {
                    region.setFlag(flag+"_group", group);
                    region.setFlag(flag, value);
                });
                SEUtils.message(player, SEColorUtil.GENERIC.format(
                    "Set %% flag to %% (for group %%)",
                    flag, String.valueOf(value.getValue()), String.valueOf(group.getValue())
                ));
            }, regionArg, flagArgs);
        }
    }

    class MembersCommand extends SECommand {
        private final ArgumentString regionArg = ArgumentType.String("region");
        private final ArgumentEntity playerArg = ArgumentType.Entity("name").onlyPlayers(true);
        public MembersCommand() {
            super("members", true);
            new Syntax(null, (player, context, session) -> {
                String name = context.get(regionArg);
                Player targetPlayer = context.get(playerArg).findFirstPlayer(player);
                if (targetPlayer == null) throw new NullPointerException("Player not found!");
                checkExists(player, igProvider, name, (region, regionManager) -> {
                    if (!igProvider.hasLeveledPermissions(player, region, InstanceGuardProvider.Action.MODIFY_MEMBERS)) throw new RuntimeException("Denied!");
                    region.addMember(targetPlayer);
                });
                SEUtils.message(player, SEColorUtil.GENERIC.format("Added player `%%` as a member to region `%%`", targetPlayer.getUsername(), name));
            }, regionArg, ArgumentType.Literal("add"), playerArg);
            new Syntax(null, (player, context, session) -> {
                String name = context.get(regionArg);
                Player targetPlayer = context.get(playerArg).findFirstPlayer(player);
                if (targetPlayer == null) throw new NullPointerException("Player not found!");
                checkExists(player, igProvider, name, (region, regionManager) -> {
                    if (!igProvider.hasLeveledPermissions(player, region, InstanceGuardProvider.Action.MODIFY_MEMBERS)) throw new RuntimeException("Denied!");
                    region.removeMember(targetPlayer);
                });
                SEUtils.message(player, SEColorUtil.GENERIC.format("Removed member `%%` from region `%%`", targetPlayer.getUsername(), name));
            }, regionArg, ArgumentType.Literal("remove"), playerArg);
        }
    }

    class InspectCommand extends SECommand {
        private final ArgumentString regionArg = ArgumentType.String("region");
        private void bounds(Player player, Region region) {
            SEUtils.message(player, SEColorUtil.GENERIC.text("Bounds:"));
            SEUtils.message(player, SEColorUtil.GENERIC.format(" > from: %%", SEUtils.pointToComp(region.getMinLocation())));
            SEUtils.message(player, SEColorUtil.GENERIC.format(" > to: %%", SEUtils.pointToComp(region.getMaxLocation())));
        }
        private void owners(Player player, Region region) {
            SEUtils.message(player, SEColorUtil.GENERIC.text("Owners"));
            for (UUID uuid : region.getOwners()) SEUtils.message(player, SEColorUtil.GENERIC.format(" - %%", uuid.toString()));
        }
        private void members(Player player, Region region) {
            SEUtils.message(player, SEColorUtil.GENERIC.text("Members:"));
            for (UUID uuid : region.getMembers()) SEUtils.message(player, SEColorUtil.GENERIC.format(" - %%", uuid.toString()));
        }
        public InspectCommand() {
            super("inspect", true);
            new Syntax(null, (player, context, session) -> {
                String name = context.get(regionArg);
                AtomicReference<Region> regionRef = new AtomicReference<>();
                checkExists(player, igProvider, name, (r, unused) -> regionRef.set(r));
                Region region = regionRef.get();
                bounds(player, region);
                owners(player, region);
                members(player, region);
            }, regionArg);
        }
    }
}
