package dev.bedcrab.stomedit.executor;

import dev.bedcrab.stomedit.SEUtils;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public interface JobWorker {
    ExecutorService getExecutor();
    CompletableFuture<LinkedList<BlockImage>> newJob(@NotNull List<WorkerAction> actions);
    CompletableFuture<LinkedList<BlockImage>> newJob(@NotNull Iterable<SEUtils.BlockPos> iterable, Function<SEUtils.BlockPos, WorkerAction> actFunc);

    record Change(Instance instance, SEUtils.BlockPos bPos, Block updated) implements WorkerAction {
        @Override
        public @NotNull BlockImage image() {
            return new BlockImage(bPos, instance.getBlock(bPos.pos()).stateId(), updated.stateId());
        }
        @Override
        public void apply() {
            instance.setBlock(bPos.pos(), updated);
        }
    }
}
