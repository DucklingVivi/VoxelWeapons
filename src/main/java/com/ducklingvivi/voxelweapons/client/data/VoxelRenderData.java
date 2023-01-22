package com.ducklingvivi.voxelweapons.client.data;

import com.ducklingvivi.voxelweapons.client.render.RenderTypes;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.ScreenEvent;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoxelRenderData {
    public VoxelRenderData(){
        data = new HashMap<>();
        order = new ArrayList<>();
        order.add(RenderType.solid());
        order.add(RenderType.cutout());
        order.add(RenderType.cutoutMipped());
    }

    public Map<RenderType, BufferBuilder> data;
    public List<RenderType> order;

    public BufferBuilder getData(RenderType renderType){
        if(data.containsKey(renderType)){
            return data.get(renderType);
        }
        if (!order.contains(renderType)){
            order.add(renderType);
        }
        BufferBuilder builder = new BufferBuilder(renderType.bufferSize());
        builder.begin(renderType.mode(),renderType.format());
        builder.setQuadSortOrigin(0,0,0);
        data.put(renderType, builder);
        return builder;

    }

    public List<RenderType> getOrder(){
        if(order.contains(RenderTypes.TRANSLUCENT)){
            order.remove(RenderTypes.TRANSLUCENT);
            order.add(RenderTypes.TRANSLUCENT);
        }
        return order;
    }



}
