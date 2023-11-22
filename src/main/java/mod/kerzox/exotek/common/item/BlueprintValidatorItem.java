package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockException;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.Blueprint;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockValidator;
import mod.kerzox.exotek.common.capability.BlueprintHandler;
import mod.kerzox.exotek.common.capability.IBlueprintCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BlueprintValidatorItem extends Item {

    public BlueprintValidatorItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack of(Item item, String blueprintName) {
        ItemStack temp = new ItemStack(item);
        temp.getCapability(Blueprint.BLUEPRINT_CAPABILITY).ifPresent(cap -> {
            cap.setBlueprint(MultiblockValidator.getBlueprint(blueprintName));
        });
        return temp;
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        Optional<IBlueprintCapability> capability = p_41458_.getCapability(Blueprint.BLUEPRINT_CAPABILITY).resolve();
        if (capability.isPresent()) {
            return Component.translatable(this.getDescriptionId(p_41458_))
                    .append(Component.translatable(": "+capability.get().getBlueprint().getPattern().getName()));
        }
        return super.getName(p_41458_);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return super.getDefaultInstance();
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Optional<IBlueprintCapability> capability = pContext.getItemInHand().getCapability(Blueprint.BLUEPRINT_CAPABILITY).resolve();
        if (pContext.getPlayer().isShiftKeyDown()) {
            if (!(pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock() instanceof AirBlock)) {
                if (capability.isPresent()) {
                    try {
                        if (capability.get() instanceof BlueprintHandler handler) {
                            handler.setFacing(pContext.getClickedFace().getOpposite());
                            handler.setRender(true);
                            handler.setPosition(pContext.getClickedPos());
                        }
                        MultiblockValidator.loadStructureFiles();
                        if (!pContext.getLevel().isClientSide) {
                            MultiblockValidator.attemptMultiblockFormation(capability.get().getBlueprint(), pContext.getClickedFace().getOpposite(), pContext.getClickedPos(), pContext.getLevel().getBlockState(pContext.getClickedPos()), pContext.getLevel(), pContext.getPlayer());

//                            if (pContext.getPlayer().isCreative()) {
//                                  MultiblockValidator.attemptMultiblockFormation(MultiblockValidator.getBlueprint("generated_multiblock_structure_0"), pContext.getClickedFace().getOpposite(), pContext.getClickedPos(), pContext.getLevel().getBlockState(pContext.getClickedPos()), pContext.getLevel(), pContext.getPlayer());
//
////                                for (BlockPredicate predicate : MultiblockValidator.getBlueprint("generated_multiblock_structure_0").getPattern().getPredicates()) {
////                                    pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos().offset(predicate.getPosition()), predicate.getValidBlocks()[0].defaultBlockState());
////                                }
//                            } else {
//                           }
                        }
                        return InteractionResult.CONSUME;
                    } catch (MultiblockException exception) {
                        pContext.getPlayer().sendSystemMessage(Component.literal(exception.getMessage()));
                    }

                }
            }
        }
        else if (capability.isPresent() && !pContext.getLevel().isClientSide) {
            BlueprintHandler handler = (BlueprintHandler) capability.get();

            handler.addPosition(pContext.getClickedFace(), pContext.getLevel(), pContext.getClickedPos());

        }
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41421_.getCapability(Blueprint.BLUEPRINT_CAPABILITY).ifPresent(cap -> {
            if (cap.getBlueprint() != null) p_41423_.add(Component.literal(cap.getBlueprint().getMultiblockName()));
            else p_41423_.add(Component.literal("No blueprint"));
        });

        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BlueprintHandler();
    }
}
