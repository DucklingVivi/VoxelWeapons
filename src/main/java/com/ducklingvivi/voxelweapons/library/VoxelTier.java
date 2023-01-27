package com.ducklingvivi.voxelweapons.library;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.AABB;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public enum VoxelTier implements StringRepresentable {


    STARTER("item.voxelweapons.tier.starter", new Color(106,106,104).getRGB(),-3,1,-3,3,7,3),
    OVERWORLD("item.voxelweapons.tier.overworld",new Color(134,134,229).getRGB(),-5,1,-5,5,11,5),
    NETHER("item.voxelweapons.tier.nether",new Color(233,182,136).getRGB(),-7,1,-7,7,15,7),
    END("item.voxelweapons.tier.end",new Color(190,144,229).getRGB(),-9,1,-9,9,19,9),
    BOSS("item.voxelweapons.tier.boss",new Color(225,6,67).getRGB(),-11,1,-11,11,23,11);

    public final AABB boundingBox;

    public final String languageKey;

    public final int color;

    public static Map<Integer, VoxelTier> tierMap = Map.of(0,STARTER,1,OVERWORLD,2,NETHER,3,END,4,BOSS);
    VoxelTier(String languageKey ,int color,int xmin, int ymin, int zmin, int xmax, int ymax, int zmax){
        this.color = color;
        this.languageKey = languageKey;
        this.boundingBox = new AABB(xmin,ymin,zmin,xmax+1,ymax+1,zmax+1);
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }
}