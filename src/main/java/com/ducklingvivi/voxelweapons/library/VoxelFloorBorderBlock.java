package com.ducklingvivi.voxelweapons.library;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class VoxelFloorBorderBlock extends VoxelFloorBlock{


    public static final ConnectionProperty CONNECTION_PROPERTY = new ConnectionProperty("connectingto", Connection.class, Arrays.stream(Connection.values()).toList());

    public VoxelFloorBorderBlock(Properties pProperties) {
        super(pProperties);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(CONNECTION_PROPERTY);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        
        return this.defaultBlockState().setValue(CONNECTION_PROPERTY, Connection.FULL);
    }

    public static class ConnectionProperty extends EnumProperty<Connection> {
        protected ConnectionProperty(String pName, Class<Connection> pClazz, Collection<Connection> pValues) {
            super(pName, pClazz, pValues);
        }
    }

    public enum Connection implements StringRepresentable {
        FULL,
        NORTHWESTCORNER,
        NORTHEASTCORNER,
        SOUTHEASTCORNER,
        SOUTHWESTCORNER,
        EDGENORTH,
        EDGEEAST,
        EDGESOUTH,
        EDGEWEST,

        EDGENORTHSOUTH,
        EDGEEASTWEST,
        CENTER;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

}
