package aster.songweaver;


import aster.songweaver.client.InputBuffer;
import aster.songweaver.registry.*;

import aster.songweaver.registry.physical.Distaff;
import aster.songweaver.registry.physical.LoomItemGroup;
import aster.songweaver.registry.physical.LoomItems;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.system.spell.loaders.DraftReloadListener;
import aster.songweaver.system.spell.loaders.RitualReloadListener;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.util.SpellUtil;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


		SongServerCasting.registerServer();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new DraftReloadListener());

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new RitualReloadListener());

		LoomMultiblocks.init();

		LoomItemGroup.init();


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






	}





}