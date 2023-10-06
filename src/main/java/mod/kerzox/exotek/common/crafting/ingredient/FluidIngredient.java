package mod.kerzox.exotek.common.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FluidIngredient extends AbstractIngredient {

    private ProxyFluid fluid;

    protected FluidIngredient(ProxyFluid fluid) {
        super(Stream.empty());
        this.fluid = fluid;
    }

    public static FluidIngredient of(ProxyFluid fluid) {
        return new FluidIngredient(fluid);
    }

    public static FluidIngredient of(FluidStack fluid) {
        return new FluidIngredient(ProxyFluid.of(fluid));
    }

    public static FluidIngredient of(ResourceLocation fluid, int amount) {
        return new FluidIngredient(ProxyFluid.of(fluid, amount));
    }

    public static FluidIngredient of(TagKey<Fluid> fluid, int amount) {
        return new FluidIngredient(ProxyFluid.of(fluid, amount));
    }

    public static FluidIngredient of(FriendlyByteBuf fromBuffer) {
        return new FluidIngredient(ProxyFluid.parse(fromBuffer));
    }

    public static FluidIngredient of(JsonObject json) {
        return new FluidIngredient(ProxyFluid.parse(json));
    }

    public List<FluidStack> getFluidStacks() {
        return getProxy().asFluidStacks();
    }

    ItemStack[] cached;

    @Override
    public ItemStack[] getItems() {
        if (cached == null) {
            cached = fluid.asFluidStacks().stream().map(FluidUtil::getFilledBucket).filter(i -> !i.isEmpty()).toArray(ItemStack[]::new);
        }
        return cached;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        if (pStack == null) {
            return false;
        }
        Optional<IFluidHandlerItem> handlerItem = FluidUtil.getFluidHandler(pStack).resolve();
        if (handlerItem.isEmpty()) return false;

        IFluidHandlerItem capability = handlerItem.get();

        for (int i = 0; i < capability.getTanks(); i++) {
            FluidStack tankStack = capability.getFluidInTank(i);
            if (fluid.isSameFluid(tankStack)) {
                FluidStack toExtract = new FluidStack(tankStack.getFluid(), fluid.amount);
                FluidStack simulation = capability.drain(toExtract, IFluidHandler.FluidAction.SIMULATE);
                if (simulation.getAmount() >= fluid.amount) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean test(FluidStack stack) {
        if (stack == null) {
            return false;
        }
        return fluid.isValidFluidAndAmount(stack);
    }

    public boolean testWithoutAmount(FluidStack stack) {
        if (stack == null) {
            return false;
        }
        return fluid.isSameFluid(stack);
    }


    public boolean drain(FluidStack stack, boolean simulate) {
        if (test(stack)) {
            if (simulate) {
                return true;
            } else {
                stack.shrink(fluid.amount);
                return true;
            }
        }
        return false;
    }

    public ProxyFluid getProxy() {
        return fluid;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        return getProxy().toJson();
    }

    public static class ProxyFluid {

        private static TagKey<Fluid> vanilla(String name)
        {
            return FluidTags.create(new ResourceLocation("minecraft", name));
        }

        private static TagKey<Fluid> forgeTag(String name)
        {
            return FluidTags.create(new ResourceLocation("forge", name));
        }

        private Either<TagKey<Fluid>, List<ResourceLocation>> fluid;
        private int amount;

        private ProxyFluid(Either<TagKey<Fluid>, List<ResourceLocation>> fluids, int amount) {
            this.fluid = fluids;
            this.amount = amount;
        }

        public static ProxyFluid of(Either<TagKey<Fluid>, List<ResourceLocation>> either, int amount) {
            return new ProxyFluid(either, amount);
        }

        public static ProxyFluid of(ResourceLocation resource, int amount) {
            return resource.getNamespace().contains("minecraft") ? ProxyFluid.of(vanilla(resource.getPath()),amount) : ProxyFluid.of(forgeTag(resource.getPath()), amount);
        }

        public static ProxyFluid of(TagKey<Fluid> tag, int amount) {
            return new ProxyFluid(Either.left(tag), amount);
        }

        public static ProxyFluid of(@Nonnull FluidStack stack) {
            ResourceLocation location = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
            return location.getNamespace().contains("minecraft") ? ProxyFluid.of(vanilla(location.getPath()), stack.getAmount()) : ProxyFluid.of(forgeTag(location.getPath()), stack.getAmount());
        }

        public List<FluidStack> asFluidStacks() {
            return fluid.map(
                    t -> StreamSupport.stream(ForgeRegistries.FLUIDS.tags().getTag(t).spliterator(), false),
                    r -> r.stream().map(ForgeRegistries.FLUIDS::getValue)
            ).map(fluid1 -> new FluidStack(fluid1, this.amount)).collect(Collectors.toList());
        }

        public boolean isExactFluid(FluidStack fluidStack) {
            return isSameFluid(fluidStack) && fluidStack.getAmount() == this.amount;
        }

        public boolean isValidFluidAndAmount(FluidStack fluidStack) {
            return isSameFluid(fluidStack) && fluidStack.getAmount() >= this.amount;
        }

        public boolean isSameFluid(FluidStack fluidStack) {
            if (fluidStack == null) return false;
            boolean hasTag  =false;
            if (fluid.left().isPresent()) {
                if (ForgeRegistries.FLUIDS.tags().getTag(fluid.left().get()).contains(fluidStack.getFluid())) {
                    hasTag = true;
                }
            }
            if (fluid.right().isPresent()) {
                if (fluid.right().get().contains(ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()))) {
                    hasTag = true;
                }
            }

            return hasTag;
        }

        public int getAmount() {
            return amount;
        }

        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            ResourceLocation name = this.fluid.orThrow().location();
            jsonObject.addProperty("tag", name.toString());
            jsonObject.addProperty("amount", this.amount);
            return jsonObject;
        }

        public static ProxyFluid parse(JsonObject json) {
            JsonObject jsonObject = json.getAsJsonObject();
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
            int amount = GsonHelper.getAsInt(jsonObject, "amount");
            return ProxyFluid.of(resourceLocation, amount);
        }


        public static ProxyFluid parse(FriendlyByteBuf buffer) {
            int numMatching = buffer.readVarInt();
            List<ResourceLocation> matching = new ArrayList<>(numMatching);
            for (int i = 0; i < numMatching; ++i) {
                matching.add(buffer.readResourceLocation());
            }
            int amount = buffer.readInt();
            return ProxyFluid.of(Either.right(matching), amount);
        }


        public void write(FriendlyByteBuf buffer) {
            List<ResourceLocation> matching = new ArrayList<>();
            if (fluid.left().isPresent()) {
                ITag<Fluid> tags = ForgeRegistries.FLUIDS.tags().getTag(fluid.left().get());
                for (Fluid tag : tags) {
                    matching.add(ForgeRegistries.FLUIDS.getKey(tag));
                }
            }
            if (fluid.right().isPresent()) {
                matching.addAll(fluid.right().get());
            }
            buffer.writeVarInt(matching.size());
            for (ResourceLocation rl : matching) buffer.writeResourceLocation(rl);
            buffer.writeInt(this.amount);
        }


    }

    public static class Serializer implements IIngredientSerializer<FluidIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public FluidIngredient parse(FriendlyByteBuf buffer) {
            return FluidIngredient.of(buffer);
        }

        @Override
        public FluidIngredient parse(JsonObject json) {
            return FluidIngredient.of(json);
        }

        @Override
        public void write(FriendlyByteBuf buffer, FluidIngredient ingredient) {
            ingredient.getProxy().write(buffer);
        }
    }

}
