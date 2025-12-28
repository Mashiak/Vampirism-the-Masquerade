package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.resources.ResourceLocation; 

import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData; 
import net.daanlokdrog.vampirismthemasquerade.MasqueradeDomainData;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;

public class VampireSpawnModifier {

    private static final ResourceLocation HABITABILITY_HEALTH_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "habitability_health_bonus");

    private static final ResourceLocation GUARD_ARMOR_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "guard_armor_bonus");

    private static final ResourceLocation GUARD_TOUGHNESS_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "guard_toughness_bonus");

    private static final ResourceLocation GUARD_ATTACK_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "guard_attack_bonus");

    public static void applyHabitabilityBonus(LivingEntity entity) {
        if (entity.level().isClientSide() || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = entity.blockPosition();
        AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armorAttribute = entity.getAttribute(Attributes.ARMOR);
        AttributeInstance toughnessAttribute = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance attackAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (healthAttribute == null || armorAttribute == null || toughnessAttribute == null || attackAttribute == null) {
            return;
        }

        healthAttribute.removeModifier(HABITABILITY_HEALTH_MODIFIER_ID);
        armorAttribute.removeModifier(GUARD_ARMOR_MODIFIER_ID);
        toughnessAttribute.removeModifier(GUARD_TOUGHNESS_MODIFIER_ID);
        attackAttribute.removeModifier(GUARD_ATTACK_MODIFIER_ID);
        MasqueradeVillageData villageData = MasqueradeVillageData.get(serverLevel);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(serverLevel);
        ChunkPos currentChunk = new ChunkPos(pos);
        ChunkPos villageCenterKey = villageData.getCenterKeyForChunk(currentChunk);

        if (villageCenterKey == null || !domainData.isDomainClaimed(villageCenterKey)) {
            return;
        }

        double effectiveHabitability = domainData.getEffectiveHabitability(serverLevel, villageCenterKey);
        double maxHealthBonus = MasqueradeConfigConfiguration.MAXIMUM_HEALTH_BONUS.get();
        double healthBonusValue = Math.min(effectiveHabitability, maxHealthBonus);

        if (healthBonusValue >= 0.001) {
            AttributeModifier healthModifier = new AttributeModifier(
                HABITABILITY_HEALTH_MODIFIER_ID, 
                healthBonusValue,                      
                AttributeModifier.Operation.ADD_VALUE 
            );
            healthAttribute.addPermanentModifier(healthModifier);
        }

        double maxArmorBonus = MasqueradeConfigConfiguration.MAXIMUM_ARMOR_BONUS.get();
        int rawGuardArmorValue = domainData.getGuardArmorValue(villageCenterKey);
        double armorBonusValue = Math.min((double) rawGuardArmorValue, maxArmorBonus);
        
        if (armorBonusValue >= 0.001) {
            AttributeModifier armorModifier = new AttributeModifier(
                GUARD_ARMOR_MODIFIER_ID, 
                armorBonusValue, 
                AttributeModifier.Operation.ADD_VALUE
            );
            armorAttribute.addPermanentModifier(armorModifier);

            AttributeModifier toughnessModifier = new AttributeModifier(
                GUARD_TOUGHNESS_MODIFIER_ID, 
                armorBonusValue, 
                AttributeModifier.Operation.ADD_VALUE
            );
            toughnessAttribute.addPermanentModifier(toughnessModifier);
        }

        double maxDamageBonus = MasqueradeConfigConfiguration.MAXIMUM_DAMAGE_BONUS.get();
        int rawGuardWeaponValue = domainData.getGuardWeaponValue(villageCenterKey);
        double attackBonusValue = Math.min((double) rawGuardWeaponValue, maxDamageBonus);
        
        if (attackBonusValue >= 0.001) {
            AttributeModifier attackModifier = new AttributeModifier(
                GUARD_ATTACK_MODIFIER_ID, 
                attackBonusValue, 
                AttributeModifier.Operation.ADD_VALUE
            );
            attackAttribute.addPermanentModifier(attackModifier);
        }
    }
}