package net.iqaddons.mod.features.kuudra.miscellaneous;

import net.iqaddons.mod.IQConstants;
import net.iqaddons.mod.config.categories.PhaseTwoConfig;
import net.iqaddons.mod.features.KuudraFeature;
import net.iqaddons.mod.model.kuudra.KuudraPhase;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class LuckyBuildFeature extends KuudraFeature {

    private static final Identifier LUCKY_BUILD_SOUND_END = Identifier.of("iq", "lucky_build_end");

    public LuckyBuildFeature() {
        super("luckyBuild", "Lucky Building",
                () -> PhaseTwoConfig.luckyBuild,
                KuudraPhase.BUILD
        );
    }

    @Override
    protected void onKuudraActivate() {
        mc.execute(() -> {
            if (mc.getSoundManager() == null) return;

            return;
        });

    }


    @Override
    protected void onKuudraDeactivate() {
        mc.execute(() -> {
            if (mc.getSoundManager() == null) return;

            return;
        });

    }
}
