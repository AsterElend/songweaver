package aster.songweaver;


import aster.songweaver.registry.*;

import aster.songweaver.system.DraftReloadListener;
import aster.songweaver.system.RitualReloadListener;
import aster.songweaver.system.cast.SongServerCasting;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Songweaver implements ModInitializer {
	public static final String MOD_ID = "songweaver";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.


		LOGGER.info("Hello Fabric world!");
		LoomItems.registerItems();

		DraftRegistry.init();
		RitualRegistry.init();
		LoomMiscRegistry.init();


		SongServerCasting.registerServer();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new DraftReloadListener());

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new RitualReloadListener());

		BlockRenderLayerMap.INSTANCE.putBlock(LoomMiscRegistry.BOBBIN, RenderLayer.getCutout());



		



	}





}