package com.ducklingvivi.voxelweapons.client.render;

import com.ducklingvivi.voxelweapons.library.VoxelFloorControllerBlockEntity;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.awt.*;
import java.lang.Math;

public class VoxelControllerRenderer implements BlockEntityRenderer<VoxelFloorControllerBlockEntity> {

    private static ResourceLocation TEXTURE =new ResourceLocation(voxelweapons.MODID,"buttons/voxelcontrollerbuttons");



    public VoxelControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(VoxelFloorControllerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        pPoseStack.pushPose();
        

        Player player = Minecraft.getInstance().player;
        VertexConsumer buffer = pBufferSource.getBuffer(RenderType.solid());
        BlockPos pos = pBlockEntity.getBlockPos();
        Vec3 lookvec = player.getLookAngle();
        Vec3 posvec = player.position();
        Vector3f size = new Vector3f(.5f,.5f, 0.1f);
        posvec = posvec.add(0,player.getEyeHeight(),0);
//        pPoseStack.translate(-cam.getPosition().x, -cam.getPosition().y,
//                -cam.getPosition().z);
//        pPoseStack.translate(pos.getX(),pos.getY(),
//                pos.getZ());

        MenuItem selecteditem = null;

        for (MenuItem item : MenuItem.values()) {
            int count = MenuItem.values().length;
            int i = item.ordinal();
            float menuitemangle = (float) (((2 * Math.PI )/ count) * i);

            Vec3 menuitempos = new Vec3(Math.cos(menuitemangle)*1.5f + 0.5,2.5,Math.sin(menuitemangle)*1.5f + 0.5);
            //Thank you zale
            menuitemangle = (float) (-menuitemangle + Math.PI/2);


            Vec3 menuitemworldpos = menuitempos.add(pos.getX(),pos.getY(),pos.getZ());

            Vec3 planenormal = new Vec3(Math.sin(menuitemangle),0,Math.cos(menuitemangle)).reverse();
            Vec3 intersectpoint = null;
            if(pos.distToCenterSqr(posvec)< 10 ){
                intersectpoint = linePlaneIntersection(posvec,lookvec, menuitemworldpos, planenormal);
            }

            boolean flag = false;

            if(intersectpoint != null){
                Vec3 vector2mouse = intersectpoint.subtract(menuitemworldpos);
                flag = Math.abs(vector2mouse.x) < size.x/2 && Math.abs(vector2mouse.y) < size.y/2 && Math.abs(vector2mouse.z)< size.x/2;
                if(flag){
                    selecteditem = item;
                }

            }
            pPoseStack.pushPose();
            pPoseStack.translate(menuitempos.x(),menuitempos.y(), menuitempos.z());
            Matrix4f rotationmatrix = new Matrix4f();
            rotationmatrix.rotation(menuitemangle, 0,1,0);
            pPoseStack.mulPoseMatrix(rotationmatrix);
            pPoseStack.translate(-size.x/2,-size.y/2, size.z);
            pPoseStack.scale(size.x,size.y,size.z);

            int brightness = LightTexture.FULL_BRIGHT;

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas( new ResourceLocation("textures/atlas/blocks.png")).apply(TEXTURE);


            float vscale = (sprite.getV1()-sprite.getV0())/4f;
            float uscale = (sprite.getU1()-sprite.getU0())/2f;
            float v0 = sprite.getV0() + i * vscale;
            float u0 = sprite.getU0() + (flag ? uscale : 0);
            float v1 = v0 + vscale;
            float u1 = u0 + uscale;

            Matrix4f matrix4f = pPoseStack.last().pose();
            Vector3f position = new Vector3f(0,0,0);
            Vector3f actualsize = new Vector3f(1,1,1);
            drawFace(pPoseStack,buffer,position,actualsize,u0,u1,sprite.getV0()+(vscale*3),sprite.getV0()+(vscale*4),brightness);

            pPoseStack.popPose();



            pPoseStack.pushPose();
            pPoseStack.translate(menuitempos.x(),menuitempos.y(), menuitempos.z());
            rotationmatrix.rotation((float) (menuitemangle), 0,1,0);
            pPoseStack.mulPoseMatrix(rotationmatrix);
            pPoseStack.translate(-size.x/2,-size.y/2, 0);
            pPoseStack.scale(size.x,size.y,size.x);
            drawEdges(pPoseStack,buffer,u0,u1,sprite.getV0()+(vscale*3),sprite.getV0()+(vscale*4),brightness,size.z);
            pPoseStack.popPose();

            pPoseStack.pushPose();
            pPoseStack.translate(menuitempos.x(),menuitempos.y(), menuitempos.z());
            rotationmatrix = new Matrix4f();
            rotationmatrix.rotation(menuitemangle + (float) (Math.PI), 0,1,0);
            pPoseStack.mulPoseMatrix(rotationmatrix);
            pPoseStack.translate(-size.x/2,-size.y/2, 0);
            pPoseStack.scale(size.x,size.y,size.z);



            position = new Vector3f(0,0,0);
            actualsize = new Vector3f(1,1,1);
            drawFace(pPoseStack,buffer,position,actualsize,u0,u1,v0,v1,brightness);
            pPoseStack.popPose();

        }

        if(selecteditem != null){
            int count = MenuItem.values().length;
            int i = selecteditem.ordinal();
            float menuitemangle = (float) (((2 * Math.PI )/ count) * i);
            Vec3 menuitempos = new Vec3(Math.cos(menuitemangle)*1.5f + 0.5,2.5,Math.sin(menuitemangle)*1.5f + 0.5);

            String value = "";
            switch (selecteditem){
                case FINISH -> {
                    if(VoxelCreatorClientData.INSTANCE.exitTimer > 0){
                        value = "ARE YOU SURE?";
                    }else{
                        value = "FINISH AND BUILD";
                    }

                }
                case ORIGIN -> {value = "TOGGLE SHOW ORIGIN";}
                case WIREFRAME -> {value = "TOGGLE SHOW WIREFRAME";}

            }
            Font font = Minecraft.getInstance().font;;


            pPoseStack.pushPose();
            pPoseStack.translate(menuitempos.x(),menuitempos.y() + size.y, menuitempos.z());
            Matrix4f rotationmatrix = new Matrix4f();
            rotationmatrix.rotation((float) (-menuitemangle + Math.PI/2), 0,1,0);
            pPoseStack.mulPoseMatrix(rotationmatrix);
            rotationmatrix.rotation((float) Math.PI, 0,0,1);
            pPoseStack.mulPoseMatrix(rotationmatrix);
            pPoseStack.scale(0.01f,0.01f,0.01f);
            pPoseStack.translate(-font.width(value)/2f, -font.lineHeight/2 , 0);
            font.drawInBatch(value, 0f,0f, new Color(255,255,255).getRGB(), false,pPoseStack.last().pose(),pBufferSource,false, 0,0);
            pPoseStack.popPose();
        }

        VoxelCreatorClientData.INSTANCE.currentMenuItem = selecteditem;

        pPoseStack.popPose();
    }


