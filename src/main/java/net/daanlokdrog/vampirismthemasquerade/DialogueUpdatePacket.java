package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class DialogueUpdatePacket implements CustomPacketPayload {

public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "dialogue_update");
public static final CustomPacketPayload.Type<DialogueUpdatePacket> TYPE = new CustomPacketPayload.Type<>(ID);

public static final StreamCodec<RegistryFriendlyByteBuf, DialogueUpdatePacket> STREAM_CODEC = StreamCodec.composite(
ByteBufCodecs.VAR_INT, DialogueUpdatePacket::getVillagerId,
ByteBufCodecs.stringUtf8(256), DialogueUpdatePacket::getNewPageId,
DialogueUpdatePacket::new
);

private final int villagerId;
private final String newPageId;

public DialogueUpdatePacket(int villagerId, String newPageId) {
this.villagerId = villagerId;
this.newPageId = newPageId;
}

public int getVillagerId() { return villagerId; }
public String getNewPageId() { return newPageId; }

@Override
public CustomPacketPayload.Type<DialogueUpdatePacket> type() { return TYPE; }

public static void handle(DialogueUpdatePacket message, IPayloadContext context) {
context.enqueueWork(() -> {
Minecraft minecraft = Minecraft.getInstance();
if (minecraft.screen instanceof NegotiationScreen negotiationScreen) {
negotiationScreen.setCurrentPageId(message.newPageId);
} 
});
}

public static void sendToClient(ServerPlayer player, int villagerId, String newPageId) {
player.connection.send(new DialogueUpdatePacket(villagerId, newPageId));
}
}