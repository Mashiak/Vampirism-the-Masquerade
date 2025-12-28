package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock.CoffinPart;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult; 
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static de.teamlapen.vampirism.blocks.CoffinBlock.VERTICAL;

@Mixin(CoffinBlock.class)
public abstract class CoffinBlockMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void vtm_wakeUpSleepingAdvisor(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide()) return;

        BlockPos headPos = getActualCoffinHeadPos(pos, state);
        if (headPos == null) return;

        AABB searchArea = new AABB(headPos).inflate(0.5);
        List<AdvancedVampireEntity> advisors = level.getEntitiesOfClass(AdvancedVampireEntity.class, searchArea, 
            e -> e.isSleeping() && e.getPersistentData().getBoolean("IsAdvisor"));

        if (advisors.isEmpty()) return;

        AdvancedVampireEntity advisor = advisors.get(0);
        advisor.stopSleeping();
        advisor.getPersistentData().putLong("VTM_SleepLockoutTime", level.getGameTime() + 40);

        Direction facing = state.getValue(HORIZONTAL_FACING);
        Direction pushDir = facing.getOpposite();
        double force = 0.5;
        double verticalLift = state.getValue(VERTICAL) ? 0.2 : 0.1;

        advisor.push(pushDir.getStepX() * force, verticalLift, pushDir.getStepZ() * force);

        cir.setReturnValue(InteractionResult.sidedSuccess(false));
        cir.cancel();
    }

    private BlockPos getActualCoffinHeadPos(BlockPos pos, BlockState state) {
        if (state.getValue(CoffinBlock.PART) == CoffinPart.HEAD) return pos;

        if (state.getValue(CoffinBlock.PART) == CoffinPart.FOOT) {
            Direction facing = state.getValue(HORIZONTAL_FACING);
            return state.getValue(VERTICAL) ? pos.above() : pos.relative(facing);
        }
        return null;
    }
}