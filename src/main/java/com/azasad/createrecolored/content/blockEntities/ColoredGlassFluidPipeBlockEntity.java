package com.azasad.createrecolored.content.blockEntities;

import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;

public class ColoredGlassFluidPipeBlockEntity extends StraightPipeBlockEntity {

    public ColoredGlassFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}