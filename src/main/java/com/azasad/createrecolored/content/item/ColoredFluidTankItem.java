package com.azasad.createrecolored.content.item;

import com.azasad.createrecolored.ColoredConnectivityHandler;
import com.azasad.createrecolored.content.block.ColoredFluidTankBlock;
import com.azasad.createrecolored.content.blockEntities.ColoredBlockEntities;
import com.azasad.createrecolored.content.blockEntities.ColoredFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class ColoredFluidTankItem extends FluidTankItem {
    public ColoredFluidTankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    //TODO: Refactor this too
    private void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isShiftKeyDown())
            return;
        Direction face = ctx.getClickedFace();
        if (!face.getAxis()
                .isVertical())
            return;
        ItemStack stack = ctx.getItemInHand();
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = level.getBlockState(placedOnPos);

        //If we aren't placing in another colored tank, abort
        if (!ColoredFluidTankBlock.isTank(placedOnState))
            return;
        ColoredFluidTankBlockEntity tankAt = ColoredConnectivityHandler.partAt(
                ColoredBlockEntities.COLORED_FLUID_TANK_ENTITY.get(), level, placedOnPos
        );
        if (tankAt == null)
            return;
        FluidTankBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.getWidth();
        if (width == 1)
            return;

        int tanksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN ?
                controllerBE.getBlockPos().relative(Direction.DOWN)
                :
                controllerBE.getBlockPos().relative(Direction.UP, controllerBE.getHeight());

        if (startPos.getY() != pos.getY())
            return;

        //For all connected blocks in the layer below or above
        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = level.getBlockState(offsetPos);
                if (ColoredFluidTankBlock.isTank(blockState))
                    continue;
                if (!blockState.canBeReplaced())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = level.getBlockState(offsetPos);
                if (ColoredFluidTankBlock.isTank(blockState))
                    continue;
                BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
//                player.getCustomData()
//                        .method_10556("SilenceTankSound", true);
                place(context);
//                player.getCustomData()
//                        .method_10551("SilenceTankSound");
            }
        }
    }
}
