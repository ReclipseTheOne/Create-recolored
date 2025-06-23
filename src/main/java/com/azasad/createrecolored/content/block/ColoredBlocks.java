package com.azasad.createrecolored.content.block;

import com.azasad.createrecolored.ColoredHelpers;
import com.azasad.createrecolored.ColoredRegistrate;
import com.azasad.createrecolored.CreateReColored;
import com.azasad.createrecolored.content.ColoredTags;
import com.azasad.createrecolored.content.item.ColoredFluidTankItem;
import com.azasad.createrecolored.content.models.ColoredFluidTankModel;
import com.azasad.createrecolored.content.models.ColoredPipeAttachmentModel;
import com.azasad.createrecolored.datagen.ColoredBlockStateGen;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ColoredBlocks {
    private static final CreateRegistrate REGISTRATE = CreateReColored.REGISTRATE;

    public static final DyedBlockList<ColoredFluidTankBlock> DYED_FLUID_TANKS = new DyedBlockList<>(dyecolor -> {
       String colorName = dyecolor.getName();
       return REGISTRATE.block(colorName + "_fluid_tank", settings -> new ColoredFluidTankBlock(settings, dyecolor))
               .initialProperties(SharedProperties::copperMetal)
               .properties(BlockBehaviour.Properties::noOcclusion)
               .transform(pickaxeOnly())
               .blockstate(ColoredBlockStateGen.coloredTank(dyecolor))
               .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredFluidTankModel::standard, dyecolor))
               .item(ColoredFluidTankItem::new)
               .lang(colorName + " Fluid Tank")
               .model((c,p) -> {
                   p.withExistingParent(c.getName(), Create.asResource("item/fluid_tank"))
                           .texture("0", p.modLoc("block/fluid_tank_top/" + colorName))
                           .texture("1", p.modLoc("block/fluid_tank/" + colorName))
                           .texture("3", p.modLoc("block/fluid_tank_window/" + colorName))
                           .texture("4", p.modLoc("block/fluid_tank_inner/" + colorName))
                           .texture("5", p.modLoc("block/fluid_tank_window_single/" + colorName));
               })
               .recipe((c,p) -> {
                   ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                           .requires(ColoredHelpers.getDyeItem(dyecolor))
                           .requires(AllBlocks.FLUID_TANK.asItem())
                           .unlockedBy("has_tank", InventoryChangeTrigger.TriggerInstance.hasItems(AllBlocks.FLUID_TANK));

                   builder.save(p, c.getId());
               })
               .tag(ColoredTags.ColoredItemTags.COLORED_TANKS.tag)
               .build()
               .register();
    });

    public static final DyedBlockList<ColoredFluidPipeBlock> DYED_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return REGISTRATE.block(colorName + "_fluid_pipe", settings -> new ColoredFluidPipeBlock(settings, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(p -> p.mapColor(dyeColor.getMapColor()))
                .transform(pickaxeOnly())
                .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::new, dyeColor))
                .blockstate(ColoredBlockStateGen.coloredPipe(dyeColor))
                .item()
                .model((c, p) -> p.withExistingParent(c.getName(), Create.asResource("item/fluid_pipe")).texture("1", "block/pipes/" + colorName))
                .recipe((c, p) -> {
                    ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                            .requires(ColoredHelpers.getDyeItem(dyeColor))
                            .requires(AllBlocks.FLUID_PIPE.asItem())
                            .unlockedBy("has_pipe", InventoryChangeTrigger.TriggerInstance.hasItems(AllBlocks.FLUID_PIPE));

                    builder.save(p, c.getId());
                })
                .tag(ColoredTags.ColoredItemTags.COLORED_PIPES.tag)
                .build()
                .register();
    });

    public static final DyedBlockList<ColoredGlassFluidPipeBlock> DYED_GLASS_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return REGISTRATE.block(colorName + "_glass_fluid_pipe", settings -> new ColoredGlassFluidPipeBlock(settings, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .transform(pickaxeOnly())
                .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
                        .forAllStatesExcept(state -> {
                            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                            return ConfiguredModel.builder()
                                    .modelFile(p.models().
                                            withExistingParent("block/colored_fluid_pipe/" + colorName + "_fluid_pipe/window", Create.asResource("block/fluid_pipe/window"))
                                            .texture("0", "block/glass_fluid_pipe/" + colorName))
                                    .uvLock(false)
                                    .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                                    .build();
                        }, BlockStateProperties.WATERLOGGED))
                .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::new, dyeColor))
                .register();
    });

    public static final DyedBlockList<ColoredEncasedPipeBlock> DYED_ENCASED_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return REGISTRATE.block(colorName + "_encased_fluid_pipe", p -> new ColoredEncasedPipeBlock(p, AllBlocks.COPPER_CASING::get, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY))
                .transform(axeOrPickaxe())
                .blockstate(ColoredBlockStateGen.encasedPipe(dyeColor))
                .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.COPPER_CASING,
                        (s, f) -> !s.getValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
                .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::new, dyeColor))
                .transform(EncasingRegistry.addVariantTo(DYED_PIPES.get(dyeColor)))
                .register();
    });

    public static void initialize() {

    }
}

