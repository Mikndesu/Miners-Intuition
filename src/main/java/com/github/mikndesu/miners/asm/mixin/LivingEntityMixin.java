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

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.mikndesu.miners.IntuitionResult;
import com.github.mikndesu.miners.MinersIntuition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    List<Vec3i> searchVectorDirections = new ArrayList<>();
    List<Vec3i> searchVectorWhenWalking = new ArrayList<>();

    @Inject(method= "tick()V", at=@At("HEAD"))
    private void inject(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if(searchVectorWhenWalking.size() == 0) {
            int radius = MinersIntuition.configHolder.effectiveRadius;
            for(int i=-radius;i<radius+1;i++) {
                for(int j=-radius;j<radius+1;j++) {
                    for(int k=-radius;k<radius+1;k++) {
                        searchVectorWhenWalking.add(new Vec3i(i,j,k));
                    }
                }
            }
        }
        if(searchVectorDirections.size() == 0) {
            for(int i=-1;i<2;i++) {
                for(int k=-1;k<2;k++) {
                    for(int j=-1;j<2;j++) {
                        searchVectorDirections.add(new Vec3i(i,j,k));
                    }
                }
            }
        }
        if(livingEntity.getLevel().isClientSide()) {
            if(livingEntity instanceof Player player && player.tickCount%3==0) {
                IntuitionResult intuitionResult = IntuitionResult.getInstance();
                intuitionResult.clearResult();
                var level = player.getLevel();
                var blockPos = player.getOnPos();
                for (var direction : searchVectorWhenWalking) {
                    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                    mutablePos.set(blockPos).move(direction);
                    BlockState bs = level.getBlockState(mutablePos).getBlock().defaultBlockState();
                    if (MinersIntuition.acceptedBlocks.stream().anyMatch(s->s.equals(bs))) {
                        ArrayList<BlockPos> list = searchSameOres(new ArrayList<>(), bs, mutablePos, level);
                        if(intuitionResult.getResult().stream().noneMatch(s->bs.equals(s.getKey()))) {
                            intuitionResult.getResult().add(new SimpleEntry<BlockState,Integer>(bs,Integer.valueOf(list.size())));
                        }
                    }
                }
            }
        }
    }

    private ArrayList<BlockPos> searchSameOres(ArrayList<BlockPos> list, BlockState blockState, BlockPos blockPos, Level level) {
        if (list.size() >= 30) {
            list.add(new BlockPos.MutableBlockPos());
            return list;
        }
        for (var direction : searchVectorDirections) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            mutablePos.set(blockPos).move(direction);
            BlockState bs = level.getBlockState(mutablePos).getBlock().defaultBlockState();
            if (bs.equals(blockState) && list.stream().noneMatch(s->s.equals(mutablePos))) {
                list.add(mutablePos);
                list = searchSameOres(list, blockState, mutablePos, level);
            }
        }
        return list;
    }
}
