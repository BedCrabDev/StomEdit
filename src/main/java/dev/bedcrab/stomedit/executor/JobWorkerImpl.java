package dev.bedcrab.stomedit.executor;

import dev.bedcrab.stomedit.SEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class JobWorkerImpl implements JobWorker {
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final int[] SPLITS = new int[]{32, 48, 80, 112};
    public JobWorkerImpl() {}

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public CompletableFuture<LinkedList<BlockImage>> newJob(@NotNull List<WorkerAction> actions) {
        LinkedList<WorkerAction> current = new LinkedList<>();
        LinkedList<SETask> tasks = new LinkedList<>();
        int size = actions.size();
        int split = SPLITS[0];
        for (int check : SPLITS) {
            check = (int) Math.pow(check, 3);
            if (!(size >= check)) {
                split = check;
                break;
            }
        }
        for (int i = 0; i < size; i++) {
            current.add(actions.get(i));
            if (i % split == 0 || i == actions.size()-1) {
                tasks.add(new SETask(current));
                current = new LinkedList<>();
            }
        }

        SEJob job = new SEJob(tasks);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getExecutor().submit(job).get();
            } catch (InterruptedException e) {
                throw new RuntimeException("Job execution was interrupted!", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Error whilst executing job!", e);
            }
        });
    }

    @Override
    public CompletableFuture<LinkedList<BlockImage>> newJob(@NotNull Iterable<SEUtils.BlockPos> iterable, Function<SEUtils.BlockPos, WorkerAction> actFunc) {
        LinkedList<WorkerAction> actions = new LinkedList<>();
        for (SEUtils.BlockPos bPos : iterable) actions.add(actFunc.apply(bPos));
        return newJob(actions);
    }
}
