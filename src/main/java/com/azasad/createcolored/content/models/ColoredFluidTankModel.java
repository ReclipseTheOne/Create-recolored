package com.azasad.createcolored.content.models;

import com.azasad.createcolored.RecoloredHelpers;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class ColoredFluidTankModel extends CTModel {

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
    public void emitBlockQuads(Level Level, BlockState state, BlockPos pos, RandomSource randomSupplier, RenderContext context) {
        CullData cullData = new CullData();
        for (Direction d : Arrays.asList(RecoloredHelpers.HORIZONTAL_DIRECTIONS))
            cullData.setCulled(d, ConnectivityHandler.isConnected(Level, pos, pos.offset(d)));

        context.pushTransform(quad -> {
            Direction cullFace = quad.cullFace();
            if (cullFace != null && cullData.isCulled(cullFace)) {
                return false;
            }
            quad.cullFace(null);
            return true;
        });

        super.emitBlockQuads(Level, state, pos, randomSupplier, context);

        context.popTransform();
    }

    private static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis()
                    .isVertical())
                return;
            culledFaces[face.getHorizontal()] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis()
                    .isVertical())
                return false;
            return culledFaces[face.getHorizontal()];
        }
    }
}
