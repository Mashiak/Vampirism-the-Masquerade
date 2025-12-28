package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage; 

import java.util.Map;

public interface IDialogueProvider {

    String getNextPageId(Player player, Entity entity, String currentPageId, int optionIndex);

    Map<String, DialoguePage> getDialoguePages();

    String getStartPageId();
}