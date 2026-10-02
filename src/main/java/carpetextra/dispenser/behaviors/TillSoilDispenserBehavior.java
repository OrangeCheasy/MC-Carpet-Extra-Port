package carpetextra.dispenser.behaviors;

import carpetextra.dispenser.DispenserItemUsageContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class TillSoilDispenserBehavior extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(true);
        ServerWorld world = pointer.world();
        Direction dispenserFacing = pointer.state().get(DispenserBlock.FACING);
        BlockPos frontBlockPos = pointer.pos().offset(dispenserFacing);

        for (int i = 0; i < 2; i++) {
            BlockPos hoeBlockPos = frontBlockPos.down(i);
            BlockHitResult hitResult = new BlockHitResult(Vec3d.of(hoeBlockPos), dispenserFacing.getOpposite(), hoeBlockPos, false);
            ItemUsageContext context = new DispenserItemUsageContext(world, stack, hitResult);

            if (stack.getItem().useOnBlock(context).isAccepted()) {
                stack.damage(1, world, null, (_) -> stack.setCount(0));
                return stack;
            }
        }

        this.setSuccess(false);
        return stack;
    }

    public static boolean canTill(Block block) {
        return block == Blocks.FARMLAND
                || block == Blocks.GRASS_BLOCK
                || block == Blocks.PODZOL
                || block == Blocks.MYCELIUM
                || block == Blocks.DIRT_PATH
                || block.getDefaultState().isIn(BlockTags.DIRT);
    }
}
