package com.azasad.createrecolored.content.models;

import com.azasad.createrecolored.CreateReColored;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.Map;

public class ColoredSpriteShifts {
    public static final Map<DyeColor, CTSpriteShiftEntry> DYED_FLUID_TANK = new EnumMap<>(DyeColor.class),
            DYED_FLUID_TANK_TOP = new EnumMap<>(DyeColor.class),
            DYED_FLUID_TANK_INNER = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            CTSpriteShiftEntry fluidTank = getColoredCT(AllCTTypes.RECTANGLE, "fluid_tank", color);
            CTSpriteShiftEntry fluidTankTop = getColoredCT(AllCTTypes.RECTANGLE, "fluid_tank_top", color);
            CTSpriteShiftEntry fluidTankInner = getColoredCT(AllCTTypes.RECTANGLE, "fluid_tank_inner", color);

            DYED_FLUID_TANK.put(color, fluidTank);
            DYED_FLUID_TANK_TOP.put(color, fluidTankTop);
            DYED_FLUID_TANK_INNER.put(color, fluidTankInner);
        }
    }

    private static CTSpriteShiftEntry getColoredCT(CTType type, String blockTextureName, DyeColor color) {
        String basePath = "block/" + blockTextureName;
        String originalTexturePath = basePath + "/" + color.getName();
        String connectedTexturePath = basePath + "_connected/" + color.getName();
        return CTSpriteShifter.getCT(type, CreateReColored.asResource(originalTexturePath),
                CreateReColored.asResource(connectedTexturePath));
    }

    public static void initialize() {

    }
}
