package com.azasad.createrecolored.content;

import com.azasad.createrecolored.CreateReColored;
import com.azasad.createrecolored.content.block.ColoredBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ColoredCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateReColored.MOD_ID);

    public static final Supplier<CreativeModeTab> RECOLORED_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
                        .icon(() -> new ItemStack(ColoredBlocks.DYED_PIPES.get(DyeColor.ORANGE)))
                        .title(Component.translatable(("itemGroup.create-colored.creative_tab")))
                        .displayItems((context, entries) -> {
                            for (DyeColor color : DyeColor.values()) {
                                entries.accept(ColoredBlocks.DYED_PIPES.get(color).asStack());
                            }

                            for (DyeColor color : DyeColor.values()){
                                entries.accept(ColoredBlocks.DYED_FLUID_TANKS.get(color));
                            }
                        })
                        .build());
}


