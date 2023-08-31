package dev.bedcrab.stomedit.executor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.*;

class SEJob implements Callable<LinkedList<BlockImage>> {
    private final ExecutorService executor = Executors.newWorkStealingPool();
    private final LinkedList<SETask> tasks;
    public SEJob(LinkedList<SETask> tasks) {
        this.tasks = tasks;
    }
    @Override
    public LinkedList<BlockImage> call() throws ExecutionException, InterruptedException {
        LinkedList<BlockImage> list = new LinkedList<>();
        for (SETask task : tasks) {
            BlockImage[] images = executor.submit(task).get();
            Collections.addAll(list, images);
        }
        return list;
    }
}
