package carpetextra.dispenser.behaviors;

import java.util.Set;

import carpetextra.dispenser.DispenserItemUsageContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class StripBlocksDispenserBehavior extends FallibleItemDispenserBehavior {
    private static final Set<Block> DEOXIDIZE_BLOCKS = Oxidizable.OXIDATION_LEVEL_DECREASES.get().keySet();
    private static final Set<Block> DEWAX_BLOCKS = HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().keySet();
    private static final Set<Block> DEOXIDIZE_RESULTS = Oxidizable.OXIDATION_LEVEL_DECREASES.get().values();
    private static final Set<Block> DEWAX_RESULTS = HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().values();

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(true);
        ServerWorld world = pointer.world();
        Direction dispenserFacing = pointer.state().get(DispenserBlock.FACING);
        BlockPos frontBlockPos = pointer.pos().offset(dispenserFacing);
        Block frontBlock = world.getBlockState(frontBlockPos).getBlock();

        if (canStrip(frontBlock) || isStripResult(frontBlock)) {
            BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(frontBlockPos), dispenserFacing.getOpposite(), frontBlockPos, false);
            ItemUsageContext context = new DispenserItemUsageContext(world, stack, hitResult);

            if (stack.getItem().useOnBlock(context).isAccepted()) {
                stack.damage(1, world, null, (_) -> stack.setCount(0));
                return stack;
            }
        }

        this.setSuccess(false);
        return stack;
    }

    public static boolean canStrip(Block block) {
        return block.getDefaultState().isIn(BlockTags.LOGS)
                || DEOXIDIZE_BLOCKS.contains(block)
                || DEWAX_BLOCKS.contains(block);
    }

    public static boolean isStripResult(Block block) {
        return block.getDefaultState().isIn(BlockTags.LOGS)
                || DEOXIDIZE_RESULTS.contains(block)
                || DEWAX_RESULTS.contains(block);
    }
}
