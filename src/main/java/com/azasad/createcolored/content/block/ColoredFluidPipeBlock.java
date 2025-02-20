package com.azasad.createcolored.content.block;

import com.azasad.createcolored.CreateColored;
import com.azasad.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.*;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import io.github.fabricators_of_create.porting_lib.util.TagUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ColoredFluidPipeBlock extends FluidPipeBlock implements IColoredBlock {
    protected final DyeColor color;

    public ColoredFluidPipeBlock(Settings properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    public static BlockState updateConnections(BlockRenderView world, BlockPos pos, BlockState state, @Nullable Direction ignored) {
        BlockState newState = state;
        for (Direction d : Iterate.directions) {
            if (d == ignored)
                continue;
            boolean shouldConnect = canConnectToColored(world, pos, state, d);
            newState = newState.with(FACING_PROPERTIES.get(d), shouldConnect);
        }
        return newState;
    }

    public static boolean canConnectToColored(BlockRenderView world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos neighbourPos = pos.offset(direction);
        BlockState neighbourState = world.getBlockState(neighbourPos);

        //if it is a colored tank, and is not the same color as the pipe, don't connect
        if(ColoredFluidTankBlock.isColoredTank(neighbourState)){
            ColoredFluidPipeBlock block = (ColoredFluidPipeBlock) state.getBlock();
            ColoredFluidTankBlock other = (ColoredFluidTankBlock) neighbourState.getBlock();
            if(block.color != other.color)
                return false;
        }

        //Has fluid capability
        if (FluidPropagator.hasFluidCapability(world, neighbourPos, direction.getOpposite()))
            return true;
        //Is a vanilla fluid target
        if (VanillaFluidTargets.canProvideFluidWithoutCapability(neighbourState))
            return true;

        //is any type of pipe
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, neighbourPos, BracketedBlockEntityBehaviour.TYPE);
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

        FluidTransportBehaviour transport = BlockEntityBehaviour.get(world, neighbourPos, FluidTransportBehaviour.TYPE);
        //If it can transport fluid, connect else we don't connect;
        if (transport == null)
            return false;
        return transport.canHaveFlowToward(neighbourState, direction.getOpposite());
    }

    public static BlockState addOppositeSide(BlockState state, Direction dir) {
        return state.with(FACING_PROPERTIES.get(dir), true).with(FACING_PROPERTIES.get(dir.getOpposite()), true);
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
        for (Direction.Axis axis : Iterate.axes) {
            int connections = 0;
            for (Direction direction : Iterate.directions)
                if (direction.getAxis() != axis && isOpenAt(state, direction))
                    connections++;
            if (connections > 2)
                return true;
        }
        return false;
    }

    public static boolean shouldDrawRim(BlockRenderView world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos offsetPos = pos.offset(direction);
        BlockState facingState = world.getBlockState(offsetPos);
        if (facingState.getBlock() instanceof EncasedPipeBlock)
            return true;
        if (!canConnectToColored(world, pos, state, direction))
            return true;
        return !isColoredPipe(facingState);
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
        if (tryRemoveBracket(context))
            return ActionResult.SUCCESS;
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction clickedFace = context.getSide();
        Direction.Axis axis = getAxis(state);
        if (axis == null) {
            Vec3d clickLocation = context.getHitPos().subtract(pos.getX(), pos.getY(), pos.getZ());
            double closest = Float.MAX_VALUE;
            Direction argClosest = Direction.UP;
            for (Direction direction : Iterate.directions) {
                if (clickedFace.getAxis() == direction.getAxis())
                    continue;
                Vec3d centerOf = Vec3d.ofCenter(direction.getVector());
                double distance = centerOf.squaredDistanceTo(clickLocation);
                if (distance < closest) {
                    closest = distance;
                    argClosest = direction;
                }
            }
            axis = argClosest.getAxis();
        }
        if (clickedFace.getAxis() == axis)
            return ActionResult.PASS;
        if (!world.isClient) {
            withBlockEntityDo(world, pos, fpte -> fpte.getBehaviour(FluidTransportBehaviour.TYPE).interfaces.values().stream().filter(pc -> pc != null && pc.hasFlow()).findAny().ifPresent($ -> AllAdvancements.GLASS_PIPE.awardTo(context.getPlayer())));
            FluidTransportBehaviour.cacheFlows(world, pos);
            world.setBlockState(pos, ColoredBlocks.DYED_GLASS_PIPES.get(color).getDefaultState().with(GlassFluidPipeBlock.AXIS, axis).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED)));
            FluidTransportBehaviour.loadFlows(world, pos);
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    private Direction.Axis getAxis(BlockState state) {
        return FluidPropagator.getStraightPipeAxis(state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        ItemStack heldItem = player.getStackInHand(hand);
        DyeColor color = TagUtil.getColorFromStack(heldItem);
        if (color != null) {
            if (!world.isClient)
                world.playSound(null, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.1f - world.random.nextFloat() * .2f);
            applyDye(state, world, pos, color);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, ray);
    }

    public void applyDye(BlockState state, World world, BlockPos pos, @Nullable DyeColor color) {
        BlockState newState =
                (color == null ? ColoredBlocks.DYED_PIPES.get(DyeColor.WHITE) : ColoredBlocks.DYED_PIPES.get(color)).getDefaultState();

        //Update newState block state
        newState = updateBlockState(newState, Direction.UP, null, world, pos);

        //Dye the block itself
        if (state != newState) {
            world.setBlockState(pos, newState);
        }
    }

    @Override
    public BlockState updateBlockState(BlockState state, Direction preferredDirection, @Nullable Direction ignore, BlockRenderView world, BlockPos pos) {
        // Do nothing if we are bracketed
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (bracket != null && bracket.isBracketPresent())
            return state;

        // get and store initial state
        BlockState prevState = state;
        int prevStateSides = (int) Arrays.stream(Iterate.directions).map(FACING_PROPERTIES::get).filter(prevState::get).count();

        //Update pipe connections
        state = updateConnections(world, pos, state, ignore);

        // see if it has enough connections
        Direction connectedDirection = null;
        for (Direction d : Iterate.directions) {
            if (isOpenAt(state, d)) {
                if (connectedDirection != null) {
                    return state;
                }
                connectedDirection = d;
            }
        }

        // //add opposite end if only one connection
        if (connectedDirection != null) {
            return state.with(FACING_PROPERTIES.get(connectedDirection.getOpposite()), true);
        }
        // return pipe facing at the opposite of the direction of the previous
        // state
        if (prevStateSides == 2) {
            Direction foundDir = null;
            for (Direction d : Iterate.directions) {
                if (prevState.get(FACING_PROPERTIES.get(d))) {
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
