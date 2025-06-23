package com.azasad.createrecolored;

import com.azasad.createrecolored.content.ColoredCreativeTabs;
import com.azasad.createrecolored.content.ColoredTags;
import com.azasad.createrecolored.content.block.ColoredBlocks;
import com.azasad.createrecolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreateReColored.MOD_ID)
public class CreateReColored {
	public static final String MOD_ID = "create-colored";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public CreateReColored(IEventBus modEventBus, ModContainer modContainer) {
		LOGGER.info("Registering create-colored blocks!");
		ColoredTags.initialize();
		ColoredBlockEntities.initialize();
		ColoredBlocks.initialize();

		modEventBus.register(ColoredCreativeTabs.CREATIVE_MODE_TABS);
		REGISTRATE.setModEventBus(modEventBus);
		REGISTRATE.registerEventListeners(modEventBus);
	}
}