package com.ducklingvivi.voxelweapons.datagen;

import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class VoxelRecipeProvider extends RecipeProvider {
    public VoxelRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        UpgradeRecipeBuilder.smithing(Ingredient.of(Registration.VOXEL_WEAPON_STARTER.get()),Ingredient.of(Registration.VOXEL_CATALYST_OVERWORLD.get()),RecipeCategory.COMBAT,Registration.VOXEL_WEAPON_OVERWORLD.get())
                .unlocks("starter_to_overworld", has(Registration.VOXEL_WEAPON_STARTER.get()))
                .save(pWriter,new ResourceLocation(voxelweapons.MODID, "starter_to_overworld"));
        UpgradeRecipeBuilder.smithing(Ingredient.of(Registration.VOXEL_WEAPON_OVERWORLD.get()),Ingredient.of(Registration.VOXEL_CATALYST_NETHER.get()),RecipeCategory.COMBAT,Registration.VOXEL_WEAPON_NETHER.get())
                .unlocks("overworld_to_nether", has(Registration.VOXEL_WEAPON_OVERWORLD.get()))
                .save(pWriter,new ResourceLocation(voxelweapons.MODID, "overworld_to_nether"));
        UpgradeRecipeBuilder.smithing(Ingredient.of(Registration.VOXEL_WEAPON_NETHER.get()),Ingredient.of(Registration.VOXEL_CATALYST_END.get()),RecipeCategory.COMBAT,Registration.VOXEL_WEAPON_END.get())
                .unlocks("nether_to_end", has(Registration.VOXEL_WEAPON_NETHER.get()))
                .save(pWriter, new ResourceLocation(voxelweapons.MODID, "nether_to_end"));
        UpgradeRecipeBuilder.smithing(Ingredient.of(Registration.VOXEL_WEAPON_END.get()),Ingredient.of(Registration.VOXEL_CATALYST_BOSS.get()),RecipeCategory.COMBAT,Registration.VOXEL_WEAPON_BOSS.get())
                .unlocks("end_to_boss", has(Registration.VOXEL_WEAPON_END.get()))
                .save(pWriter,new ResourceLocation(voxelweapons.MODID, "end_to_boss"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.VOXEL_CATALYST_STARTER.get())
                .pattern("DDD")
                .pattern("DED")
                .pattern("DDD")
                .define('E', Tags.Items.ENDER_PEARLS)
                .define('D', Tags.Items.DYES)
                .unlockedBy("has_starter",has(Registration.VOXEL_CATALYST_STARTER.get()))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.VOXEL_CATALYST_OVERWORLD.get())
                .pattern("LDL")
                .pattern("DVD")
                .pattern("LDL")
                .define('V', Registration.VOXEL_CATALYST_STARTER.get())
                .define('L', Tags.Items.LEATHER)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_starter",has(Registration.VOXEL_CATALYST_STARTER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.VOXEL_CATALYST_NETHER.get())
                .pattern("LDL")
                .pattern("DVD")
                .pattern("LDL")
                .define('V', Registration.VOXEL_CATALYST_OVERWORLD.get())
                .define('L', Tags.Items.RODS_BLAZE)
                .define('D', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("has_overworld",has(Registration.VOXEL_CATALYST_OVERWORLD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.VOXEL_CATALYST_END.get())
                .pattern("LDL")
                .pattern("DVD")
                .pattern("LDL")
                .define('V', Registration.VOXEL_CATALYST_NETHER.get())
                .define('L', Items.SHULKER_SHELL)
                .define('D', Items.CHORUS_FLOWER)
                .unlockedBy("has_nether",has(Registration.VOXEL_CATALYST_NETHER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.VOXEL_CATALYST_BOSS.get())
                .pattern("LDL")
                .pattern("DVD")
                .pattern("LDL")
                .define('V', Registration.VOXEL_CATALYST_END.get())
                .define('L', Items.END_CRYSTAL)
                .define('D', Tags.Items.NETHER_STARS)
                .unlockedBy("has_end",has(Registration.VOXEL_CATALYST_END.get()))
                .save(pWriter);

    }
}
