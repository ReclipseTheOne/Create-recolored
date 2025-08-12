package com.azasad.createcolored.content.block;

import com.azasad.createcolored.RecoloredHelpers;
import com.azasad.createcolored.RecoloredRegistrate;
import com.azasad.createcolored.CreateRecolored;
import com.azasad.createcolored.content.ColoredTags;
import com.azasad.createcolored.content.item.ColoredFluidTankItem;
import com.azasad.createcolored.content.models.ColoredFluidTankModel;
import com.azasad.createcolored.content.models.ColoredPipeAttachmentModel;
import com.azasad.createcolored.datagen.ColoredBlockStateGen;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.redstone.displayLink.source.BoilerDisplaySource;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import io.github.fabricators_of_create.porting_lib.models.generators.ConfiguredModel;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.core.Direction;

import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ColoredBlocks {
    private static final CreateRegistrate REGISTRATE = CreateRecolored.REGISTRATE;

    public static final DyedBlockList<ColoredFluidTankBlock> DYED_FLUID_TANKS = new DyedBlockList<>(dyecolor -> {
       String colorName = dyecolor.getName();
       return REGISTRATE.block(colorName + "_fluid_tank", settings -> new ColoredFluidTankBlock(settings, dyecolor))
               .initialProperties(SharedProperties::copperMetal)
               .properties(p -> p.nonOpaque().solidBlock((p1, p2, p3) -> true))
               .transform(pickaxeOnly())
               .blockstate(ColoredBlockStateGen.coloredTank(dyecolor))
               .onRegister(RecoloredRegistrate.coloredBlockModel(() -> ColoredFluidTankModel::standard, dyecolor))
               .onRegister(assignDataBehaviour(new BoilerDisplaySource(), "boiler_status"))
               .addLayer(() -> RenderLayer::getCutoutMipped)
               .item(ColoredFluidTankItem::new)
//               .lang(colorName + " Fluid Tank")
               .model((c,p) -> {
                   p.withExistingParent(c.getName(), Create.asResource("item/fluid_tank"))
                           .texture("0", p.modLoc("block/fluid_tank_top/" + colorName))
                           .texture("1", p.modLoc("block/fluid_tank/" + colorName))
                           .texture("3", p.modLoc("block/fluid_tank_window/" + colorName))
                           .texture("4", p.modLoc("block/fluid_tank_inner/" + colorName))
                           .texture("5", p.modLoc("block/fluid_tank_window_single/" + colorName));
               })
               .recipe((c,p) -> {
                   ShapelessRecipeJsonBuilder builder = ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                           .input(RecoloredHelpers.getDyeItem(dyecolor))
                           .input(AllBlocks.FLUID_TANK.asItem())
                           .criterion("has_tank", InventoryChangedCriterion.Conditions.items(AllBlocks.FLUID_TANK));

                   builder.offerTo(p, CreateRecolored.rl(c.getName()));
               })
               .tag(ColoredTags.ColoredItemTags.COLORED_TANKS.tag)
               .build()
               .register();
    });

    public static final DyedBlockList<ColoredFluidPipeBlock> DYED_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return REGISTRATE.block(colorName + "_fluid_pipe", settings -> new ColoredFluidPipeBlock(settings, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(p -> p.mapColor(dyeColor.getMapColor()).solid())
                .transform(pickaxeOnly())
                .onRegister(RecoloredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::new, dyeColor))
                .blockstate(ColoredBlockStateGen.coloredPipe(dyeColor))
                .item()
                .model((c, p) -> p.withExistingParent(c.getName(), Create.asResource("item/fluid_pipe")).texture("1", "block/pipes/" + colorName))
                .recipe((c, p) -> {
                    ShapelessRecipeJsonBuilder builder = ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                            .input(RecoloredHelpers.getDyeItem(dyeColor))
                            .input(AllBlocks.FLUID_PIPE.asItem())
                            .criterion("has_pipe", InventoryChangedCriterion.Conditions.items(AllBlocks.FLUID_PIPE));

                    builder.offerTo(p, CreateRecolored.rl(c.getName()));
                })
                .tag(ColoredTags.ColoredItemTags.COLORED_PIPES.tag)
                .build()
                .register();
    });

    public static final DyedBlockList<ColoredGlassFluidPipeBlock> DYED_GLASS_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return REGISTRATE.block(colorName + "_glass_fluid_pipe", settings -> new ColoredGlassFluidPipeBlock(settings, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(AbstractBlock.Settings::solid)
                .addLayer(() -> RenderLayer::getCutoutMipped)
                .transform(pickaxeOnly())
                .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
                        .forAllStatesExcept(state -> {
                            Direction.Axis axis = state.getValue(Properties.AXIS);
                            return ConfiguredModel.builder()
                                    .modelFile(p.models().
                                            withExistingParent("block/colored_fluid_pipe/" + colorName + "_fluid_pipe/window", Create.asResource("block/fluid_pipe/window"))
                                            .texture("0", "block/glass_fluid_pipe/" + colorName))
                                    .uvLock(false)
                                    .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                                    .build();
                        }, BlockStateProperties.WATERLOGGED))
                .onRegister(RecoloredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::new, dyeColor))
                .loot((p, b) -> p.addDrop(b, DYED_PIPES.get(dyeColor).get()))
                .register();
    });

    public static final DyedBlockList<ColoredEncasedPipeBlock> DYED_ENCASED_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return REGISTRATE.block(colorName + "_encased_fluid_pipe", p -> new ColoredEncasedPipeBlock(p, AllBlocks.COPPER_CASING::get, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(p -> p.nonOpaque().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY))
                .transform(axeOrPickaxe())
                .blockstate(ColoredBlockStateGen.encasedPipe(dyeColor))
                .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.COPPER_CASING,
                        (s, f) -> !s.get(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
                .onRegister(RecoloredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::new, dyeColor))
                .loot((p, b) -> p.addDrop(b, DYED_PIPES.get(dyeColor).get()))
                .transform(EncasingRegistry.addVariantTo(DYED_PIPES.get(dyeColor)))
                .register();
    });

    public static void initialize() {

    }
}

