package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.client.data.VoxelDataClient;
import com.ducklingvivi.voxelweapons.client.data.VoxelRenderData;
import com.ducklingvivi.voxelweapons.client.render.RenderTypes;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class VoxelWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {

    BlockEntityRenderDispatcher dispatcher;

    public VoxelWithoutLevelRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
        dispatcher = p_172550_;
    }

    private static VoxelWithoutLevelRenderer instance;

    public static VoxelWithoutLevelRenderer getInstance() {
        if (instance == null)
            instance = new VoxelWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        return instance;
    }



    //@Override
    //public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType pTransformType, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

        BlockPos blockPos = null;
        Entity entity = itemStack.getEntityRepresentation();
        if (entity != null) {
            blockPos = entity.blockPosition();
        }
        if (blockPos == null) {
            blockPos = Minecraft.getInstance().player.blockPosition();
        }


        if (!itemStack.is(Registration.VOXELWEAPONITEM.get())) return;

        VoxelData data;
        VoxelRenderData renderData;
        CompoundTag nbt = itemStack.getOrCreateTag();
        if (!nbt.contains("voxelUUID")) return;
        UUID uuid = nbt.getUUID("voxelUUID");
        Optional<VoxelData> optionaldata = VoxelDataClient.getData(uuid);
        if (optionaldata.isEmpty()) return;
        data = optionaldata.get();
        Optional<VoxelRenderData> optionalrenderdata = VoxelDataClient.getRenderData(uuid);
        if (optionalrenderdata.isEmpty()) {
            RenderItem(data, uuid, pBuffer);
            return;
        }

        VoxelTintAndBlockGetter tintAndBlockGetter = new VoxelTintAndBlockGetter(Minecraft.getInstance().level, data, new BlockPos(0, 0, 0), LightTexture.FULL_BRIGHT);
        RandomSource randomsource = RandomSource.create(42);

        poseStack.pushPose();

        switch (pTransformType){
            case GUI -> {
                guiPose(poseStack,data);
            }
            case FIXED, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, FIRST_PERSON_LEFT_HAND, GROUND, NONE, HEAD -> {}
            default -> {
                //guiPose(poseStack,data);
            }
        }

        renderData = optionalrenderdata.get();
        data.presentTileEntities.forEach((blockpos, blockentity) -> {
            if (blockentity != null) {
                BlockEntityRenderDispatcher entityDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                BlockEntityRenderer<BlockEntity> renderer = entityDispatcher.getRenderer(blockentity);
                if (renderer != null) {
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.scale(0.1f, 0.1f, 0.1f);
                    poseStack.translate(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                    poseStack.translate(-0.5, -0.5, -0.5);
                    renderer.render(blockentity, 1.0F, poseStack, pBuffer, pPackedLight, pPackedOverlay);
                    poseStack.popPose();
                }
            }
        });





        renderData.getOrder().forEach((key)->{
            VertexConsumer buffer = pBuffer.getBuffer(key);
            if (buffer instanceof BufferBuilder builder) {
                BufferBuilder value = renderData.getData(key);
                putBulkData(poseStack,pPackedLight, builder, value);
            }

        });

        poseStack.popPose();

    }


    private void RenderItem(VoxelData data, UUID uuid,MultiBufferSource source) {
        VoxelTintAndBlockGetter tintAndBlockGetter = new VoxelTintAndBlockGetter(Minecraft.getInstance().level, data, new BlockPos(0, 0, 0), LightTexture.FULL_BRIGHT);
        RandomSource randomsource = RandomSource.create(42);
        PoseStack poseStack = new PoseStack();

        VoxelRenderData renderData = new VoxelRenderData();
        //Render Blocks
        data.devGetVoxels().forEach((info) -> {

            FluidState fluidstate = tintAndBlockGetter.getFluidState(info.pos);
            if (!fluidstate.isEmpty()) {

                poseStack.pushPose();
                RenderType rendertype = RenderTypes.TRANSLUCENT;
                //TODO CHANGE THIS

                BufferBuilder bufferbuilder = renderData.getData(rendertype);
                float t0 = (float)(Math.round(info.pos.getX() >> 4)*16);
                float t1 = (float)(Math.round(info.pos.getY() >> 4)*16);
                float t2 = (float)(Math.round(info.pos.getZ() >> 4)*16);
                poseStack.translate(t0,t1,t2);
                BufferBuilder vertexConsumer = Tesselator.getInstance().getBuilder();
                vertexConsumer.begin(rendertype.mode(),rendertype.format());
                Minecraft.getInstance().getBlockRenderer().renderLiquid(info.pos, tintAndBlockGetter, vertexConsumer , info.state, fluidstate);




                ByteBuffer vertexBuffer = cloneBytes(vertexConsumer);
                int size = DefaultVertexFormat.BLOCK.getVertexSize();
                int buffersize = vertexConsumer.vertices * vertexConsumer.format.getVertexSize();
                for (int i = 0; i < vertexConsumer.vertices; i++) {
                    int index = size * i;
                    float f0 = vertexBuffer.getFloat(index);
                    float f1 = vertexBuffer.getFloat(4+index);
                    float f2 = vertexBuffer.getFloat(8+index);
                    Vector4f vector = new Vector4f(f0, f1, f2, 1);
                    vector = poseStack.last().pose().transform(vector);
                    vertexBuffer.putFloat(index, vector.x);
                    vertexBuffer.putFloat(4 + index, vector.y);
                    vertexBuffer.putFloat(8 + index, vector.z);
                }
                vertexBuffer.position(0);
                vertexBuffer.limit(buffersize);
                bufferbuilder.putBulkData(vertexBuffer);

                vertexConsumer.end();
                poseStack.popPose();
                vertexConsumer.discard();
            }

            if (info.state.getRenderShape() != RenderShape.INVISIBLE) {

                var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(info.state);
                var modelData = model.getModelData(tintAndBlockGetter, info.pos, info.state, data.modelData.getOrDefault(info.pos, ModelData.builder().build()));
                randomsource.setSeed(info.state.getSeed(info.pos));

                for (RenderType type : model.getRenderTypes(info.state, randomsource, modelData)) {
                    RenderType rendertype = type;
                    if(type.equals(RenderType.translucent())) {
                        rendertype = RenderTypes.TRANSLUCENT;
                    }
                    poseStack.pushPose();
                    poseStack.setIdentity();
                    poseStack.translate(info.pos.getX(), info.pos.getY(), info.pos.getZ());
                    BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
                    BufferBuilder buffer = renderData.getData(rendertype);


                    renderer.renderBatched(info.state, info.pos, tintAndBlockGetter, poseStack, buffer, true, RandomSource.create(42), ModelData.builder().build(), rendertype, true);
                    //renderSingleBlock(info.state, poseStack,p_108833_,p_108834_,p_108835_, ModelData.builder().build(),null);\


                    poseStack.popPose();
                }
            }
        });
        VoxelDataClient.addRenderData(uuid, renderData);

    }


    public void guiPose(PoseStack poseStack, VoxelData data){
        poseStack.translate(0.5f,0.5f,0.5f);
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotation((float) -(Math.PI/2), 0,1,0);
        poseStack.mulPoseMatrix(rotationMatrix);
//        rotationMatrix.rotation((float) -(Math.PI/4), 1,0,0);
//        poseStack.mulPoseMatrix(rotationMatrix);

        AABB boundingBox = data.bounds;
        double largestsize = Math.max(Math.max(boundingBox.getXsize(),boundingBox.getYsize()),boundingBox.getZsize());
        float scale = (float) ( 1 / (largestsize*0.1));
        poseStack.translate(-0.5,-0.5,-0.5);
        poseStack.scale(scale,scale,scale);

        poseStack.translate(-0.5f,-0.5f,-0.5f);
    }
    public ByteBuffer cloneBytes(BufferBuilder builder){
        int size = builder.vertices*builder.format.getVertexSize();

        ByteBuffer buffer = ByteBuffer.allocateDirect(size);

        ByteBuffer tempbuffer = builder.buffer.duplicate();

        tempbuffer.rewind();
        tempbuffer.limit(size);
        buffer.put(tempbuffer);
        buffer.position(0);
        buffer.order(builder.buffer.order());


        return buffer;
    }
    public ByteBuffer poseData(PoseStack poseStack,int packedLight, BufferBuilder builder) {
        ByteBuffer buffer = cloneBytes(builder);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.1f, 0.1f, 0.1f);
        poseStack.translate(-0.5, -0.5, -0.5);
        int size = builder.format.getVertexSize();
        boolean flag = builder.format.hasUV(2);
        for (int i = 0; i < builder.vertices; i++) {
            int index = i * size;

            float f0 = buffer.getFloat(index);
            float f1 = buffer.getFloat(4 + index);
            float f2 = buffer.getFloat(8 + index);


            Vector4f vector = new Vector4f(f0, f1, f2, 1);
            vector = poseStack.last().pose().transform(vector);
            buffer.putFloat(index, vector.x);
            buffer.putFloat(4 + index, vector.y);
            buffer.putFloat(8 + index, vector.z);

            if(flag){
                buffer.putInt(24 + index, packedLight);
            }

        }

        poseStack.popPose();

        return buffer;
    }

    public void putBulkData(PoseStack poseStack,int packedLight, BufferBuilder builderto, BufferBuilder builderfrom) {
        if (builderto.format.getVertexSize() != builderfrom.format.getVertexSize()) {
            return;
        }

        builderto.putBulkData(poseData(poseStack,packedLight, builderfrom));

    }
}
