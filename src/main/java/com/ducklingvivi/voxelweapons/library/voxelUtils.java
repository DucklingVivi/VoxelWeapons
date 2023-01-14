package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.voxelweapons;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Optional;

public class voxelUtils {

    private static Level getLevelServer(){
        //HACKY
        return ServerLifecycleHooks.getCurrentServer().overworld();
    }
    private static Level getLevelClient(){
        //SLIGHTLY HACKY
        return Minecraft.getInstance().level;
    }
    public static Level getLevel(){
        Level level = null;
        //VERY HACKY OMG
        var temp = DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () ->voxelUtils::getLevelServer);
        if (temp!=null) level = temp;
        var temp2 = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> voxelUtils::getLevelClient);
        if (temp2!=null) level = temp2;

        return level;
    }
    public static ListTag writeAABB(AABB bb) {
        ListTag bbtag = new ListTag();
        bbtag.add(FloatTag.valueOf((float) bb.minX));
        bbtag.add(FloatTag.valueOf((float) bb.minY));
        bbtag.add(FloatTag.valueOf((float) bb.minZ));
        bbtag.add(FloatTag.valueOf((float) bb.maxX));
        bbtag.add(FloatTag.valueOf((float) bb.maxY));
        bbtag.add(FloatTag.valueOf((float) bb.maxZ));
        return bbtag;
    }

    public static AABB readAABB(ListTag bbtag) {
        if (bbtag == null || bbtag.isEmpty())
            return null;
        return new AABB(bbtag.getFloat(0), bbtag.getFloat(1), bbtag.getFloat(2), bbtag.getFloat(3),
                bbtag.getFloat(4), bbtag.getFloat(5));
    }






    public static CompoundTag writeFluidState(FluidState p_178023_) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", BuiltInRegistries.FLUID.getKey(p_178023_.getType()).toString());
        ImmutableMap<Property<?>, Comparable<?>> immutablemap = p_178023_.getValues();
        if (!immutablemap.isEmpty()) {
            CompoundTag compoundtag1 = new CompoundTag();

            for(Map.Entry<Property<?>, Comparable<?>> entry : immutablemap.entrySet()) {
                Property<?> property = entry.getKey();
                compoundtag1.putString(property.getName(), getName(property, entry.getValue()));
            }

            compoundtag.put("Properties", compoundtag1);
        }

        return compoundtag;
    }

    public static FluidState readFLuidState(HolderGetter<Fluid> p_256363_, CompoundTag p_250775_) {
        if (!p_250775_.contains("Name", 8)) {
            return Fluids.EMPTY.defaultFluidState();
        } else {
            ResourceLocation resourcelocation = new ResourceLocation(p_250775_.getString("Name"));
            Optional<? extends Holder<Fluid>> optional = p_256363_.get(ResourceKey.create(Registries.FLUID, resourcelocation));
            if (optional.isEmpty()) {
                return Fluids.EMPTY.defaultFluidState();
            } else {
                Fluid fluid = optional.get().value();
                FluidState fluidState = fluid.defaultFluidState();
                if (p_250775_.contains("Properties", 10)) {
                    CompoundTag compoundtag = p_250775_.getCompound("Properties");
                    StateDefinition<Fluid, FluidState> statedefinition = fluid.getStateDefinition();

                    for(String s : compoundtag.getAllKeys()) {
                        Property<?> property = statedefinition.getProperty(s);
                        if (property != null) {
                            fluidState = setValueHelper(fluidState, property, s, compoundtag, p_250775_);
                        }
                    }
                }

                return fluidState;
            }
        }
    }
    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S p_129205_, Property<T> p_129206_, String p_129207_, CompoundTag p_129208_, CompoundTag p_129209_) {
        Optional<T> optional = p_129206_.getValue(p_129208_.getString(p_129207_));
        if (optional.isPresent()) {
            return p_129205_.setValue(p_129206_, optional.get());
        } else {
            voxelweapons.LOGGER.warn("Unable to read property: {} with value: {} for fluidstate: {}", p_129207_, p_129208_.getString(p_129207_), p_129209_.toString());
            return p_129205_;
        }
    }

    private static <T extends Comparable<T>> String getName(Property<T> p_129211_, Comparable<?> p_129212_) {
        return p_129211_.getName((T)p_129212_);
    }


}



