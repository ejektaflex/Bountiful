package io.ejekta.bountiful.forge;

import io.ejekta.bountiful.config.BountifulIO;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BountyDataReloader implements ResourceReloader {
    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            BountifulIO.INSTANCE.doContentReload(manager);
            return null;
        });
    }
}
