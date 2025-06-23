package com.azasad.createrecolored;

import com.azasad.createrecolored.content.ColoredCreativeTabs;
import com.azasad.createrecolored.content.models.ColoredPartials;
import com.azasad.createrecolored.content.models.ColoredSpriteShifts;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = CreateReColored.MOD_ID, dist = Dist.CLIENT)
public class CreateReColoredClient {
    public CreateReColoredClient(IEventBus eventBus, ModContainer modContainer) {
        ColoredSpriteShifts.initialize();
        ColoredPartials.initialize();
    }
}
