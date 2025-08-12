package com.azasad.createcolored.content.blockEntities;

import com.azasad.createcolored.RecoloredConnectivityHandler;
import com.azasad.createcolored.IConnectableBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;

public class ColoredFluidTankBlockEntity extends FluidTankBlockEntity implements IConnectableBlockEntity {
    public ColoredFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide())
            return;
        if (!isController())
            return;
        RecoloredConnectivityHandler.formMulti(this);
    }

    @Override
    public boolean canConnectWith(BlockPos otherPos, Level level) {
        BlockEntity be = level.getBlockEntity(otherPos);
        if (be instanceof ColoredFluidTankBlockEntity) {
            return be.getBlockState().getBlock().equals(this.getBlockState().getBlock());
        }
        return false;
    }
}
