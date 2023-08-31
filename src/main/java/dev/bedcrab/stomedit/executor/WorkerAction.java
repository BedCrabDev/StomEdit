package dev.bedcrab.stomedit.executor;

import org.jetbrains.annotations.NotNull;

public interface WorkerAction {
    @NotNull BlockImage image();
    void apply();
}
