package com.github.mikndesu.miners.asm.mixin;

import com.github.mikndesu.miners.IntuitionResult;
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
        int i = 0;
        for(var element:IntuitionResult.getInstance().getResult()) {
            if(element.getValue() != 0) {
                int baseX = 0;
                int baseY = i * 20;
                String text = element.getValue() > 30 ? "30+": String.valueOf(element.getValue());
                textRenderer.draw(poseStack, "\u00d7 " + text, baseX + 20, baseY+5, 0xFFFFFF);
                itemRenderer.renderGuiItem(new ItemStack(element.getKey().getBlock().asItem()), baseX, baseY);
                i++;
            }
        }
        modelMatrices.popPose();
        modelMatrices.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
