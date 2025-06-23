package com.azasad.createrecolored.content.blockEntities;

import com.azasad.createrecolored.ColoredConnectivityHandler;
import com.azasad.createrecolored.IConnectableBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ColoredFluidTankBlockEntity extends FluidTankBlockEntity implements IConnectableBlockEntity {
    public ColoredFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void updateConnectivity() {
        updateConnectivity = false;
        if (this.getLevel().isClientSide())
            return;
        if (!isController())
            return;
        ColoredConnectivityHandler.formMulti(this);
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
