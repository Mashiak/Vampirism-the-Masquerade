package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.util.VampireVillagerUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DialogueProviderRegistry {

    private static final Map<String, Supplier<IDialogueProvider>> providers = new HashMap<>();
    private static final Supplier<IDialogueProvider> BASIC_VILLAGER_PROVIDER_SUPPLIER = BasicVillagerDialogueProvider::new;
    private static final Supplier<IDialogueProvider> ADVISOR_PROVIDER_SUPPLIER = AdvisorDialogueProvider::new;

    public static void registerAll() {
        registerProfession("minecraft:farmer", FarmerDialogueProvider::new);
        registerProfession("minecraft:cleric", PriestDialogueProvider::new);
        registerProfession("vampirism:priest", PriestDialogueProvider::new);
        registerProfession("minecraft:armorer", ArmorerDialogueProvider::new);
        registerProfession("minecraft:weaponsmith", WeaponsmithDialogueProvider::new);
        registerProfession("vampirism:vampire_expert", ExpertDialogueProvider::new);
        registerProfession("vampirism:hunter_expert", HunterExpertDialogueProvider::new);
        registerProfession("minecraft:none", BASIC_VILLAGER_PROVIDER_SUPPLIER);
        registerProfession("minecraft:nitwit", BASIC_VILLAGER_PROVIDER_SUPPLIER);

        registerEntityType("vampirism:advanced_vampire", ADVISOR_PROVIDER_SUPPLIER);
    }

    public static void registerProfession(String professionId, Supplier<IDialogueProvider> providerSupplier) {
        providers.put("Profession:" + professionId, providerSupplier);
    }

    public static void registerEntityType(String entityTypeId, Supplier<IDialogueProvider> providerSupplier) {
        providers.put("EntityType:" + entityTypeId, providerSupplier);
    }

    public static IDialogueProvider getProvider(Entity entity) {
        String lookupKey = getLookupKey(entity);

        if (providers.containsKey(lookupKey)) {
            return providers.get(lookupKey).get();
        }

        if (VampireVillagerUtil.isVampireVillager(entity)) {
            return BASIC_VILLAGER_PROVIDER_SUPPLIER.get();
        }

        return BASIC_VILLAGER_PROVIDER_SUPPLIER.get();
    }

    private static String getLookupKey(Entity entity) {
        if (entity instanceof AdvancedVampireEntity) {
            ResourceLocation entityTypeId = entity.getType().builtInRegistryHolder().key().location();
            return "EntityType:" + entityTypeId.toString();
        }

        if (VampireVillagerUtil.isVampireVillager(entity) && entity instanceof Villager villager) {
            return "Profession:" + getProfessionId(villager);
        }

        ResourceLocation entityTypeId = entity.getType().builtInRegistryHolder().key().location();
        return "EntityType:" + entityTypeId.toString();
    }

    private static String getProfessionId(Villager villager) {
        try {
            VillagerProfession profession = villager.getVillagerData().getProfession();
            ResourceLocation professionRL = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
            if (professionRL != null) {
                return professionRL.toString();
            }
            return "minecraft:none";
        } catch (Exception e) {
            VampirismTheMasqueradeMod.LOGGER.error("Failed to retrieve profession ID from Villager: {}", e.getMessage());
            return "minecraft:none";
        }
    }

    public static String getStartPageId(Entity entity) {
        IDialogueProvider provider = getProvider(entity);
        return provider != null ? provider.getStartPageId() : "MAIN";
    }

    public static DialoguePage getPage(Entity entity, String pageId) {
        IDialogueProvider provider = getProvider(entity);

        if (provider == null) {
            return BASIC_VILLAGER_PROVIDER_SUPPLIER.get().getDialoguePages().get("MAIN");
        }

        DialoguePage page = provider.getDialoguePages().get(pageId);

        if (page == null) {
            IDialogueProvider basicProvider = BASIC_VILLAGER_PROVIDER_SUPPLIER.get();
            return basicProvider.getDialoguePages().get(basicProvider.getStartPageId());
        }
        return page;
    }

    static {
        registerAll();
    }
}
