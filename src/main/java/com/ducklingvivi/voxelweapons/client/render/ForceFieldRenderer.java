package com.ducklingvivi.voxelweapons.client.render;

import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class ForceFieldRenderer {
    private static ResourceLocation TEXTURE = new ResourceLocation(voxelweapons.MODID,"textures/misc/forcefield.png");

    public static void render(Tesselator tesselator, PoseStack poseStack, Camera cam, AABB aabb){


        BufferBuilder buffer = tesselator.getBuilder();
        poseStack.pushPose();

        poseStack.translate(-cam.getPosition().x, -cam.getPosition().y,
                -cam.getPosition().z);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableCull();


        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = poseStack.last().pose();
        float f4 = 100f;

        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y - f4), (float)aabb.maxZ).uv(0,0).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y - f4), (float)aabb.maxZ).uv(0,1).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y + f4), (float)aabb.maxZ).uv(1,0).endVertex();
        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y + f4), (float)aabb.maxZ).uv(1,1).endVertex();
        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y - f4), (float)aabb.minZ).uv(0,0).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y - f4), (float)aabb.minZ).uv(0,1).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y + f4), (float)aabb.minZ).uv(1,0).endVertex();
        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y + f4), (float)aabb.minZ).uv(1,1).endVertex();
        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y - f4), (float)aabb.minZ).uv(0,0).endVertex();
        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y - f4), (float)aabb.maxZ).uv(0,1).endVertex();
        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y + f4), (float)aabb.maxZ).uv(1,0).endVertex();
        buffer.vertex(matrix, (float)aabb.minX, (float) (cam.getPosition().y + f4), (float)aabb.minZ).uv(1,1).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y - f4), (float)aabb.minZ).uv(0,0).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y - f4), (float)aabb.maxZ).uv(0,0).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y + f4), (float)aabb.maxZ).uv(1,0).endVertex();
        buffer.vertex(matrix, (float)aabb.maxX, (float) (cam.getPosition().y + f4), (float)aabb.minZ).uv(1,1).endVertex();



        tesselator.end();
        poseStack.popPose();

        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);


    }

}
