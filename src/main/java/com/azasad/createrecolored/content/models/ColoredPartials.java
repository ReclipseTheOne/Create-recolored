package com.azasad.createrecolored.content.models;

import com.azasad.createrecolored.CreateReColored;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ColoredPartials {

    //Create casings map
    public static final Map<DyeColor, PartialModel> COLORED_FLUID_PIPE_CASINGS = new EnumMap<>(DyeColor.class);
    //Create attachment map
    public static final Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<DyeColor, Map<String, PartialModel>>> COLORED_PIPE_ATTACHMENTS = new EnumMap<>(
            FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            COLORED_FLUID_PIPE_CASINGS.put(color, block("colored_fluid_pipe/" + color.getName() + "_fluid_pipe/casing"));
        }
    }

    // populate models
    static {
        for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials type : FluidTransportBehaviour.AttachmentTypes.ComponentPartials
                .values()) {
            Map<DyeColor, Map<String, PartialModel>> colorMap = new EnumMap<>(DyeColor.class);
            for (DyeColor color : DyeColor.values()) {
                Map<String, PartialModel> map = new HashMap<>();
                for (Direction d : Direction.values()) {
                    String asId = type.name();
                    map.put(d.toString(), block("colored_fluid_pipe/" + color.getName() + "_fluid_pipe/" + asId + "/" + d.toString()));
                }
                colorMap.put(color, map);
            }
            COLORED_PIPE_ATTACHMENTS.put(type, colorMap);
        }
    }

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateReColored.MOD_ID, "block/" + path));
    }

    public static void initialize() {
    }
}
