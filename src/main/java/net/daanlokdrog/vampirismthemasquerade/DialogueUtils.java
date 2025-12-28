package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public class DialogueUtils {

    public static String safeGetNextPageId(Map<String, DialoguePage> pages,
                                           String currentPageId,
                                           int optionIndex,
                                           String defaultPageId) {
        if (currentPageId == null || pages == null) {
            return defaultPageId;
        }

        DialoguePage page = pages.get(currentPageId);
        if (page == null || page.options == null || page.options.isEmpty()) {
            return defaultPageId;
        }

        if (optionIndex < 0 || optionIndex >= page.options.size()) {
            return defaultPageId;
        }

        DialogueOption option = page.options.get(optionIndex);
        if (option == null || option.nextState == null) {
            return defaultPageId;
        }
        
        if ("EXIT".equals(option.nextState)) {
            return "EXIT";
        }

        return option.nextState;
    }
}
