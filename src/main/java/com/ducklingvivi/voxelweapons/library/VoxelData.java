package com.ducklingvivi.voxelweapons.library;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.units.qual.C;


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


    public void readNBT(Level world, CompoundTag nbt) {
        blocks.clear();
        presentTileEntities.clear();
        Tag blocks = nbt.get("Blocks");
        boolean usePalettedDeserialization =
                blocks != null && blocks.getId() == Tag.TAG_COMPOUND && ((CompoundTag) blocks).contains("Palette");
        if(blocks!=null){
            readBlocksCompound(blocks, world, usePalettedDeserialization);
        }
        ListTag fluids = nbt.getList("Fluids",Tag.TAG_COMPOUND);
        if(fluids != null){
            readFluidsCompound(fluids);
        }

        offset = NbtUtils.readBlockPos(nbt.getCompound("Offset"));
        bounds = voxelUtils.readAABB(nbt.getList("Bounds", Tag.TAG_COMPOUND));
    }



    public CompoundTag writeNBT(){
        CompoundTag nbt = new CompoundTag();
        nbt.put("Offset", NbtUtils.writeBlockPos(offset));
        nbt.put("Blocks", writeBlocksCompound());
        nbt.put("Bounds", voxelUtils.writeAABB(bounds));
        nbt.put("Fluids", writeFluidsCompound());
        return nbt;
    }

    public static VoxelData fromNBT(Level world, CompoundTag nbt){
        VoxelData voxelData = new VoxelData();
        voxelData.readNBT(world, nbt);
        return voxelData;
    }
    protected Pair<StructureBlockInfo, FluidState> capture(Level world, BlockPos pos) {
        BlockState blockstate = world.getBlockState(pos);
        FluidState fluidstate = world.getFluidState(pos);
        if (blockstate.getBlock() instanceof ButtonBlock) {
            blockstate = blockstate.setValue(ButtonBlock.POWERED, false);
            world.scheduleTick(pos, blockstate.getBlock(), -1);
        }
        if (blockstate.getBlock() instanceof PressurePlateBlock) {
            blockstate = blockstate.setValue(PressurePlateBlock.POWERED, false);
            world.scheduleTick(pos, blockstate.getBlock(), -1);
        }


        CompoundTag compoundnbt = getTileEntityNBT(world, pos);
        return Pair.of(new StructureBlockInfo(pos, blockstate, compoundnbt), fluidstate);
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
    public void devAddBlock(BlockPos blockPos, Level world){
        addBlock(blockPos, capture(world, blockPos));
    }
    protected void addBlock(BlockPos pos, Pair<StructureBlockInfo, FluidState> pair) {
        StructureBlockInfo captured = pair.getKey();
        BlockPos localPos = pos.subtract(offset);
        StructureBlockInfo structureBlockInfo = new StructureBlockInfo(localPos, captured.state, captured.nbt);

        FluidState fluidState = pair.getValue();
        if(!fluidState.isEmpty()){
            fluids.put(localPos,fluidState);
        }

        if (blocks.put(localPos, structureBlockInfo) != null)
            return;
        bounds = bounds.minmax(new AABB(localPos));
    }

    private ListTag writeFluidsCompound(){
        ListTag fluidList = new ListTag();
        fluids.forEach((blockPos,fluidState)->{
            CompoundTag fluidTag = new CompoundTag();
            fluidTag.putLong("Pos",blockPos.asLong());
            fluidTag.put("State",voxelUtils.writeFluidState(fluidState));

            fluidList.add(fluidTag);
        });
        return fluidList;
    }

    private CompoundTag writeBlocksCompound() {
        CompoundTag compound = new CompoundTag();
        HashMapPalette<BlockState> palette = new HashMapPalette<>(GameData.getBlockStateIDMap(), 16, (i, s) -> {
            throw new IllegalStateException("Palette Map index exceeded maximum value");
        });
        ListTag blockList = new ListTag();
        for (StructureBlockInfo block : this.blocks.values()) {
            int id = palette.idFor(block.state);
            CompoundTag c = new CompoundTag();
            c.putLong("Pos", block.pos.asLong());
            c.putInt("State", id);
            if (block.nbt != null)
                c.put("Data", block.nbt);
            blockList.add(c);
        }



        ListTag paletteNBT = new ListTag();
        for (int i = 0; i < palette.getSize(); ++i)
            paletteNBT.add(NbtUtils.writeBlockState(palette.values.byId(i)));
        compound.put("Palette", paletteNBT);
        compound.put("BlockList", blockList);

        return compound;

    }
    private void readFluidsCompound(Tag compound) {

        ListTag listTag = (ListTag) compound;
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            BlockPos pos = BlockPos.of(tag.getLong("Pos"));
            FluidState fluidState = voxelUtils.readFLuidState(voxelUtils.getLevel().holderLookup(Registries.FLUID),tag.getCompound("State"));
            //TODO FIX FLUIDSTATE
            fluids.put(pos, fluidState);
        }

    }
    private void readBlocksCompound(Tag compound, Level world, boolean usePalettedDeserialization) {
        HashMapPalette<BlockState> palette = null;
        ListTag blockList;
        if (usePalettedDeserialization) {
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
        HashMapPalette<BlockState> finalPalette = palette;


        blockList.forEach(e -> {
            CompoundTag c = (CompoundTag) e;

            StructureBlockInfo info = usePalettedDeserialization ? readStructureBlockInfo(c, finalPalette) : legacyReadStructureBlockInfo(c);

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

    private static StructureBlockInfo readStructureBlockInfo(CompoundTag blockListEntry,
                                                             HashMapPalette<BlockState> palette) {
        return new StructureBlockInfo(BlockPos.of(blockListEntry.getLong("Pos")),
                Objects.requireNonNull(palette.valueFor(blockListEntry.getInt("State"))),
                blockListEntry.contains("Data") ? blockListEntry.getCompound("Data") : null);
    }
    private static StructureBlockInfo legacyReadStructureBlockInfo(CompoundTag blockListEntry) {
        return new StructureBlockInfo(NbtUtils.readBlockPos(blockListEntry.getCompound("Pos")),
                NbtUtils.readBlockState(voxelUtils.getLevel().holderLookup(Registries.BLOCK),blockListEntry.getCompound("Block")),
                blockListEntry.contains("Data") ? blockListEntry.getCompound("Data") : null);
    }

    public Iterable<? extends StructureBlockInfo> devGetVoxels() {
        return blocks.values();
    }

}
