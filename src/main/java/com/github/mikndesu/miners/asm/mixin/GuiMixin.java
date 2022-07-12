package com.github.mikndesu.miners.asm.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
@Environment(EnvType.CLIENT)
public class GuiMixin {
    @Inject(method = "renderPlayerHealth", at=@At(value="INVOKE", target="Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    public void inject(PoseStack poseStack, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        final Font textRenderer = client.font;
        final ItemRenderer itemRenderer = client.getItemRenderer();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
        PoseStack modelMatrices = RenderSystem.getModelViewStack();
        modelMatrices.pushPose();
        modelMatrices.translate(5, 5, 1.0);
        modelMatrices.pushPose();
        modelMatrices.scale(1.0f, 1.0f, 1.0f);
        RenderSystem.applyModelViewMatrix();
        textRenderer.draw(poseStack, "Text", 5, 5, -1);
        itemRenderer.renderGuiItem(new ItemStack(Items.DIAMOND_ORE), 0, 20);
        modelMatrices.popPose();
        modelMatrices.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
