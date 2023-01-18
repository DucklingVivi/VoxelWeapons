package com.ducklingvivi.voxelweapons.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.StructureBlockRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class LineBoxRenderer {

    public static void render(Tesselator tesselator, PoseStack poseStack, Camera cam, AABB boundingBox){
        BufferBuilder buffer = tesselator.getBuilder();
        poseStack.pushPose();

        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(5.0f);
        poseStack.translate(-cam.getPosition().x, -cam.getPosition().y,
                -cam.getPosition().z);
        //poseStack.translate(boundingBox.minX,boundingBox.minY,
        //        boundingBox.minZ);
        //poseStack.scale(1.01f,1.01f,1.01f);
        //poseStack.translate(-0.00,-0.005,-0.005);
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        RenderSystem.disableCull();
        LevelRenderer.renderLineBox(poseStack, buffer,boundingBox, 0,1,0,1);

        tesselator.end();
        RenderSystem.lineWidth(1.0f);
        poseStack.popPose();
    }

}
