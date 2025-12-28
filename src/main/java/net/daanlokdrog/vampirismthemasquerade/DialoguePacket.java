package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;

public class DialoguePacket implements CustomPacketPayload {
	
public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "dialogue_choice");
public static final CustomPacketPayload.Type<DialoguePacket> TYPE = new CustomPacketPayload.Type<>(ID);

public static final StreamCodec<RegistryFriendlyByteBuf, DialoguePacket> STREAM_CODEC = StreamCodec.composite(
ByteBufCodecs.VAR_INT, DialoguePacket::getVillagerId,
ByteBufCodecs.VAR_INT, DialoguePacket::getOptionIndex, 
ByteBufCodecs.stringUtf8(256), DialoguePacket::getCurrentPageId,
DialoguePacket::new
);

private final int villagerId;
private final int optionIndex; 
private final String currentPageId;

public DialoguePacket(int villagerId, int optionIndex, String currentPageId) {
this.villagerId = villagerId;
this.optionIndex = optionIndex;
this.currentPageId = currentPageId;
}
public int getVillagerId() { return villagerId; }
public int getOptionIndex() { return optionIndex; }
public String getCurrentPageId() { return currentPageId; }
public CustomPacketPayload.Type<DialoguePacket> type() { return TYPE; }

public static void handle(DialoguePacket message, IPayloadContext context) {
context.enqueueWork(() -> {
ServerPlayer player = (ServerPlayer) context.player();
Entity targetEntity = player.level().getEntity(message.villagerId);

if (targetEntity == null) {
    return;
}

IDialogueProvider provider = DialogueProviderRegistry.getProvider(targetEntity);

if (provider == null) {
    return;
}

String nextStateId = provider.getNextPageId(player, targetEntity, message.currentPageId, message.optionIndex);

if ("EXIT".equals(nextStateId)) {
    return;
}

DialogueUpdatePacket.sendToClient(player, message.villagerId, nextStateId);
});
}

public static void sendToServer(int villagerId, int optionIndex, String currentPageId) {
PacketDistributor.sendToServer(new DialoguePacket(villagerId, optionIndex, currentPageId));
}
}