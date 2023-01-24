package com.ducklingvivi.voxelweapons.client.render;

import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.joml.Matrix4f;

public class ItemTooltip implements TooltipComponent ,ClientTooltipComponent {

    private ItemStack item;
    private FormattedText text;
    public ItemTooltip(ItemStack item, FormattedText text){
        this.item = item;
        this.text = text;
    }
    @Override
    public int getHeight() {
        return 8;
    }

    @Override
    public int getWidth(Font pFont) {
        return 8;
    }

    @Override
    public void renderImage(Font pFont, int pMouseX, int pMouseY, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset) {

        //pItemRenderer.render(item, ItemTransforms.TransformType.GUI,false,pPoseStack, Minecraft.getInstance().renderBuffers().bufferSource(),15728880, OverlayTexture.NO_OVERLAY,pItemRenderer.getModel(item,null,null,0));
        TextureAtlasSprite sprite =  Minecraft.getInstance().getTextureAtlas( new ResourceLocation("textures/atlas/blocks.png")).apply(new ResourceLocation("item/ender_pearl"));


        blit(pPoseStack,pMouseX+pFont.width(text),pMouseY,pBlitOffset,sprite);
    }

    private void blit(PoseStack poseStack, int px, int py, int blitoffset, TextureAtlasSprite sprite){

        RenderSystem.setShaderTexture(0,sprite.atlasLocation());
        GuiComponent.blit(poseStack,px,py,blitoffset,8,8,sprite);
    }


    public static void registerFactory()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemTooltip::onRegisterTooltipEvent);
    }

    private static void onRegisterTooltipEvent(RegisterClientTooltipComponentFactoriesEvent event)
    {
        event.register(ItemTooltip.class, x -> x);
    }

}
