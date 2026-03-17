package aster.songweaver;


import aster.songweaver.cca.HaloComponent;
import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.DimensionStuff;
import aster.songweaver.registry.LoomMultiblocks;
import aster.songweaver.registry.MagicRegistry;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomItemGroup;
import aster.songweaver.registry.physical.LoomItems;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.world.trees.LoomFoliagePlacers;
import aster.songweaver.registry.world.trees.LoomTrunkPlacers;
import aster.songweaver.system.LoomRuleTests;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.loaders.DraftReloadListener;
import aster.songweaver.system.spell.loaders.RitualReloadListener;
import aster.songweaver.util.SpellUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Songweaver implements ModInitializer {
	public static final String MOD_ID = "songweaver";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier locate (String name){
		return new Identifier(MOD_ID, name);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.



		LOGGER.info("Hello Fabric world!");
		LoomItems.registerItems();

		MagicRegistry.init();
		LoomMiscRegistry.init();
		LoomBlockStuff.init();
		LoomTrunkPlacers.init();
		LoomFoliagePlacers.init();


		SongServerCasting.registerServer();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new DraftReloadListener());

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new RitualReloadListener());


		LoomMultiblocks.init();

		LoomItemGroup.init();

		DimensionStuff.init();

		LoomRuleTests.register();

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient) {
				return ActionResult.PASS;
			}

			if (hand != Hand.MAIN_HAND) {
				return ActionResult.PASS;
			}

			if (SpellUtil.tryClientCast(player, hand)) {
				return ActionResult.CONSUME; // 🔥 blocks villager / boat / item frame
			}

			return ActionResult.PASS;
		});


		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClient) {
				return ActionResult.PASS;
			}

			if (hand != Hand.MAIN_HAND) {
				return ActionResult.PASS;
			}

			if (SpellUtil.tryClientCast(player, hand)) {
				return ActionResult.CONSUME; // 🔥 blocks chest / GUI / doors
			}

			return ActionResult.PASS;
		});


		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) ->
						SongweaverCommands.register(dispatcher)
		);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;

			// Get the Halo component for this player
			HaloComponent halo = SongweaverComponents.HALO.get(player);

			// Check if the component has uninitialized/default stacks
			boolean needsInit = halo.getStacks().stream().allMatch(ItemStack::isEmpty);

			if (needsInit) {
				// Initialize default stacks (empty inventory)
				// This is mostly just defensive; readFromNbt already handles missing data
				halo.purge();

				// Sync with the client so the player sees it immediately
				SongweaverComponents.HALO.sync(player);
			}
		});




	}





}