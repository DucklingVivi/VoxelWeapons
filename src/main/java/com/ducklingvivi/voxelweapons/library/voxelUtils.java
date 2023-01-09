package com.ducklingvivi.voxelweapons.library;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

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
}
