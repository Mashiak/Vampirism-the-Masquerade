package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModBlocks;

import java.util.List;
import java.util.UUID;

public class ThrallBellItem extends BlockItem {
    public ThrallBellItem() {
        super(VampirismTheMasqueradeModBlocks.THRALL_BELL.get(),
              new Item.Properties().stacksTo(1).fireResistant());
    }
}


