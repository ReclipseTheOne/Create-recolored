package com.azasad.createcolored;

import com.azasad.createcolored.content.ColoredCreativeTabs;
import com.azasad.createcolored.content.models.ColoredPartials;
import com.azasad.createcolored.content.models.ColoredSpriteShifts;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateRecolored.MOD_ID, value = Dist.CLIENT)
public class CreateRecoloredClient {
    public CreateRecoloredClient() {
        ColoredCreativeTabs.initialize();
        ColoredSpriteShifts.initialize();
        ColoredPartials.initialize();
    }
}
