package com.ducklingvivi.voxelweapons.setup;

import com.ducklingvivi.voxelweapons.library.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

import java.util.function.Predicate;

import static com.ducklingvivi.voxelweapons.voxelweapons.MODID;

public class Registration {

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void init(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        RECIPE_SERIALIZERS.register(bus);

    }

    public static final Predicate<Holder<Item>> VOXEL_WEAPON_PREDICATE = itemHolder -> itemHolder.get().getClass() == VoxelWeaponItem.class;
    public static final Predicate<Holder<Item>> VOXEL_CATALYST_PREDICATE = itemHolder -> itemHolder.get().getClass() == VoxelCatalystItem.class;
    public static final RegistryObject<Item> VOXEL_WEAPON_STARTER = ITEMS.register("voxel_weapon_starter", () -> new VoxelWeaponItem(VoxelTier.STARTER));
    public static final RegistryObject<Item> VOXEL_WEAPON_OVERWORLD = ITEMS.register("voxel_weapon_overworld", () -> new VoxelWeaponItem(VoxelTier.OVERWORLD));
    public static final RegistryObject<Item> VOXEL_WEAPON_NETHER = ITEMS.register("voxel_weapon_nether", () -> new VoxelWeaponItem(VoxelTier.NETHER));
    public static final RegistryObject<Item> VOXEL_WEAPON_END = ITEMS.register("voxel_weapon_end", () -> new VoxelWeaponItem(VoxelTier.END));
    public static final RegistryObject<Item> VOXEL_WEAPON_BOSS = ITEMS.register("voxel_weapon_boss", () -> new VoxelWeaponItem(VoxelTier.BOSS));

    public static final RegistryObject<Item> VOXEL_CATALYST_STARTER = ITEMS.register("voxel_catalyst_starter", () -> new VoxelCatalystItem(VoxelTier.STARTER));
    public static final RegistryObject<Item> VOXEL_CATALYST_OVERWORLD = ITEMS.register("voxel_catalyst_overworld", () -> new VoxelCatalystItem(VoxelTier.OVERWORLD));
    public static final RegistryObject<Item> VOXEL_CATALYST_NETHER = ITEMS.register("voxel_catalyst_nether", () -> new VoxelCatalystItem(VoxelTier.NETHER));
    public static final RegistryObject<Item> VOXEL_CATALYST_END = ITEMS.register("voxel_catalyst_end", () -> new VoxelCatalystItem(VoxelTier.END));
    public static final RegistryObject<Item> VOXEL_CATALYST_BOSS = ITEMS.register("voxel_catalyst_boss", () -> new VoxelCatalystItem(VoxelTier.BOSS));


    public static final RegistryObject<Block> VOXELFLOORBLOCK = BLOCKS.register("voxel_floor", ()-> new VoxelFloorBlock(VoxelFloorBlock.DEFAULTPROPERTIES));
    public static final RegistryObject<Block> VOXELFLOORBORDERBLOCK = BLOCKS.register("voxel_floor_border", ()-> new VoxelFloorBorderBlock(VoxelFloorBlock.DEFAULTPROPERTIES));
    public static final RegistryObject<Block> VOXELFLOORCONTROLLERBLOCK = BLOCKS.register("voxel_floor_controller", ()-> new VoxelFloorControllerBlock(VoxelFloorBlock.DEFAULTPROPERTIES));
    public static final RegistryObject<BlockEntityType<VoxelFloorControllerBlockEntity>> VOXELFLOORCONTROLLERBLOCKENTITY = BLOCK_ENTITIES.register("voxel_floor_controller", () -> BlockEntityType.Builder.of(VoxelFloorControllerBlockEntity::new, VOXELFLOORCONTROLLERBLOCK.get()).build(null));
    public static final RegistryObject<Block> VOXELORIGINBLOCK = BLOCKS.register("voxel_origin", ()-> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.QUARTZ).strength(-1,36000000.8F).sound(SoundType.STONE).lightLevel((state)->0)));
}
