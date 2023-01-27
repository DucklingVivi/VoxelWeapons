package com.ducklingvivi.voxelweapons.client.data;

import com.ducklingvivi.voxelweapons.client.render.VoxelControllerRenderer;
import com.ducklingvivi.voxelweapons.library.VoxelTier;
import net.minecraft.core.BlockPos;

public class VoxelCreatorClientData {

    public static int BUILDTIMERTICKTIME = 100;
    public static VoxelCreatorClientData INSTANCE = new VoxelCreatorClientData();
    public VoxelControllerRenderer.MenuItem currentMenuItem;
    public boolean isWireFrameVisible;
    public boolean isOriginVisible;
    public int exitTimer = 0;
    private BlockPos origin;
    private VoxelTier tier;

    public VoxelCreatorClientData() {
        origin = BlockPos.ZERO;
        tier = VoxelTier.STARTER;
        currentMenuItem = null;
        isWireFrameVisible = false;

    }

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public VoxelTier getTier() {
        return tier;
    }

    public void setTier(VoxelTier tier){
        this.tier = tier;
    }
}