    @Override
    public boolean shouldRenderOffScreen(VoxelFloorControllerBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(VoxelFloorControllerBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    public static void register() {
        BlockEntityRenderers.register(Registration.VOXELFLOORCONTROLLERBLOCKENTITY.get(),VoxelControllerRenderer::new);
    }
    private static Vec3 linePlaneIntersection(Vec3 lineorigin, Vec3 linedir, Vec3 planeorigin, Vec3 planenormal) {
        float epsilon = -1e-3f;
        double flag = planenormal.dot(linedir.normalize());
        if(flag > epsilon){
            return null;
        }
        double t = (planenormal.dot(planeorigin)- planenormal.dot(lineorigin)) / planenormal.dot(linedir.normalize());
        return lineorigin.add(linedir.normalize().scale(t));
    }


    private static void drawEdges(PoseStack poseStack, VertexConsumer buffer, float u0, float u1, float v0, float v1, int brightness,float width){
        Matrix4f rotationmatrix = new Matrix4f();
        Vector3f pos = new Vector3f(0,0,0);
        Vector3f size = new Vector3f(.2f,1,.2f);


        poseStack.pushPose();
        drawEdge(poseStack,buffer,pos,size,u0,u1,v0,v1,brightness);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(1,0,0);
        rotationmatrix.rotation((float) (3*Math.PI/2),0,0,-1);
        poseStack.mulPoseMatrix(rotationmatrix);
        drawEdge(poseStack,buffer,pos,size,u0,u1,v0,v1,brightness);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(1,1,0);
        rotationmatrix.rotation((float) (Math.PI),0,0,1);
        poseStack.mulPoseMatrix(rotationmatrix);
        drawEdge(poseStack,buffer,pos,size,u0,u1,v0,v1,brightness);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0,1,0);
        rotationmatrix.rotation((float) (Math.PI/2),0,0,-1);
        poseStack.mulPoseMatrix(rotationmatrix);
        drawEdge(poseStack,buffer,pos,size,u0,u1,v0,v1,brightness);
        poseStack.popPose();
    }
    private static void drawFace(PoseStack poseStack, VertexConsumer buffer, Vector3f position, Vector3f size, float u0, float u1, float v0, float v1, int brightness){
        Matrix4f matrix4f = poseStack.last().pose();
        buffer.vertex(matrix4f, position.x,position.y,position.z).color(255,255,255,255).uv(u0,v1).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, position.x+size.x, position.y, position.z).color(255,255,255,255).uv(u1,v1).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, position.x+size.x, position.y+size.y, position.z).color(255,255,255,255).uv(u1,v0).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, position.x, position.y+size.y, position.z).color(255,255,255,255).uv(u0,v0).uv2(brightness).normal(1,0,0).endVertex();
    }
    private static void drawEdge(PoseStack poseStack, VertexConsumer buffer, Vector3f position, Vector3f size, float u0, float u1, float v0, float v1, int brightness){
        Matrix4f matrix4f = poseStack.last().pose();
        buffer.vertex(matrix4f, position.x,position.y,position.z).color(255,255,255,255).uv(u1,v1).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, position.x, position.y, position.z+size.x).color(255,255,255,255).uv(u0,v1).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, position.x, position.y+size.y, position.z+size.x).color(255,255,255,255).uv(u0,v0).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix4f, position.x, position.y+size.y, position.z).color(255,255,255,255).uv(u1,v0).uv2(brightness).normal(1,0,0).endVertex();
    }

    public enum MenuItem{
        FINISH,
        ORIGIN,
        WIREFRAME
    }

}
