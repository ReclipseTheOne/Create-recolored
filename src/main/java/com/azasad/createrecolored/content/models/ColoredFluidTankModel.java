package com.azasad.createrecolored.content.models;

import com.azasad.createrecolored.RecoloredUtils;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.awt.image.renderable.RenderContext;
import java.util.Arrays;
import java.util.function.Supplier;

import static com.azasad.createrecolored.RecoloredUtils.*;

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
            culledFaces[HORIZONTAL_DIRECTIONS.indexOf(face)] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis()
                    .isVertical())
                return false;
            return culledFaces[HORIZONTAL_DIRECTIONS.indexOf(face)];
        }
    }
}
