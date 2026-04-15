package aster.songweaver.api.spell.requirement;

import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Requirement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;

public class
StructureRequirement implements Requirement {

    private final IMultiblock structure;

    public StructureRequirement(IMultiblock structure) {
        this.structure = structure;
    }


    @Override
    public CastFeedback check(ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity controller, boolean ritual) {

        if (controller == null) {
            return CastFeedback.RITUAL_DRAFT_MISMATCH;
        }



        BlockPos anchorpos = controller.getPos().subtract(new Vec3i(0, 1, 0));

        BlockRotation rotation = structure.validate(controller.getWorld(), anchorpos);


        boolean valid = rotation != null && structure.validate(controller.getWorld(), anchorpos, rotation);

        if (!valid){
            return CastFeedback.INVALID_STRUCTURE;
        }
        return null;




    }



}
