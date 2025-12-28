package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSoundEvents {
    public static final String MODID = VampirismTheMasqueradeMod.MODID;
    
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
        DeferredRegister.create(Registries.SOUND_EVENT, MODID);

     public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_SERVICE = registerSoundEvent("vampire_service");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_DEAD = registerSoundEvent("vampire_dead");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_REVIVED = registerSoundEvent("vampire_revived");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_WIND1 = registerSoundEvent("vampire_wind1");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_WIND2 = registerSoundEvent("vampire_wind2");
    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_STRONG = registerSoundEvent("heart_strong");
    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_WEAK = registerSoundEvent("heart_weak");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_F_ROAR = registerSoundEvent("vampire_f_roar");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_M_ROAR = registerSoundEvent("vampire_m_roar");

    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH1 = registerSoundEvent("village_day_high1");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH2 = registerSoundEvent("village_day_high2");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH3 = registerSoundEvent("village_day_high3");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH4 = registerSoundEvent("village_day_high4");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH5 = registerSoundEvent("village_day_high5");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH6 = registerSoundEvent("village_day_high6");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH7 = registerSoundEvent("village_day_high7");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_HIGH8 = registerSoundEvent("village_day_high8");

    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW1 = registerSoundEvent("village_day_low1");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW2 = registerSoundEvent("village_day_low2");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW3 = registerSoundEvent("village_day_low3");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW4 = registerSoundEvent("village_day_low4");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW5 = registerSoundEvent("village_day_low5");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW6 = registerSoundEvent("village_day_low6");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW7 = registerSoundEvent("village_day_low7");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_LOW8 = registerSoundEvent("village_day_low8");

    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE1 = registerSoundEvent("village_day_safe1");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE2 = registerSoundEvent("village_day_safe2");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE3 = registerSoundEvent("village_day_safe3");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE4 = registerSoundEvent("village_day_safe4");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE5 = registerSoundEvent("village_day_safe5");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE6 = registerSoundEvent("village_day_safe6");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE7 = registerSoundEvent("village_day_safe7");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE8 = registerSoundEvent("village_day_safe8");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE9 = registerSoundEvent("village_day_safe9");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE10 = registerSoundEvent("village_day_safe10");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE11 = registerSoundEvent("village_day_safe11");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE12 = registerSoundEvent("village_day_safe12");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE13 = registerSoundEvent("village_day_safe13");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE14 = registerSoundEvent("village_day_safe14");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE15 = registerSoundEvent("village_day_safe15");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE16 = registerSoundEvent("village_day_safe16");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE17 = registerSoundEvent("village_day_safe17");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE18 = registerSoundEvent("village_day_safe18");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE19 = registerSoundEvent("village_day_safe19");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE20 = registerSoundEvent("village_day_safe20");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE21 = registerSoundEvent("village_day_safe21");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE22 = registerSoundEvent("village_day_safe22");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_DAY_SAFE23 = registerSoundEvent("village_day_safe23");

    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH1 = registerSoundEvent("village_night_high1");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH2 = registerSoundEvent("village_night_high2");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH3 = registerSoundEvent("village_night_high3");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH4 = registerSoundEvent("village_night_high4");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH5 = registerSoundEvent("village_night_high5");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH6 = registerSoundEvent("village_night_high6");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH7 = registerSoundEvent("village_night_high7");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_HIGH8 = registerSoundEvent("village_night_high8");

    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_LOW1 = registerSoundEvent("village_night_low1");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_LOW2 = registerSoundEvent("village_night_low2");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_LOW3 = registerSoundEvent("village_night_low3");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGE_NIGHT_LOW4 = registerSoundEvent("village_night_low4");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}