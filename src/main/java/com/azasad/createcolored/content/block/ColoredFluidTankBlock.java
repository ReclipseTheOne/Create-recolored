package com.azasad.createcolored.content.block;

import com.azasad.createcolored.RecoloredConnectivityHandler;
import com.azasad.createcolored.content.blockEntities.ColoredBlockEntities;
import com.azasad.createcolored.content.blockEntities.ColoredFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;

public class ColoredFluidTankBlock extends FluidTankBlock {
    protected final DyeColor color;
    protected ColoredFluidTankBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties, false);
        this.color = color;
    }

    public static boolean isColoredTank(BlockState state) {
        return (state.getBlock() instanceof ColoredFluidTankBlock);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof FluidTankBlockEntity))
                return;
            ColoredFluidTankBlockEntity tankBE = (ColoredFluidTankBlockEntity) be;
            level.removeBlockEntity(pos);
            RecoloredConnectivityHandler.splitMulti(tankBE); //Problem lies here
        }
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_FLUID_TANK_ENTITY.get();
    }
}
