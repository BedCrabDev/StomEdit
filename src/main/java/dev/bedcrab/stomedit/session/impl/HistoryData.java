package dev.bedcrab.stomedit.session.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.executor.BlockImage;
import dev.bedcrab.stomedit.session.SessionData;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("UnstableApiUsage")
public record HistoryData(int count, NBTCompound entries) implements SessionData {
    public static @NotNull NBT defaultFunc(@NotNull String missing) {
        return switch (missing) {
            case "count" -> NBT.Int(0);
            case "entries" -> SEUtils.emptyCompound();
            default -> throw new IllegalStateException("Unexpected value: " + missing);
        };
    }

    public CompletableFuture<HistoryData> save(@NotNull CompletableFuture<LinkedList<BlockImage>> completableJob, String description) {
        int index = count+1;
        return completableJob.thenApply(images -> {
            String format = "";
            for (BlockImage image : images) format += image.serialize();
            byte[] bytes;
            try {
                FastByteArrayOutputStream byteStream = new FastByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(byteStream);
                gzip.write(format.getBytes(StandardCharsets.UTF_8));
                gzip.flush();
                gzip.close();
                bytes = byteStream.array;
            } catch (IOException e) {
                throw new RuntimeException("Error whilst handling byte/gzip stream", e);
            }
            MutableNBTCompound newEntries = NBT.Compound(Map.of()).toMutableCompound();
            Tag.Structure("entry"+index, Entry.class).write(newEntries, new Entry(
                index, images.size(), Base64.getEncoder().encodeToString(bytes), description
            ));
            return new HistoryData(index, newEntries.toCompound());
        });
    }

    public record Entry(int index, int count, String gzip, String description) {}
}
