package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.client.data.VoxelDataClient;
import com.ducklingvivi.voxelweapons.client.data.VoxelRenderData;
import com.ducklingvivi.voxelweapons.client.render.RenderTypes;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelItem;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.*;

import java.awt.*;
import java.lang.Math;
import java.nio.ByteBuffer;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class VoxelWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {

    BlockEntityRenderDispatcher dispatcher;
    private List<List<Integer>> faces;

    private List<Vector4f> vectorList;

    private long oldmillis;
    private float angle = 0;

    public VoxelWithoutLevelRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);

        dispatcher = p_172550_;
        faces = new ArrayList<>();
        faces.add(List.of(0,1,3,2));
        faces.add(List.of(0,1,5,4));
        faces.add(List.of(0,1,9,8));;
        faces.add(List.of(0,2,6,4));;
        faces.add(List.of(0,2,10,8));
        faces.add(List.of(0,4,12,8));
        faces.add(List.of(1,3,7,5));
        faces.add(List.of(1,3,11,9));
        faces.add(List.of(1,5,13,9));
        faces.add(List.of(2,3,7,6));
        faces.add(List.of(2,3,11,10));
        faces.add(List.of(2,6,14,10));
        faces.add(List.of(3,7,15,11));
        faces.add(List.of(4,5,7,6));
        faces.add(List.of(4,5,13,12));
        faces.add(List.of(4,6,14,12));
        faces.add(List.of(5,7,15,13));
        faces.add(List.of(6,7,15,14));
        faces.add(List.of(8,9,11,10));
        faces.add(List.of(8,9,13,12));
        faces.add(List.of(8,10,14,12));
        faces.add(List.of(9,11,15,13));
        faces.add(List.of(10,11,15,14));
        faces.add(List.of(12,13,15,14));

        oldmillis = 0;
        vectorList = new ArrayList<>();
        vectorList.add(new Vector4f(-1, -1, -1, -1));
        vectorList.add(new Vector4f(-1, -1, -1, 1));

        vectorList.add(new Vector4f(-1, -1, 1, -1));
        vectorList.add(new Vector4f(-1, -1, 1, 1));

        vectorList.add(new Vector4f(-1, 1, -1, -1));
        vectorList.add(new Vector4f(-1, 1, -1, 1));

        vectorList.add(new Vector4f(-1, 1, 1, -1));
        vectorList.add(new Vector4f(-1, 1, 1, 1));

        vectorList.add(new Vector4f(1, -1, -1, -1));
        vectorList.add(new Vector4f(1, -1, -1, 1));
        vectorList.add(new Vector4f(1, -1, 1, -1));
        vectorList.add(new Vector4f(1, -1, 1, 1));

        vectorList.add(new Vector4f(1, 1, -1, -1));
        vectorList.add(new Vector4f(1, 1, -1, 1));

        vectorList.add(new Vector4f(1, 1, 1, -1));
        vectorList.add(new Vector4f(1, 1, 1, 1));

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

        if (itemStack.is(Registration.VOXELWEAPONITEM.get())) {
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
            poseStack.pushPose();

            switch (pTransformType) {
                case GUI -> {
                    guiPose(poseStack, data);
                }
                case FIXED, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, FIRST_PERSON_LEFT_HAND, GROUND, NONE, HEAD -> {
                }
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
            renderData.getOrder().forEach((key) -> {
                VertexConsumer buffer = pBuffer.getBuffer(key);
                if (buffer instanceof BufferBuilder builder) {
                    BufferBuilder value = renderData.getData(key);
                    putBulkData(poseStack, pPackedLight, builder, value);
                }
            });
            poseStack.popPose();
        }else if(itemStack.is(Registration.VOXELCATALYSTITEM.get())){

            poseStack.pushPose();

            float cosineAngle = (float) Math.cos(angle);
            float sineAngle = (float) Math.sin(angle);
            int distance = 3;


            Matrix4f rotationXY = new Matrix4f();
            rotationXY.set(cosineAngle,-sineAngle,0,0,sineAngle,cosineAngle,0,0,0,0,1,0,0,0,0,1 );
            Matrix4f rotationZW = new Matrix4f();
            rotationZW.set(1,0,0,0,0,1,0,0,0,0,cosineAngle,-sineAngle,0,0,sineAngle,cosineAngle);
            Matrix4x3f projection = new Matrix4x3f();
            List<Vector3f> projected = new ArrayList<>();
            for (int i = 0; i < vectorList.size(); i++) {
                Vector4f value = new Vector4f(vectorList.get(i));
                value = value.mul(rotationXY);
                value = value.mul(rotationZW);
                float w = 1 / (distance - value.w);
                projection.set(w,0,0,0,w,0,0,0,w,0,0,0);
                value = value.mul(projection);
                value = value.mul(3f/8f);
                projected.add(i,new Vector3f(value.x,value.y,value.z));
            }

            VertexConsumer buffer = pBuffer.getBuffer(RenderTypes.HYPERCUBE);

            TextureAtlasSprite sprite =  Minecraft.getInstance().getTextureAtlas( new ResourceLocation("textures/atlas/blocks.png")).apply(new ResourceLocation(voxelweapons.MODID,"block/catalyst"));




            poseStack.pushPose();
            poseStack.translate(0.5f,0.5f,0.5f);
            int color = Color.HSBtoRGB(angle / 12f, .5f, .4f);
            switch (pTransformType) {
                case GUI,FIXED,NONE -> {
                    poseStack.translate(0,0,-0.2);
                    poseStack.mulPoseMatrix(new Matrix4f().rotate((float) (Math.PI/4),0,0,1));
                    poseStack.mulPoseMatrix(new Matrix4f().rotate((float) (Math.PI/4),1,0,0));
                    color = Color.HSBtoRGB(angle / 12f, .5f, .5f);
                }
                case THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, FIRST_PERSON_LEFT_HAND, GROUND, HEAD -> {
                }

                default -> {
                    poseStack.mulPoseMatrix(new Matrix4f().rotate((float) (Math.PI/4),0,0,1));
                    poseStack.mulPoseMatrix(new Matrix4f().rotate((float) (Math.PI/4),1,0,0));
                    color = Color.HSBtoRGB(angle / 12f, .5f, .5f);
                }
            }



            Matrix4f matrix = poseStack.last().pose();

            for (int i = 0; i < faces.size(); i++) {
                List<Integer> face = faces.get(i);
                Vector3f vertex0 = projected.get(face.get(0));
                Vector3f vertex1 = projected.get(face.get(1));
                Vector3f vertex2 = projected.get(face.get(2));
                Vector3f vertex3 = projected.get(face.get(3));


                buffer.vertex(matrix, vertex0.x, vertex0.y, vertex0.z).color(color).uv(sprite.getU0(), sprite.getV0()).uv2(pPackedLight).normal(1, 1, 1).endVertex();
                buffer.vertex(matrix, vertex1.x, vertex1.y, vertex1.z).color(color).uv(sprite.getU0(), sprite.getV1()).uv2(pPackedLight).normal(1, 1, 1).endVertex();
                buffer.vertex(matrix, vertex2.x, vertex2.y, vertex2.z).color(color).uv(sprite.getU1(), sprite.getV1()).uv2(pPackedLight).normal(1, 1, 1).endVertex();
                buffer.vertex(matrix, vertex3.x, vertex3.y, vertex3.z).color(color).uv(sprite.getU1(), sprite.getV0()).uv2(pPackedLight).normal(1, 1, 1).endVertex();

//                buffer.vertex(matrix, vertex3.x, vertex3.y, vertex3.z).color(color).uv(sprite.getU1(), sprite.getV1()).uv2(pPackedLight).normal(-1, -1, -1).endVertex();
//                buffer.vertex(matrix, vertex2.x, vertex2.y, vertex2.z).color(color).uv(sprite.getU0(), sprite.getV1()).uv2(pPackedLight).normal(-1, -1, -1).endVertex();
//                buffer.vertex(matrix, vertex1.x, vertex1.y, vertex1.z).color(color).uv(sprite.getU0(), sprite.getV0()).uv2(pPackedLight).normal(-1, -1, -1).endVertex();
//                buffer.vertex(matrix, vertex0.x, vertex0.y, vertex0.z).color(color).uv(sprite.getU1(), sprite.getV0()).uv2(pPackedLight).normal(-1, -1, -1).endVertex();

            }


            poseStack.popPose();
            poseStack.popPose();
            long newmillis =  Util.getMillis();
            angle = (angle + (newmillis - oldmillis)/1500f) % 360f;

            oldmillis = newmillis;


        }
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
        BlockPos pos = data.offset;

        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotation((float) -(Math.PI/4),1,0,0);

        AABB boundingBox = data.bounds;
        Vector4f sizeVector = new Vector4f((float) boundingBox.getXsize(), (float) boundingBox.getYsize(), (float) boundingBox.getZsize(),1f);
        sizeVector = rotationMatrix.transform(sizeVector);

        double largestsize = Math.max(Math.max(sizeVector.x,sizeVector.y),sizeVector.z);
        float scale = (float) ( 1 / (largestsize*0.1));
        poseStack.scale(scale,scale,scale);


        poseStack.translate(pos.getX(),pos.getY(),pos.getZ());
        rotationMatrix.rotation((float) -(Math.PI/2), 0,1,0);
        poseStack.mulPoseMatrix(rotationMatrix);
        rotationMatrix.rotation((float) -(Math.PI/4), 1,0,0);
        poseStack.mulPoseMatrix(rotationMatrix);

        //poseStack.translate(0,-scale,0);
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
