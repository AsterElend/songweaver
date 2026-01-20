package aster.songweaver.system.spell.requirement;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Requirement;
import aster.songweaver.system.spell.definition.Tier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;

public class StructureRequirement implements Requirement {

    private final IMultiblock structure;

    public StructureRequirement(IMultiblock structure) {
        this.structure = structure;
    }


    @Override
    public CastFailure check(ServerPlayerEntity caster, @Nullable RitualControllerBlockEntity controller) {

        if (controller == null) {
            return CastFailure.RITUAL_DRAFT_MISMATCH;
        }



        BlockPos anchorpos = controller.getPos().subtract(new Vec3i(0, 1, 0));

        BlockRotation rotation = structure.validate(controller.getWorld(), anchorpos);


        boolean valid = rotation != null && structure.validate(controller.getWorld(), anchorpos, rotation);

        if (!valid){
            return CastFailure.INVALID_STRUCTURE;
        }
        return null;




    }



}
