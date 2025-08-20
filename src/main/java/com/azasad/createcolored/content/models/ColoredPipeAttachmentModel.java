package com.azasad.createcolored.content.models;

import com.azasad.createcolored.content.block.ColoredFluidPipeBlock;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColoredPipeAttachmentModel extends BakedModelWrapper<BakedModel> {
    private final DyeColor color;

    // Define model properties
    public static final ModelProperty<ColoredPipeModelData> PIPE_DATA = new ModelProperty<>();
    public static final ModelProperty<BlockAndTintGetter> WORLD_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS_PROPERTY = new ModelProperty<>();

    public ColoredPipeAttachmentModel(BakedModel template, DyeColor color) {
        super(template);
        this.color = color;
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand,
                                    @Nonnull ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, extraData, renderType));

        // Get the model data from extraData
        ColoredPipeModelData data = extraData.get(PIPE_DATA);
        if (data == null) {
            // If no data is present, create it from the world
            BlockAndTintGetter level = extraData.get(WORLD_PROPERTY);
            BlockPos pos = extraData.get(POS_PROPERTY);

            if (level != null && pos != null) {
                data = new ColoredPipeModelData();

                // Populate attachment list
                FluidTransportBehaviour transport = BlockEntityBehaviour.get(level, pos, FluidTransportBehaviour.TYPE);
                if (transport != null) {
                    for (Direction d : Direction.values()) {
                        FluidTransportBehaviour.AttachmentTypes attachment = transport.getRenderedRimAttachment(level, pos, state, d);
                        data.putAttachment(d, attachment);
                    }
                }

                // Bracket logic
                BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(level, pos, BracketedBlockEntityBehaviour.TYPE);
                if (bracket != null) {
                    data.putBracket(bracket.getBracket());
                }

                data.setEncased(state != null && ColoredFluidPipeBlock.shouldDrawCasing(state));
            } else {
                return quads;
            }
        }

        // Add attachment quads
        for (Direction d : Direction.values()) {
            FluidTransportBehaviour.AttachmentTypes type = data.getAttachment(d);
            if (type != null && type.partials != null) {
                for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : type.partials) {
                    if (ColoredPartials.COLORED_PIPE_ATTACHMENTS.containsKey(partial)) {
                        var colorMap = ColoredPartials.COLORED_PIPE_ATTACHMENTS.get(partial);
                        if (colorMap.containsKey(color)) {
                            var directionMap = colorMap.get(color);
                            if (directionMap.containsKey(d.getName())) {
                                BakedModel partialModel = directionMap.get(d.getName()).get();
                                if (partialModel != null) {
                                    quads.addAll(partialModel.getQuads(state, side, rand, extraData, renderType));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add casing quads if needed
        if (data.isEncased() && ColoredPartials.COLORED_FLUID_PIPE_CASINGS.containsKey(color)) {
            BakedModel casingModel = ColoredPartials.COLORED_FLUID_PIPE_CASINGS.get(color).get();
            if (casingModel != null) {
                quads.addAll(casingModel.getQuads(state, side, rand, extraData, renderType));
            }
        }

        // Add bracket quads
        BakedModel bracketModel = data.getBracket();
        if (bracketModel != null) {
            quads.addAll(bracketModel.getQuads(state, side, rand, extraData, renderType));
        }

        return quads;
    }

    @Override
    @Nonnull
    public ModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData modelData) {
        ColoredPipeModelData data = new ColoredPipeModelData();

        // Populate attachment list
        FluidTransportBehaviour transport = BlockEntityBehaviour.get(level, pos, FluidTransportBehaviour.TYPE);
        if (transport != null) {
            for (Direction d : Direction.values()) {
                FluidTransportBehaviour.AttachmentTypes attachment = transport.getRenderedRimAttachment(level, pos, state, d);
                data.putAttachment(d, attachment);
            }
        }

        // Bracket logic
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(level, pos, BracketedBlockEntityBehaviour.TYPE);
        if (bracket != null && bracket.getBracket() != null) {
            data.putBracket(bracket.getBracket());
        }

        data.setEncased(ColoredFluidPipeBlock.shouldDrawCasing(state));

        return modelData.derive()
                .with(PIPE_DATA, data)
                .with(WORLD_PROPERTY, level)
                .with(POS_PROPERTY, pos)
                .build();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }

    public static class ColoredPipeModelData {
        private final FluidTransportBehaviour.AttachmentTypes[] attachments;
        private boolean encased;
        private BakedModel bracket;

        public ColoredPipeModelData() {
            attachments = new FluidTransportBehaviour.AttachmentTypes[6];
            Arrays.fill(attachments, FluidTransportBehaviour.AttachmentTypes.NONE);
        }

        public void putBracket(BlockState state) {
            if (state != null) {
                this.bracket = Minecraft.getInstance()
                        .getBlockRenderer()
                        .getBlockModel(state);
            }
        }

        public BakedModel getBracket() {
            return this.bracket;
        }

        public void putAttachment(Direction face, FluidTransportBehaviour.AttachmentTypes rim) {
            if (rim != null) {
                attachments[face.get3DDataValue()] = rim;
            }
        }

        public FluidTransportBehaviour.AttachmentTypes getAttachment(Direction face) {
            return attachments[face.get3DDataValue()];
        }

        public boolean isEncased() {
            return this.encased;
        }

        public void setEncased(boolean encased) {
            this.encased = encased;
        }
    }
}