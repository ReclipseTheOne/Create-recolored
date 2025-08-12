package com.azasad.createcolored;

import com.azasad.createcolored.content.ColoredTags;
import com.azasad.createcolored.content.block.ColoredBlocks;
import com.azasad.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreateRecolored.MOD_ID)
public class CreateRecolored {
	public static final String MOD_ID = "create-colored";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public CreateRecolored(IEventBus bus, ModContainer container) {
		LOGGER.info("Registering create-colored blocks!");
		ColoredTags.initialize();
		ColoredBlockEntities.initialize();
		ColoredBlocks.initialize();
		REGISTRATE.registerEventListeners(bus);
	}
}