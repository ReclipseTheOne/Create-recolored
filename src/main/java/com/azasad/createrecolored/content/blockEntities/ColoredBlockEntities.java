package com.azasad.createrecolored.content.blockEntities;

import com.azasad.createrecolored.CreateReColored;
import com.azasad.createrecolored.content.block.ColoredBlocks;
import com.simibubi.create.content.fluids.pipes.TransparentStraightPipeRenderer;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ColoredBlockEntities {
    private static final CreateRegistrate REGISTRATE = CreateReColored.REGISTRATE;

    public static final BlockEntityEntry<ColoredFluidPipeBlockEntity> COLORED_FLUID_PIPE_ENTITY = REGISTRATE
            .blockEntity("colored_fluid_pipe", ColoredFluidPipeBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_PIPES.toArray())
            .register();

    public static final BlockEntityEntry<ColoredFluidTankBlockEntity> COLORED_FLUID_TANK_ENTITY = REGISTRATE
            .blockEntity("fluid_tank", ColoredFluidTankBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_FLUID_TANKS.toArray())
            .renderer(() -> FluidTankRenderer::new)
            .register();

    public static final BlockEntityEntry<ColoredFluidPipeBlockEntity> COLORED_ENCASED_FLUID_PIPE = REGISTRATE
            .blockEntity("colored_encased_fluid_pipe", ColoredFluidPipeBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_ENCASED_PIPES.toArray())
            .register();

    public static final BlockEntityEntry<ColoredGlassFluidPipeBlockEntity> COLORED_GLASS_FLUID_PIPE_ENTITY = REGISTRATE
            .blockEntity("colored_glass_fluid_pipe", ColoredGlassFluidPipeBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_GLASS_PIPES.toArray())
            .renderer(() -> TransparentStraightPipeRenderer::new)
            .register();

    public static void initialize() {

    }
}
