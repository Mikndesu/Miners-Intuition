package com.github.mikndesu.miners;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.mikndesu.miners.config.IntuitionConfig;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class MinersIntuition implements ClientModInitializer {

    public static final String MOD_ID = "miners_intuition";
    public static final Logger LOGGER = LogManager.getLogger("MinersIntuition/Main");
    public static List<BlockState> acceptedBlocks = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(IntuitionConfig.class, JanksonConfigSerializer::new);
        var config = AutoConfig.getConfigHolder(IntuitionConfig.class).getConfig();
        for(var blockRegistryName:config.registryNameList) {
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