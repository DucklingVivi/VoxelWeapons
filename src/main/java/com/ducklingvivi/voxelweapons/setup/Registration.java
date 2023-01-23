package com.ducklingvivi.voxelweapons.setup;

import com.ducklingvivi.voxelweapons.library.*;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.ducklingvivi.voxelweapons.voxelweapons.MODID;

public class Registration {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void init(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }


    public static final RegistryObject<VoxelItem> VOXELWEAPONITEM = ITEMS.register("voxelweapon", VoxelItem::new);
    public static final RegistryObject<VoxelCatalystItem> VOXELCATALYSTITEM = ITEMS.register("voxelcatalyst", VoxelCatalystItem::new);

    public static final RegistryObject<Block> VOXELFLOORBLOCK = BLOCKS.register("voxel_floor", ()-> new VoxelFloorBlock(VoxelFloorBlock.DEFAULTPROPERTIES));
    public static final RegistryObject<Block> VOXELFLOORBORDERBLOCK = BLOCKS.register("voxel_floor_border", ()-> new VoxelFloorBorderBlock(VoxelFloorBlock.DEFAULTPROPERTIES));

    public static final RegistryObject<Block> VOXELFLOORCONTROLLERBLOCK = BLOCKS.register("voxel_floor_controller", ()-> new VoxelFloorControllerBlock(VoxelFloorBlock.DEFAULTPROPERTIES));


    public static final RegistryObject<BlockEntityType<VoxelFloorControllerBlockEntity>> VOXELFLOORCONTROLLERBLOCKENTITY = BLOCK_ENTITIES.register("voxel_floor_controller", () -> BlockEntityType.Builder.of(VoxelFloorControllerBlockEntity::new, VOXELFLOORCONTROLLERBLOCK.get()).build(null));




    public static final RegistryObject<Block> VOXELORIGINBLOCK = BLOCKS.register("voxel_origin", ()-> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.QUARTZ).strength(-1,36000000.8F).sound(SoundType.STONE).lightLevel((state)->0)));



}
