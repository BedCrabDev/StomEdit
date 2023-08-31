package dev.bedcrab.stomedit.executor;

import java.util.LinkedList;
import java.util.concurrent.Callable;

public class SETask implements Callable<BlockImage[]> {
    private final LinkedList<WorkerAction> actions;
    public SETask(LinkedList<WorkerAction> actions) {
        this.actions = actions;
    }

    @Override
    public BlockImage[] call() {
        BlockImage[] images = new BlockImage[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            WorkerAction act = actions.get(i);
            images[i] = act.image();
            act.apply();
        }
        return images;
    }
}
