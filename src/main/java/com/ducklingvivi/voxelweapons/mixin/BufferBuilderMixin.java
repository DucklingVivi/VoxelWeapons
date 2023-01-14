package com.ducklingvivi.voxelweapons.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {
    @Shadow
    private ByteBuffer buffer;

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
