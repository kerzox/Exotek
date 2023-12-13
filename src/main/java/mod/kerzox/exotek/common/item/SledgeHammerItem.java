package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.OreDeposit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class SledgeHammerItem extends ExotekItem {

    public SledgeHammerItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        level.getChunkAt(pos).getCapability(ExotekCapabilities.DEPOSIT_CAPABILITY).ifPresent(cap -> {

            if (cap instanceof ChunkDeposit chunkDeposit) {

                if (chunkDeposit.isFluidDeposit()) {
                    ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

                    CompoundTag tag = new CompoundTag();

                    stack.addTagElement("filtered_title", StringTag.valueOf("Fluids because im retarded and forgot to actually implement these properly should still exist and stuff"));
                    ListTag listtag = new ListTag();
                    String str = "'";

                    for (FluidStack fluid : chunkDeposit.getFluids()) {
                        str += "" + fluid.getDisplayName().getString().toString() + "\n";
                        str += "Amount: " + fluid.getAmount() + "\n";
                    }

                    str += "'";

                    listtag.add(StringTag.valueOf(str));


                    stack.addTagElement("pages", listtag);
                    stack.addTagElement("title", StringTag.valueOf("Fluids"));
                    stack.addTagElement("author", StringTag.valueOf("Sample Drill"));
                    stack.addTagElement("resolved", StringTag.valueOf(String.valueOf((byte) 1)));

                    System.out.println(stack.getTag());

                    ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    level.addFreshEntity(entity);
                }

                if (chunkDeposit.isOreDeposit()) {
                    ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

                    CompoundTag tag = new CompoundTag();

                    stack.addTagElement("filtered_title", StringTag.valueOf(chunkDeposit.getOreDeposit().getName()));
                    ListTag listtag = new ListTag();
                    String str = "'";

                    for (OreDeposit.OreStack item : chunkDeposit.getItems()) {
                        str += "" + item.getItemStack().getItem().toString() + "\n";
                        str += "Amount: " + item.getItemStack().getCount() + "\n";
                    }

                    str += "'";

                    listtag.add(StringTag.valueOf(str));


                    stack.addTagElement("pages", listtag);
                    stack.addTagElement("title", StringTag.valueOf(chunkDeposit.getOreDeposit().getName()));
                    stack.addTagElement("author", StringTag.valueOf("Sample Drill"));
                    stack.addTagElement("resolved", StringTag.valueOf(String.valueOf((byte) 1)));

                    System.out.println(stack.getTag());

                    ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    level.addFreshEntity(entity);
                }
            }
        });
        return super.useOn(ctx);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return null;
    }


}
