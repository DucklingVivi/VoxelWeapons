package com.ducklingvivi.voxelweapons.library;



import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;



public class Voxel {
    BlockPos blockPos;
    public BlockState blockState;

    public Voxel(BlockPos blockPos, BlockState blockState) {
        this.blockPos = blockPos;
        this.blockState = blockState;
    }


    public CompoundTag toCompound() {

        CompoundTag tag = new CompoundTag();
        tag.putInt("x",blockPos.getX());
        tag.putInt("y",blockPos.getY());
        tag.putInt("z",blockPos.getZ());
        tag.put("blockstate", NbtUtils.writeBlockState(blockState));
        return tag;
    }

    public static Voxel fromCompound(CompoundTag tag) {




        return new Voxel(new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z")), NbtUtils.readBlockState(
                voxelUtils.getLevel().holderLookup(Registries.BLOCK),
                tag.getCompound("blockstate")));
    }


}

//    public static class BBVoxel {
//        public int x;
//        public int y;
//        public int textureIndex;
//        public BBVoxel(int x, int y, int textureIndex){
//            this.x = x;
//            this.y = y;
//            this.textureIndex = textureIndex;
//        }
//        public CompoundTag convertToCompound() {
//            CompoundTag tag = new CompoundTag();
//            tag.putInt("x", this.x);
//            tag.putInt("y", this.y);
//            tag.putInt("textureIndex", this.textureIndex);
//            return tag;
//        }
//        public static BBVoxel convertFromCompound(CompoundTag tag) {
//
//            return new BBVoxel(tag.getInt("x"),tag.getInt("y"),tag.getInt("textureIndex"));
//        }
//
//    }
//
//    public static class BBVoxelResourceData {
//        public List<BBVoxel> bbVoxels;
//        public Map<Integer,String> resourceMap;
//
//        BBVoxelResourceData(Map<Integer,String> resourceMap, List<BBVoxel> bbVoxels){
//            this.bbVoxels = bbVoxels;
//            this.resourceMap = resourceMap;
//        }
//
//        public CompoundTag convertToCompound() {
//            CompoundTag tag = new CompoundTag();
//            CompoundTag listTag = new CompoundTag();
//            listTag.putInt("size", bbVoxels.size());
//            for (int i = 0; i < bbVoxels.size(); i++) {
//                listTag.put(""+i,bbVoxels.get(i).convertToCompound());
//            }
//            tag.put("bbvoxels", listTag);
//            CompoundTag mapTag = new CompoundTag();
//            mapTag.putInt("size", resourceMap.size());
//            for (int i = 0; i < resourceMap.size(); i++) {
//                mapTag.putString(""+i,resourceMap.get(i));
//            }
//            tag.put("resourceMap", mapTag);
//            return tag;
//        }
//        public static BBVoxelResourceData convertFromCompound(CompoundTag tag){
//            List<BBVoxel> voxelList = new ArrayList<>();
//            Map<Integer, String> resourceMap = new HashMap<Integer, String>();
//            if(!tag.contains("bbvoxels")||!tag.contains("resourceMap")){
//                return null;
//            }
//            CompoundTag listTag = tag.getCompound("bbvoxels");
//            for (int i = 0; i < listTag.getInt("size"); i++) {
//                CompoundTag comp = listTag.getCompound(""+i);
//                voxelList.add(Voxel.BBVoxel.convertFromCompound(comp));
//            }
//
//            CompoundTag mapTag = tag.getCompound("resourceMap");
//            for (int i = 0; i < mapTag.getInt("size"); i++) {
//                resourceMap.put(i,mapTag.getString(""+i));
//            }
//
//
//            return new BBVoxelResourceData(resourceMap, voxelList);
//        }
//    }
//}
