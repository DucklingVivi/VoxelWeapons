package com.ducklingvivi.voxelweapons.library;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.GameData;


import java.util.*;

public class VoxelData {

    public AABB bounds;
    public BlockPos offset;
    protected Map<BlockPos, StructureBlockInfo> blocks;
    protected  Map<BlockPos, FluidState> fluids;

    // Client
    public Map<BlockPos, ModelData> modelData;
    public Map<BlockPos, BlockEntity> presentTileEntities;

    public VoxelData(){
        blocks = new HashMap<>();
        fluids = new HashMap<>();
        modelData = new HashMap<>();
        presentTileEntities = new HashMap<>();
        offset = new BlockPos(0,0,0);
        bounds = new AABB(BlockPos.ZERO);
    }

    // TODO you're going to need a semi-persistent client-side structure to manage this anyway, so you define your packet so that it selects a specific area [at worst, a single block - you may need it if your blocks can be tile entities that may contain a large amount of data itself] to update

    public void readNBT(Level world, CompoundTag nbt) {
        blocks.clear();
        presentTileEntities.clear();
        Tag blocks = nbt.get("Blocks");
        if(blocks!=null){
            readBlocksCompound(blocks, world);
        }

        offset = NbtUtils.readBlockPos(nbt.getCompound("Offset"));
        bounds = voxelUtils.readAABB(nbt.getList("Bounds", Tag.TAG_COMPOUND));
    }



    public CompoundTag writeNBT(){
        CompoundTag nbt = new CompoundTag();
        nbt.put("Offset", NbtUtils.writeBlockPos(offset));
        nbt.put("Blocks", writeBlocksCompound());
        nbt.put("Bounds", voxelUtils.writeAABB(bounds));
        return nbt;
    }

    public static VoxelData fromNBT(Level world, CompoundTag nbt){
        VoxelData voxelData = new VoxelData();
        voxelData.readNBT(world, nbt);
        return voxelData;
    }
    protected StructureBlockInfo capture(Level world, BlockPos pos) {
        BlockState blockstate = world.getBlockState(pos);
        if (blockstate.getBlock() instanceof ButtonBlock) {
            blockstate = blockstate.setValue(ButtonBlock.POWERED, false);
            world.scheduleTick(pos, blockstate.getBlock(), -1);
        }
        if (blockstate.getBlock() instanceof PressurePlateBlock) {
            blockstate = blockstate.setValue(PressurePlateBlock.POWERED, false);
            world.scheduleTick(pos, blockstate.getBlock(), -1);
        }


        CompoundTag compoundnbt = getTileEntityNBT(world, pos);
        return new StructureBlockInfo(pos, blockstate, compoundnbt);
    }

    private CompoundTag getTileEntityNBT(Level world, BlockPos pos) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity == null)
            return null;
        CompoundTag nbt = tileentity.saveWithFullMetadata();
        nbt.remove("x");
        nbt.remove("y");
        nbt.remove("z");
        return nbt;
    }

    //TODO REMOVE
    public Map<BlockPos, StructureBlockInfo> devGetBlocks(){
        return blocks;
    }
    public Map<BlockPos, FluidState> devGetFluids(){
        return fluids;
    }

    public void devAddRange(BlockPos start, BlockPos end, Level World){
        for(BlockPos target : BlockPos.betweenClosed(start,end)){
            addBlock(target,capture(World,target));
        }
    }
    public void devAddBlock(BlockPos blockPos, Level world){
        addBlock(blockPos, capture(world, blockPos));
    }
    protected void addBlock(BlockPos pos, StructureBlockInfo pair) {
        if (pair.state.isAir()){
            return;
        }
        BlockPos localPos = pos.subtract(offset);
        StructureBlockInfo structureBlockInfo = new StructureBlockInfo(localPos, pair.state, pair.nbt);


        if (blocks.put(localPos, structureBlockInfo) != null)
            return;
        bounds = bounds.minmax(new AABB(localPos));
    }



    private CompoundTag writeBlocksCompound() {
        CompoundTag compound = new CompoundTag();
//        HashMapPalette<BlockState> palette = new HashMapPalette<>(GameData.getBlockStateIDMap(), 16, (i, s) -> {
//            throw new IllegalStateException("Palette Map index exceeded maximum value");
//        });
        ListTag blockList = new ListTag();
        for (StructureBlockInfo block : this.blocks.values()) {
            // int id = palette.idFor(block.state);
            CompoundTag c = new CompoundTag();
            c.putLong("Pos", block.pos.asLong());
            c.putInt("State", Block.getId(block.state));
            if (block.nbt != null)
                c.put("Data", block.nbt);
            blockList.add(c);
        }

        //ListTag paletteNBT = new ListTag();
//        for (int i = 0; i < palette.getSize(); ++i)
//            paletteNBT.add(NbtUtils.writeBlockState(palette.values.byId(i)));
        //compound.put("Palette", paletteNBT);
        compound.put("BlockList", blockList);

        return compound;

    }
