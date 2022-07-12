package com.github.mikndesu.miners;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.level.block.state.BlockState;

public class IntuitionResult {
    private static IntuitionResult intuitionResult = new IntuitionResult();
    private ArrayList<SimpleEntry<BlockState,Integer>> result = new ArrayList<>();
    public static IntuitionResult getInstance(){
        return intuitionResult;
    }
    public ArrayList<SimpleEntry<BlockState,Integer>> getResult() {
        return this.result;
    }
    public void clearResult() {
        this.result = new ArrayList<>();
    }
}