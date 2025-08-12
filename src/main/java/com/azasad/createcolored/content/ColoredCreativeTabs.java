package com.azasad.createcolored.content;

import com.azasad.createcolored.CreateRecolored;
import com.azasad.createcolored.content.block.ColoredBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.registries.DeferredRegister;

public class ColoredCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> HONTABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateRecolored.MOD_ID);

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, COLORED_CREATIVE_TAB,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ColoredBlocks.DYED_PIPES.get(DyeColor.ORANGE)))
                        .displayName(Text.translatable(("itemGroup.create-colored.creative_tab")))
                        .entries((context, entries) -> {
                            for (DyeColor color : DyeColor.values()) {
                                entries.add(ColoredBlocks.DYED_PIPES.get(color).asStack());
                            }

                            for (DyeColor color : DyeColor.values()){
                                entries.add(ColoredBlocks.DYED_FLUID_TANKS.get(color));
                            }
                        })
                        .build());
    }
}


