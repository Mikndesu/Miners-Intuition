/*
 Copyright (c) 2022 Mikndesu

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
@Environment(EnvType.CLIENT)
public class GuiMixin {
    @Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    public void inject(PoseStack poseStack, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
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
        for (var element : IntuitionResult.getInstance().getResult()) {
            int baseX = 0;
            int baseY = i * 20;
            if (element.getValue() != 0) {
                if (element.getKey() == Blocks.LAVA.defaultBlockState() && element.getValue()>=5 && isLavaCautionAvailable(player)) {
                    String text = "Caution: Lava Lake may be near by!";
                    textRenderer.draw(poseStack, text, (client.getWindow().getGuiScaledWidth()-client.font.width(text))/2, baseY + 5, 0xDC143C);
                } else {
                    String text = element.getValue() > 30 ? "30+" : String.valueOf(element.getValue());
                    textRenderer.draw(poseStack, "\u00d7 " + text, baseX + 20, baseY + 5, 0xFFFFFF);
                    itemRenderer.renderGuiItem(new ItemStack(element.getKey().getBlock().asItem()), baseX, baseY);
                    i++;
                }
            }
        }
        modelMatrices.popPose();
        modelMatrices.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public boolean isLavaCautionAvailable(LocalPlayer player) {
        var dimension = player.clientLevel.dimension();
        if(dimension == Level.NETHER) {
            return false;
        } else if(dimension == Level.OVERWORLD) {
            return player.position().y < 0;
        }
        return true;
    }
}
