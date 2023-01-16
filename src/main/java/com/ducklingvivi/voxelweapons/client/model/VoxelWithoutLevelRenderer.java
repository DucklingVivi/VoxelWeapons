package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.library.IItemStackMixinInterface;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.checkerframework.checker.units.qual.C;
import org.joml.Matrix4dStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBConditionalRenderInverted;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.UUID;

public class VoxelWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {

    BlockEntityRenderDispatcher dispatcher;

    public VoxelWithoutLevelRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
        dispatcher = p_172550_;
    }

    private static VoxelWithoutLevelRenderer instance;
    public static VoxelWithoutLevelRenderer getInstance(){
        if(instance == null) instance = new VoxelWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());
        return instance;
    }


    //@Override
    //public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType pTransformType, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {




        BlockPos blockPos = null;
        if(blockPos == null) {
            Entity entity = itemStack.getEntityRepresentation();
            if (entity != null) {
                blockPos = entity.blockPosition();
            }
        }
        if(blockPos == null){
            blockPos = Minecraft.getInstance().player.blockPosition();
        }



        if (itemStack.is(Registration.VOXELWEAPONITEM.get())) {
            poseStack.pushPose();
            VoxelData data;
            CompoundTag nbt = itemStack.getOrCreateTag();
            if(nbt.contains("voxelUUID")){
                UUID uuid = nbt.getUUID("voxelUUID");
                data = VoxelDataClient.getData(uuid);
            }else{
                data = new VoxelData();
            }
            VoxelTintAndBlockGetter tintAndBlockGetter = new VoxelTintAndBlockGetter(Minecraft.getInstance().level,data,blockPos, pPackedLight);
            RandomSource randomsource = RandomSource.create(42);
            data.devGetVoxels().forEach((info) -> {
                //TODO BLOCK ENTITY RENDERING HERE I SUPPOSE
                if (info.state.hasBlockEntity()) {
                    BlockEntity blockentity = tintAndBlockGetter.getBlockEntity(info.pos);
                    if (blockentity != null) {
                        BlockEntityRenderDispatcher entityDispatcher =  Minecraft.getInstance().getBlockEntityRenderDispatcher();
                        BlockEntityRenderer<BlockEntity> renderer = entityDispatcher.getRenderer(blockentity);
                        if (renderer !=null){
                            poseStack.pushPose();
                            poseStack.translate(0.5,0.5,0.5);
                            poseStack.scale(0.1f,0.1f,0.1f);
                            poseStack.translate(info.pos.getX(),info.pos.getY(),info.pos.getZ());
                            poseStack.translate(-0.5,-0.5,-0.5);
                            renderer.render(blockentity,1.0F, poseStack,pBuffer,pPackedLight,pPackedOverlay);
                            poseStack.popPose();
                        }
                    }
                }
                /*
                FluidState fluidstate = info.state.getFluidState();
                if (!fluidstate.isEmpty()) {
                    poseStack.pushPose();
                    RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(fluidstate);
                    BufferBuilder bufferbuilder = (BufferBuilder) pBuffer.getBuffer(rendertype);

                    BufferBuilder vertexConsumer = Tesselator.getInstance().getBuilder();
                    vertexConsumer.begin(bufferbuilder.mode, bufferbuilder.format);

                    Minecraft.getInstance().getBlockRenderer().renderLiquid(info.pos, tintAndBlockGetter,vertexConsumer , info.state, fluidstate);


                    float t0 = (float)(Math.round(info.pos.getX() >> 4)*16);
                    float t1 = (float)(Math.round(info.pos.getY() >> 4)*16);
                    float t2 = (float)(Math.round(info.pos.getZ() >> 4)*16);
                    poseStack.translate(0.5,0.5,0.5);
                    poseStack.scale(0.2f,0.2f,0.2f);
                    poseStack.translate(t0,t1,t2);
                    poseStack.translate(-0.5,-0.5,-0.5);
                    ByteBuffer vertexBuffer = vertexConsumer.buffer;
                    int size = bufferbuilder.format.getVertexSize();
                    int amount = vertexConsumer.vertices;
                    for (int i = 0; i < amount; i++) {
                        float f0,f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11;
                        short s0,s1;


                        f0 = vertexBuffer.getFloat(i*size);
                        f1 = vertexBuffer.getFloat(4+i*size);
                        f2 = vertexBuffer.getFloat(8+i*size);
                        byte b0 = vertexBuffer.get(12+i*size);
                        byte b1 = vertexBuffer.get(13+i*size);
                        byte b2 = vertexBuffer.get(14+i*size);
                        byte b3 = vertexBuffer.get(15+i*size);
                        f7 = vertexBuffer.getFloat(16+i*size);
                        f8 = vertexBuffer.getFloat(20+i*size);
                        s0 = vertexBuffer.getShort(24+i*size);
                        s1 = vertexBuffer.getShort(26+i*size);
                        f9 = vertexBuffer.get(28+i*size)/127f;
                        f10 = vertexBuffer.get(29+i*size)/127f;
                        f11 = vertexBuffer.get(30+i*size)/127f;
                        f3 = (float)((int)b0 & 0xff)/255f;
                        f4 = (float)((int)b1 & 0xff)/255f;
                        f5 = (float)((int)b2 & 0xff)/255f;
                        f6 = (float)((int)b3 & 0xff)/255f;


                        //Vector4f vector4f = new Vector4f(f0,f1,f2,1.0F);

                        Vector4f vector4f = poseStack.last().pose().transform(new Vector4f(f0,f1,f2,1.0f));
                        Vector3f normalVector = new Vector3f(f9,f10,f11);
                        //normalVector.mul(poseStack.last().normal());
                        bufferbuilder.vertex(vector4f.x,vector4f.y,vector4f.z).color(f3,f4,f5,f6).uv(f7,f8).uv2(s0,s1).normal(normalVector.x,normalVector.y,normalVector.z).endVertex();
                    }
                    vertexConsumer.end();
                    vertexConsumer.discard();
                    poseStack.popPose();
                }
                 */
                if (info.state.getRenderShape() != RenderShape.INVISIBLE) {
                    var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(info.state);
                    var modelData = model.getModelData(tintAndBlockGetter, info.pos, info.state, data.modelData.getOrDefault(info.pos, ModelData.builder().build()));
                    randomsource.setSeed(info.state.getSeed(info.pos));
                    for (RenderType rendertype : model.getRenderTypes(info.state, randomsource, modelData)) {
                        poseStack.pushPose();
                        poseStack.translate(0.5,0.5,0.5);
                        poseStack.scale(0.1f,0.1f,0.1f);
                        poseStack.translate(info.pos.getX(),info.pos.getY(),info.pos.getZ());
                        poseStack.translate(-0.5,-0.5,-0.5);
                        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
                        renderer.renderBatched(info.state,info.pos,tintAndBlockGetter, poseStack,pBuffer.getBuffer(rendertype),true, RandomSource.create(42), ModelData.builder().build(),rendertype, true);
                        //renderSingleBlock(info.state, poseStack,p_108833_,p_108834_,p_108835_, ModelData.builder().build(),null);
                        poseStack.popPose();
                    }
                }
            });
            poseStack.popPose();

        }
    }

}
