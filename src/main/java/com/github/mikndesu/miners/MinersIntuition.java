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

package com.github.mikndesu.miners;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.mikndesu.miners.config.IntuitionConfig;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class MinersIntuition implements ClientModInitializer {

    public static final String MOD_ID = "miners_intuition";
    public static final Logger LOGGER = LogManager.getLogger("MinersIntuition/Main");
    public static List<BlockState> acceptedBlocks = new ArrayList<>();
    public static IntuitionConfig configHolder;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(IntuitionConfig.class, GsonConfigSerializer::new);
        configHolder = AutoConfig.getConfigHolder(IntuitionConfig.class).getConfig();
        for(var blockRegistryName:configHolder.registryNameList) {
            try {
                var resourceLocation = new ResourceLocation(blockRegistryName);
                var block = Registry.BLOCK.get(resourceLocation);
                acceptedBlocks.add(block.defaultBlockState());
            } catch(ResourceLocationException e) {
                continue;
            }
        }  
    }
    
}