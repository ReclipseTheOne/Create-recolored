package com.azasad.createcolored;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ColoredRegistrate extends CreateRegistrate {
    //I really feel like this is suboptimal code, but I don't know enough about java to make it optimal
    protected ColoredRegistrate(String modid) {
        super(modid);
    }

    public static <T extends Block> NonNullConsumer<? super T> coloredBlockModel(
            Supplier<NonNullBiFunction<BakedModel, DyeColor, ? extends BakedModel>> func, DyeColor color) {
        return entry -> onClient(() -> () -> registerColoredBlockModel(entry, func, color));
    }

    @OnlyIn(Dist.CLIENT)
    private static void registerColoredBlockModel(Block entry,
                                                  Supplier<NonNullBiFunction<BakedModel, DyeColor, ? extends BakedModel>> func, DyeColor color) {
        CreateClient.MODEL_SWAPPER.getCustomBlockModels()
                .register(RegisteredObjects.getKeyOrThrow(entry), model -> func.get().apply(model, color));
    }
}
