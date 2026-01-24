package aster.songweaver.system.spell.loaders;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;

public class MultiblockLoader {
    public static StructureTemplate loadTemplate(ServerWorld world, String name) {
        StructureTemplateManager manager = world.getStructureTemplateManager();
        Identifier id = new Identifier("songweaver", "multis/" + name); // e.g., "my_multiblock"
        return manager.getTemplate(id).orElseThrow(() ->
                new IllegalArgumentException("Could not find structure template: " + id));
    }
}
