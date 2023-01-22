package com.ducklingvivi.voxelweapons.client.render;

import com.ducklingvivi.voxelweapons.voxelweapons;
import com.ibm.icu.util.Output;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class RenderTypes extends RenderType {
    public RenderTypes(String name, VertexFormat vertexFormat, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, vertexFormat, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    private static CompositeState addState(ShaderStateShard shard) {
        return CompositeState.builder()
                .setLightmapState(LIGHTMAP)
                .setShaderState(shard)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setOutputState(TRANSLUCENT_TARGET)
                .createCompositeState(true);
    }



    public static final RenderType ADD = create(voxelweapons.MODID + ":additive",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
            2097152, true, true,
            addState(RENDERTYPE_TRANSLUCENT_SHADER));
    public static final RenderType TRANSLUCENT = create(voxelweapons.MODID + ":translucent",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
            2097152, true, true,
            CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(true));

    public static final RenderType FLUID = create(voxelweapons.MODID + ":fluid",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,2097152,false,false,
            CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setOutputState(TRANSLUCENT_TARGET)
                    .createCompositeState(true));
}
