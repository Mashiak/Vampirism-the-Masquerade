package net.daanlokdrog.vampirismthemasquerade;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.util.Tuple;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.init.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

@Mod("vampirism_the_masquerade")
public class VampirismTheMasqueradeMod {
	public static final Logger LOGGER = LogManager.getLogger(VampirismTheMasqueradeMod.class);
	public static final String MODID = "vampirism_the_masquerade";

	public VampirismTheMasqueradeMod(IEventBus modEventBus) {
		// Start of user code block mod constructor
		MasqueradeEffect.MOB_EFFECTS.register(modEventBus);
		ModEventBusSubscriber.MENUS.register(modEventBus);
		modEventBus.register(ModEventBusSubscriber.class);
		net.daanlokdrog.vampirismthemasquerade.MasqueradeTaskRewards.register(modEventBus);
		net.daanlokdrog.vampirismthemasquerade.MasqueradeTask.register(modEventBus);
		net.daanlokdrog.vampirismthemasquerade.ModSoundEvents.register(modEventBus);
		net.daanlokdrog.vampirismthemasquerade.MinionTasks.register(modEventBus);
		net.daanlokdrog.vampirismthemasquerade.CoffinPOIs.register(modEventBus);
		NeoForge.EVENT_BUS.register(net.daanlokdrog.vampirismthemasquerade.AdvisorGoalInjector.class);
		VampirismTheMasqueradeModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		net.daanlokdrog.vampirismthemasquerade.capability.ThrallAttachment.ATTACHMENTS.register(modEventBus);
		net.daanlokdrog.vampirismthemasquerade.init.ModEntities.ENTITIES.register(modEventBus);
		Class<?> c = net.daanlokdrog.vampirismthemasquerade.registry.MasqueradeRaiderTypes.class;
		net.daanlokdrog.vampirismthemasquerade.init.LargeCampfireSmokeParticles.REGISTRY.register(modEventBus);
		NeoForge.EVENT_BUS.register(net.daanlokdrog.vampirismthemasquerade.client.HumorIconRenderer.class);
		// End of user code block mod constructor
		NeoForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::registerNetworking);
		VampirismTheMasqueradeModBlocks.REGISTRY.register(modEventBus);
		VampirismTheMasqueradeModItems.REGISTRY.register(modEventBus);
		VampirismTheMasqueradeModTabs.REGISTRY.register(modEventBus);
		VampirismTheMasqueradeModVariables.ATTACHMENT_TYPES.register(modEventBus);
		VampirismTheMasqueradeModMobEffects.REGISTRY.register(modEventBus);
		VampirismTheMasqueradeModParticleTypes.REGISTRY.register(modEventBus);
		VampirismTheMasqueradeModVillagerProfessions.PROFESSIONS.register(modEventBus);
		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
	}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MODID);
		MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
		networkingRegistered = true;
	}

	private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new Tuple<>(action, tick));
	}

	@SubscribeEvent
	public void tick(ServerTickEvent.Post event) {
		List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
		workQueue.forEach(work -> {
			work.setB(work.getB() - 1);
			if (work.getB() == 0)
				actions.add(work);
		});
		actions.forEach(e -> e.getA().run());
		workQueue.removeAll(actions);
	}
}