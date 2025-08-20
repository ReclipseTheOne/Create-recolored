package com.azasad.createcolored.content.models;

import com.azasad.createcolored.RecoloredHelpers;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColoredFluidTankModel extends CTModel {

    // Define custom model properties
    public static final ModelProperty<BlockAndTintGetter> WORLD_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS_PROPERTY = new ModelProperty<>();

    public static ColoredFluidTankModel standard(BakedModel originalModel, DyeColor color) {
        return new ColoredFluidTankModel(originalModel,
                ColoredSpriteShifts.DYED_FLUID_TANK.get(color),
                ColoredSpriteShifts.DYED_FLUID_TANK_TOP.get(color),
                ColoredSpriteShifts.DYED_FLUID_TANK_INNER.get(color)
        );
    }

    private ColoredFluidTankModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top,
                                  CTSpriteShiftEntry inner) {
        super(originalModel, new FluidTankCTBehaviour(side, top, inner));
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand,
                                    @Nonnull ModelData extraData, @Nullable RenderType renderType) {
        // Get the base quads
        List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, extraData, renderType));

        // Apply culling based on connected tanks
        BlockAndTintGetter level = extraData.get(WORLD_PROPERTY);
        BlockPos pos = extraData.get(POS_PROPERTY);

        if (level != null && pos != null) {
            CullData cullData = new CullData();

            // Check connections on horizontal sides
            for (Direction d : RecoloredHelpers.HORIZONTAL_DIRECTIONS) {
                BlockPos adjacentPos = pos.relative(d);
                cullData.setCulled(d, ConnectivityHandler.isConnected(level, pos, adjacentPos));
            }

            // Filter quads based on culling
            List<BakedQuad> filteredQuads = new ArrayList<>();
            for (BakedQuad quad : quads) {
                Direction cullFace = quad.getDirection();
                if (cullFace == null || !cullData.isCulled(cullFace)) {
                    filteredQuads.add(quad);
                }
            }

            return filteredQuads;
        }

        return quads;
    }

    @Override
    @Nonnull
    public ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        return super.gatherModelData(builder, level, pos, state, modelData)
                .with(WORLD_PROPERTY, level)
                .with(POS_PROPERTY, pos);
    }

    private static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis().isVertical())
                return;
            culledFaces[RecoloredHelpers.getHorizontal(face)] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis().isVertical())
                return false;
            return culledFaces[RecoloredHelpers.getHorizontal(face)];
        }
    }
}