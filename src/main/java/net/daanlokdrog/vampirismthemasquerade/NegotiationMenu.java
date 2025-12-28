package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class NegotiationMenu extends AbstractContainerMenu {
    
    private final Level level;
    private final int villagerEntityId;
    private final String startPageId;

public NegotiationMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
    this(id, inv, extraData.readInt(), extraData.readUtf()); 
}

    public NegotiationMenu(int id, Inventory inv, int villagerEntityId) {
        this(id, inv, villagerEntityId, "START"); 
    }
    
    public NegotiationMenu(int id, Inventory inv, int villagerEntityId, String startPageId) {
        super(ModEventBusSubscriber.NEGOTIATION_MENU.get(), id); 
        this.level = inv.player.level();
        this.villagerEntityId = villagerEntityId;
        this.startPageId = startPageId; 
    }


    @Override
    public boolean stillValid(Player player) {
        return true; 
    }
    
    public int getVillagerEntityId() {
        return villagerEntityId;
    }
    
    public String getStartPageId() {
        return startPageId;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}