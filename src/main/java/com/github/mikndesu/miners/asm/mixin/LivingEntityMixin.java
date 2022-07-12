package com.github.mikndesu.miners.asm.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.mikndesu.miners.MinersIntuition;
import com.google.common.collect.ImmutableList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    List<Vec3i> searchVectorDirections = new ArrayList<>();
    List<Vec3i> searchVectorWhenWalking = new ArrayList<>();
    List<BlockState> targetBlocks = ImmutableList.of(Blocks.DIAMOND_ORE.defaultBlockState(), Blocks.GOLD_ORE.defaultBlockState(), Blocks.IRON_ORE.defaultBlockState(), Blocks.ANCIENT_DEBRIS.defaultBlockState());

    @Inject(method= "tick()V", at=@At("HEAD"))
    private void inject(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if(searchVectorWhenWalking.size() == 0) {
            for(int i=-2;i<3;i++) {
                for(int k=-2;k<3;k++) {
                    for(int j=-2;j<3;j++) {
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
            if(livingEntity instanceof Player player && player.tickCount%60==0) {
                var level = player.getLevel();
                var blockPos = player.blockPosition();
                for (var direction : searchVectorWhenWalking) {
                    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                    mutablePos.set(blockPos).move(direction);
                    BlockState bs = level.getBlockState(mutablePos).getBlock().defaultBlockState();
                    if (targetBlocks.stream().anyMatch(s->s.equals(bs))) {
                        ArrayList<BlockPos> list = searchSameOres(new ArrayList<>(), bs, blockPos, level);
                        MinersIntuition.LOGGER.error("{}: {}", bs.getBlock().getDescriptionId(), list.size());
                    }
                }
            }
        }
    }

    private ArrayList<BlockPos> searchSameOres(ArrayList<BlockPos> list, BlockState blockState, BlockPos blockPos, Level level) {
        if (list.size() >= 2000) return list;
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