//    private void readFluidsCompound(Tag compound) {
//
//        ListTag listTag = (ListTag) compound;
//        for (int i = 0; i < listTag.size(); i++) {
//            CompoundTag tag = listTag.getCompound(i);
//            BlockPos pos = BlockPos.of(tag.getLong("Pos"));
//            FluidState fluidState = voxelUtils.readFLuidState(voxelUtils.getLevel().holderLookup(Registries.FLUID),tag.getCompound("State"));
//            //TODO FIX FLUIDSTATE
//            fluids.put(pos, fluidState);
//        }
//
//    }
    private void readBlocksCompound(Tag compound, Level world) {
        HashMapPalette<BlockState> palette = null;
        CompoundTag d = ((CompoundTag) compound);
        /*if (usePalettedDeserialization) {
            CompoundTag c = ((CompoundTag) compound);
            palette = new HashMapPalette<>(GameData.getBlockStateIDMap(), 16, (i, s) -> {
                throw new IllegalStateException("Palette Map index exceeded maximum");
            });

            ListTag list = c.getList("Palette", Tag.TAG_COMPOUND);
            palette.values.clear();
            for (int i = 0; i < list.size(); ++i)
                palette.values.add(NbtUtils.readBlockState(voxelUtils.getLevel().holderLookup(Registries.BLOCK),list.getCompound(i)));
            blockList = c.getList("BlockList", Tag.TAG_COMPOUND);
        } else {
            blockList = (ListTag) compound;
        }
        HashMapPalette<BlockState> finalPalette = palette;*/

        ListTag blockList = d.getList("BlockList", Tag.TAG_COMPOUND);
        blockList.forEach(e -> {
            CompoundTag c = (CompoundTag) e;

            StructureBlockInfo info = readStructureBlockInfo(c);

            this.blocks.put(info.pos, info);

            if (!world.isClientSide)
                return;

            CompoundTag tag = info.nbt;
            if (tag == null)
                return;

            tag.putInt("x", info.pos.getX());
            tag.putInt("y", info.pos.getY());
            tag.putInt("z", info.pos.getZ());

            BlockEntity te = BlockEntity.loadStatic(info.pos, info.state, tag);
            if (te == null)
                return;
            te.setLevel(world);
            modelData.put(info.pos, te.getModelData());
            presentTileEntities.put(info.pos, te);
        });

    }

    private static StructureBlockInfo readStructureBlockInfo(CompoundTag blockListEntry) {
        return new StructureBlockInfo(BlockPos.of(blockListEntry.getLong("Pos")),
                Objects.requireNonNull(Block.stateById(blockListEntry.getInt("State"))),
                blockListEntry.contains("Data") ? blockListEntry.getCompound("Data") : null);
    }

    public Iterable<? extends StructureBlockInfo> devGetVoxels() {
        return blocks.values();
    }

}
