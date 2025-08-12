package com.azasad.createcolored.content.block;

import com.azasad.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.*;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ColoredFluidPipeBlock extends FluidPipeBlock implements IColoredBlock {
    protected final DyeColor color;

    public ColoredFluidPipeBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    public static BlockState updateConnections(BlockAndTintGetter level, BlockPos pos, BlockState state, @Nullable Direction ignored) {
        BlockState newState = state;
        for (Direction d : Direction.values()) {
            if (d == ignored)
                continue;
            boolean shouldConnect = canConnectToColored(level, pos, state, d);
            newState = newState.setValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(d), shouldConnect);
        }
        return newState;
    }

    public static boolean canConnectToColored(BlockAndTintGetter level, BlockPos pos, BlockState state, Direction direction) {
        BlockPos neighbourPos = pos.offset(direction.getNormal());
        BlockState neighbourState = level.getBlockState(neighbourPos);

        //if it is a colored tank, and is not the same color as the pipe, don't connect
        if(ColoredFluidTankBlock.isColoredTank(neighbourState)){
            ColoredFluidPipeBlock block = (ColoredFluidPipeBlock) state.getBlock();
            ColoredFluidTankBlock other = (ColoredFluidTankBlock) neighbourState.getBlock();
            if(block.color != other.color)
                return false;
        }

        //Has fluid capability
        if (FluidPropagator.hasFluidCapability(level, neighbourPos, direction.getOpposite()))
            return true;
        //Is a vanilla fluid target
        if (VanillaFluidTargets.canProvideFluidWithoutCapability(neighbourState))
            return true;

        //is any type of pipe
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(level, neighbourPos, BracketedBlockEntityBehaviour.TYPE);
        if (isPipe(neighbourState)) {
            //if is a colored pipe and not of the same color, then we don't connect
            if (isColoredPipe(neighbourState) && !sameColor(state, neighbourState)) {
                return false;
            }

            //else just check for brackets and such
            return bracket == null || !bracket.isBracketPresent() ||
                    FluidPropagator.getStraightPipeAxis(neighbourState) == direction.getAxis();
        }

        //if it is a glass pipe and is not of the same color, we don't connect
        if (neighbourState.getBlock() instanceof ColoredGlassFluidPipeBlock glassPipe) {
            if (!sameColor(state, ColoredBlocks.DYED_PIPES.get(glassPipe.color).getDefaultState()))
                return false;
        }

        FluidTransportBehaviour transport = BlockEntityBehaviour.get(level, neighbourPos, FluidTransportBehaviour.TYPE);
        //If it can transport fluid, connect else we don't connect;
        if (transport == null)
            return false;
        return transport.canHaveFlowToward(neighbourState, direction.getOpposite());
    }

    public static BlockState addOppositeSide(BlockState state, Direction dir) {
        return state.setValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(dir), true).setValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(dir.getOpposite()), true);
    }

    public static boolean sameColor(BlockState state, BlockState neighbour) {
        return state.getBlock().equals(neighbour.getBlock());
    }

    public static boolean isColoredPipe(BlockState state) {
        return state.getBlock() instanceof ColoredFluidPipeBlock;
    }

    public static boolean isColoredGlassPipe(BlockState state) {
        return state.getBlock() instanceof ColoredGlassFluidPipeBlock;
    }

    public static boolean shouldDrawCasing(BlockState state) {
        if (!isColoredPipe(state))
            return false;
        for (Direction.Axis axis : Direction.Axis.values()) {
            int connections = 0;
            for (Direction direction : Direction.values())
                if (direction.getAxis() != axis && isOpenAt(state, direction))
                    connections++;
            if (connections > 2)
                return true;
        }
        return false;
    }

    public static boolean shouldDrawRim(Level level, BlockPos pos, BlockState state, Direction direction) {
        BlockPos offsetPos = pos.offset(direction.getNormal());
        BlockState facingState = level.getBlockState(offsetPos);
        if (facingState.getBlock() instanceof EncasedPipeBlock)
            return true;
        if (!canConnectToColored(level, pos, state, direction))
            return true;
        return !isColoredPipe(facingState);
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (tryRemoveBracket(context))
            return InteractionResult.SUCCESS;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Direction.Axis axis = getAxis(state);
        if (axis == null) {
            Vec3 clickLocation = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            double closest = Float.MAX_VALUE;
            Direction argClosest = Direction.UP;
            for (Direction direction : Direction.values()) {
                if (clickedFace.getAxis() == direction.getAxis())
                    continue;
                Vec3 centerOf = Vec3.atCenterOf(direction.getNormal());
                double distance = centerOf.distanceToSqr(clickLocation);
                if (distance < closest) {
                    closest = distance;
                    argClosest = direction;
                }
            }
            axis = argClosest.getAxis();
        }
        if (clickedFace.getAxis() == axis)
            return InteractionResult.PASS;
        if (!level.isClientSide()) {
            withBlockEntityDo(level, pos, fpte -> fpte.getBehaviour(FluidTransportBehaviour.TYPE).interfaces.values().stream().filter(pc -> pc != null && pc.hasFlow()).findAny().ifPresent($ -> AllAdvancements.GLASS_PIPE.awardTo(context.getPlayer())));
            FluidTransportBehaviour.cacheFlows(level, pos);
            level.setBlockAndUpdate(pos, ColoredBlocks.DYED_GLASS_PIPES.get(color).getDefaultState().setValue(GlassFluidPipeBlock.AXIS, axis).setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED)));
            FluidTransportBehaviour.loadFlows(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private Direction.Axis getAxis(BlockState state) {
        return FluidPropagator.getStraightPipeAxis(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        DyeColor color = DyeColor.getColor(heldItem);
        if (color != null) {
            if (!level.isClientSide())
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.random.nextFloat() * .2f);
            applyDye(state, level, pos, color);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, ray);
    }

    public void applyDye(BlockState state, Level level, BlockPos pos, @Nullable DyeColor color) {
        BlockState newState =
                (color == null ? ColoredBlocks.DYED_PIPES.get(DyeColor.WHITE) : ColoredBlocks.DYED_PIPES.get(color)).getDefaultState();

        //Update newState block state
        newState = updateBlockState(newState, Direction.UP, null, level, pos);

        //Dye the block itself
        if (state != newState) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    @Override
    public BlockState updateBlockState(BlockState state, Direction preferredDirection, @Nullable Direction ignore, BlockAndTintGetter level, BlockPos pos) {
        // Do nothing if we are bracketed
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(level, pos, BracketedBlockEntityBehaviour.TYPE);
        if (bracket != null && bracket.isBracketPresent())
            return state;

        // get and store initial state
        BlockState prevState = state;
        int prevStateSides = (int) Arrays.stream(Direction.values()).map(EncasedPipeBlock.FACING_TO_PROPERTY_MAP::get).filter(prevState::getValue).count();

        //Update pipe connections
        state = updateConnections(level, pos, state, ignore);

        // see if it has enough connections
        Direction connectedDirection = null;
        for (Direction d : Direction.values()) {
            if (isOpenAt(state, d)) {
                if (connectedDirection != null) {
                    return state;
                }
                connectedDirection = d;
            }
        }

        // //add opposite end if only one connection
        if (connectedDirection != null) {
            return state.setValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(connectedDirection.getOpposite()), true);
        }
        // return pipe facing at the opposite of the direction of the previous
        // state
        if (prevStateSides == 2) {
            Direction foundDir = null;
            for (Direction d : Direction.values()) {
                if (prevState.getValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(d))) {
                    foundDir = d;
                    break;
                }
            }
            if (foundDir != null)
                return addOppositeSide(state, foundDir);
        }
        return addOppositeSide(state, preferredDirection);
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_FLUID_PIPE_ENTITY.get();
    }
}
