package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.setup.Registration;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4dStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBConditionalRenderInverted;

import java.nio.ByteBuffer;

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


    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType p_108831_, PoseStack poseStack, MultiBufferSource p_108833_, int p_108834_, int p_108835_) {
        if (itemStack.is(Registration.VOXELWEAPONITEM.get())) {
            poseStack.pushPose();
            VoxelData data = new VoxelData();
            CompoundTag nbt = itemStack.getOrCreateTag();
            if(nbt.contains("voxelData")){
                data.readNBT(Minecraft.getInstance().level,nbt.getCompound("voxelData"));
            }
            VoxelTintAndBlockGetter tintAndBlockGetter = new VoxelTintAndBlockGetter(Minecraft.getInstance().level,data);
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
                            poseStack.scale(0.2f,0.2f,0.2f);
                            poseStack.translate(info.pos.getX(),info.pos.getY(),info.pos.getZ());
                            poseStack.translate(-0.5,-0.5,-0.5);
                            renderer.render(blockentity,1.0F, poseStack,p_108833_,p_108834_,p_108835_);
                            poseStack.popPose();
                        }
                    }
                }
                //TODO FLUID STATE HERE
                FluidState fluidstate = tintAndBlockGetter.getFluidState(info.pos);
                if (!fluidstate.isEmpty()) {

                    poseStack.pushPose();
                    RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(fluidstate);
                    VertexConsumer bufferbuilder = p_108833_.getBuffer(rendertype);
                    poseStack.translate(0.5,0.5,0.5);
                    poseStack.scale(0.2f,0.2f,0.2f);
                    poseStack.translate(info.pos.getX(),info.pos.getY(),info.pos.getZ());
                    poseStack.translate(-0.5,-0.5,-0.5);

                    BufferBuilder vertexConsumer = Tesselator.getInstance().getBuilder();
                    vertexConsumer.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.BLOCK);
                    Minecraft.getInstance().getBlockRenderer().renderLiquid(info.pos, tintAndBlockGetter, vertexConsumer , info.state, fluidstate);


                    ByteBuffer vertexBuffer = vertexConsumer.buffer;
                    int size = DefaultVertexFormat.BLOCK.getVertexSize();
                    for (int i = 0; i < vertexConsumer.vertices; i++) {
                        float f0,f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11;
                        int i0,i1;

                        i0 = i1 = 1;
                        f0 = vertexBuffer.getFloat(i*size);
                        f1 = vertexBuffer.getFloat(4+i*size);
                        f2 = vertexBuffer.getFloat(8+i*size);
                        byte b0 = vertexBuffer.get(12+i*size);
                        byte b1 = vertexBuffer.get(13+i*size);
                        byte b2 = vertexBuffer.get(14+i*size);
                        byte b3 = vertexBuffer.get(15+i*size);
                        f7 = vertexBuffer.getFloat(16+i*size);
                        f8 = vertexBuffer.getFloat(20+i*size);
                        i0 = vertexBuffer.getInt(24+i*size);
                        f9 = vertexBuffer.get(28+i*size)/127f;
                        f10 = vertexBuffer.get(29+i*size)/127f;
                        f11 = vertexBuffer.get(30+i*size)/127f;

                        f3 = (float)((int)b0);
                        f4 = (float)((int)b1);
                        f5 = (float)((int)b2);
                        f6 = (float)((int)b3);

                        Vector4f vector4f = new Vector4f(f0,f1,f2,1.0F);
                        //Vector4f vector4f = poseStack.last().pose().transform(new Vector4f(f0,f1,f2,1.0f));

                        bufferbuilder.vertex(vector4f.x,vector4f.y,vector4f.z,f3,f4,f5,f6,f7,f8,i0,i1,f9,f10,f11);
                        bufferbuilder.endVertex();
                    }
                    vertexConsumer.end();
                    poseStack.popPose();
                    vertexConsumer.discard();

                }
                if (info.state.getRenderShape() != RenderShape.INVISIBLE) {
                    var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(info.state);
                    var modelData = model.getModelData(tintAndBlockGetter, info.pos, info.state, data.modelData.getOrDefault(info.pos, ModelData.builder().build()));
                    randomsource.setSeed(info.state.getSeed(info.pos));
                    for (RenderType rendertype : model.getRenderTypes(info.state, randomsource, modelData)) {
                        poseStack.pushPose();
                        poseStack.translate(0.5,0.5,0.5);
                        poseStack.scale(0.2f,0.2f,0.2f);
                        poseStack.translate(info.pos.getX(),info.pos.getY(),info.pos.getZ());
                        poseStack.translate(-0.5,-0.5,-0.5);
                        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
                        renderer.renderBatched(info.state,info.pos,tintAndBlockGetter, poseStack,p_108833_.getBuffer(rendertype),true, RandomSource.create(42), ModelData.builder().build(),rendertype, true);
                        //renderSingleBlock(info.state, poseStack,p_108833_,p_108834_,p_108835_, ModelData.builder().build(),null);
                        poseStack.popPose();
                    }
                }
            });
            poseStack.popPose();

        }
    }

}
