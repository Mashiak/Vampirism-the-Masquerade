package net.daanlokdrog.vampirismthemasquerade.capability;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
/*
 * abandoned
 */

public class ThrallAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VampirismTheMasqueradeMod.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> THRALL =
            ATTACHMENTS.register("thrall",
                () -> AttachmentType.builder(holder -> Boolean.FALSE)
                        .serialize(Codec.BOOL)
                        .build());

    public static void setThrall(Villager villager, boolean value) {
        villager.getPersistentData().putBoolean("VTM_IsThrall", value);

        villager.setData(THRALL.get(), value);
    }

    public static boolean isThrall(Villager villager) {
        return villager.getData(THRALL.get());
    }
}
