package com.azasad.createrecolored.content.block;

import com.azasad.createrecolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ColoredEncasedPipeBlock extends EncasedPipeBlock {
    private final DyeColor color;

    public ColoredEncasedPipeBlock(Properties properties, Supplier<Block> casing, DyeColor color) {
        super(properties, casing);
        this.color = color;
    }

    public ItemStack getPickedResult(HitResult result) {
        return ColoredBlocks.DYED_PIPES.get(color).asStack();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        BlockState equivalentPipe = transferSixWayProperties(state, ColoredBlocks.DYED_PIPES.get(color).getDefaultState());

        Direction firstFound = Direction.UP;
        for (Direction d : Direction.values())
            if (state.getValue(FACING_TO_PROPERTY_MAP.get(d))) {
                firstFound = d;
                break;
            }

        FluidTransportBehaviour.cacheFlows(level, pos);
        level.setBlockAndUpdate(pos, ColoredBlocks.DYED_PIPES.get(color).get()
                .updateBlockState(equivalentPipe, firstFound, null, level, pos));
        FluidTransportBehaviour.loadFlows(level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_ENCASED_FLUID_PIPE.get();
    }
}
