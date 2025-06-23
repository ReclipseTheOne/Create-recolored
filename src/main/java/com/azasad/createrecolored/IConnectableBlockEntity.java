package com.azasad.createrecolored;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public interface IConnectableBlockEntity extends IMultiBlockEntityContainer {
    boolean canConnectWith(BlockPos pos, Level level);
}
