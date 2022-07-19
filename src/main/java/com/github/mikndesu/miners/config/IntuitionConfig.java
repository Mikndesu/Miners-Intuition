package com.github.mikndesu.miners.config;

import java.util.Arrays;
import java.util.List;

import com.github.mikndesu.miners.MinersIntuition;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.RequiresRestart;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = MinersIntuition.MOD_ID)
public class IntuitionConfig implements ConfigData {
    @RequiresRestart
    @Comment("Changes will be applied to your client after restarting.")
    public List<String> registryNameList = Arrays.asList("minecraft:diamond_ore", "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:deepslate_diamond_ore", "minecraft:deepslate_gold_ore", "minecraft:deepslate_iron_ore");
    public int efficientRadius = 4;
}