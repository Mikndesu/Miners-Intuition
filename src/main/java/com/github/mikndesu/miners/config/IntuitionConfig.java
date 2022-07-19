package com.github.mikndesu.miners.config;

import java.util.ArrayList;
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
    public List<String> registryNameList = new ArrayList<>();
}