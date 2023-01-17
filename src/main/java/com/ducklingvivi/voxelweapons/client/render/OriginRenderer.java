package com.ducklingvivi.voxelweapons.client.render;

import com.ducklingvivi.voxelweapons.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class OriginRenderer {
    public static void render(Tesselator tesselator, PoseStack poseStack, Camera cam){

        BufferBuilder buffer = tesselator.getBuilder();
        poseStack.pushPose();

        RenderSystem.setShader(GameRenderer::getBlockShader);


        BlockPos pos = VoxelCreatorClientData.INSTANCE.getOrigin();
        poseStack.translate(-cam.getPosition().x, -cam.getPosition().y,
                -cam.getPosition().z);
        poseStack.translate(pos.getX(),pos.getY(),
                pos.getZ());
        poseStack.scale(1.01f,1.01f,1.01f);
        poseStack.translate(-0.005,-0.005,-0.005);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        BlockState state = Registration.VOXELORIGINBLOCK.get().defaultBlockState();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),buffer,state, model,255,255,255,1231231,0, ModelData.builder().build(), RenderType.translucent());
        tesselator.end();
        poseStack.popPose();
    }
}
