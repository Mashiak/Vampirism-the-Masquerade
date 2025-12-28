package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player; 
import net.minecraft.server.level.ServerLevel; 
import net.minecraft.core.BlockPos; 
import net.minecraft.world.level.ChunkPos; 
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;

import net.daanlokdrog.vampirismthemasquerade.util.VampireVillagerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicVillagerDialogueProvider implements IDialogueProvider {

    private final Map<String, DialoguePage> pages = new HashMap<>();
    private final String START_PAGE_ID = "MAIN";

    public BasicVillagerDialogueProvider() {
        DialogueOption backToMain = new DialogueOption(Component.translatable("dialogue.vtm.back_to_main"), 0, "MAIN");

        pages.put("MAIN", new DialoguePage(
            Component.translatable("dialogue.vtm.main_npc_greeting"), 
            List.of(
                new DialogueOption(Component.translatable("dialogue.vtm.q1_mortal_awareness"), 1, "SERVER_CHECK"), 
                new DialogueOption(Component.translatable("dialogue.vtm.q2_how_to_hide"), 2, "SERVER_CHECK"),     
                new DialogueOption(Component.translatable("dialogue.vtm.q3_things_to_note"), 3, "SERVER_CHECK"),  
                new DialogueOption(Component.translatable("dialogue.vtm.q4_exit_dialogue"), 4, "EXIT")    
            )
        ));
        
        pages.put("Q1_LOW", new DialoguePage(
            Component.translatable("dialogue.basic.q1a.low"),
            List.of(backToMain)
        ));

        pages.put("Q1_MID", new DialoguePage(
            Component.translatable("dialogue.basic.q1a.mid"),
            List.of(backToMain)
        ));

        pages.put("Q1_HIGH", new DialoguePage(
            Component.translatable("dialogue.basic.q1a.high"),
            List.of(backToMain)
        ));

        pages.put("Q2_STATIC", new DialoguePage(
            Component.translatable("dialogue.vtm.q2_response"), 
            List.of(backToMain)
        ));

        pages.put("Q3_STATIC", new DialoguePage(
            Component.translatable("dialogue.vtm.q3_response"), 
            List.of(backToMain)
        ));
    }

    @Override
    public String getStartPageId() {
        return START_PAGE_ID;
    }

    @Override
    public Map<String, DialoguePage> getDialoguePages() {
        return pages;
    }

@Override
public String getNextPageId(Player player, Entity entity, String currentPageId, int optionIndex) {

    if (!VampireVillagerUtil.isVampireVillager(entity)) {
        return "EXIT";
    }

    if (optionIndex == 1) {
        int panicLevel = getVillagerPanicLevel(entity);

        if (panicLevel <= 30) {
            return "Q1_LOW";
        } else if (panicLevel < 70) {
            return "Q1_MID";
        } else {
            return "Q1_HIGH";
        }
    }

    switch (optionIndex) {
        case 2:
            return "Q2_STATIC";
        case 3:
            return "Q3_STATIC";
        case 4:
            return "EXIT";
        default:
            return DialogueUtils.safeGetNextPageId(pages, currentPageId, optionIndex, getStartPageId());
    }
}


    private int getVillagerPanicLevel(Entity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            BlockPos pos = entity.blockPosition();
            ChunkPos chunk = new ChunkPos(pos);
            
            MasqueradeVillageData data = MasqueradeVillageData.get(serverLevel);
            ChunkPos centerKey = data.getCenterKeyForChunk(chunk);
            Double panic = centerKey != null ? data.getPanicForChunk(centerKey) : null;

            if (panic != null) {
                return panic.intValue();
            }
        }
        
        return 35; 
    }
}
