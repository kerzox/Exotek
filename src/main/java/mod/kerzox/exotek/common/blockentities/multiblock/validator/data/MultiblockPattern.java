package mod.kerzox.exotek.common.blockentities.multiblock.validator.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mod.kerzox.exotek.common.block.multiblock.MultiblockBlock;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class MultiblockPattern {

    private final String name;
    private final boolean xzAgnostic;
    private final boolean specialModel;
    private final ResourceLocation brainBlock;
    private final MultiblockStructure structure;
    private final boolean ignoreAirBlocks;

    private HashMap<BlockPos, BlockPredicate> predicateMap = new HashMap<>();

    private MultiblockPattern(String name,
                              ResourceLocation brain,
                              boolean xzAgnostic,
                              MultiblockStructure structure,
                              List<BlockPredicate> pattern,
                              boolean specialModel, boolean ignoreAirBlocks) {
        this.name = name;
        this.xzAgnostic = xzAgnostic;
        this.structure = structure;
        this.brainBlock = brain;
        this.specialModel = specialModel;
        this.ignoreAirBlocks = ignoreAirBlocks;
        for (BlockPredicate predicate : pattern) {
            predicateMap.put(predicate.getPosition(), predicate);
        }

    }

    public static MultiblockPattern of(String name, MultiblockBlock brain, boolean xzAgnostic, MultiblockStructure structure, BlockPredicate... predicates) {
        return new MultiblockPattern(name, ForgeRegistries.BLOCKS.getKey(brain), xzAgnostic, structure, Arrays.asList(predicates), false, false);
    }


    public static MultiblockPattern of(String name, MultiblockBlock brain, boolean xzAgnostic, boolean specialModel, MultiblockStructure structure, BlockPredicate... predicates) {
        return new MultiblockPattern(name, ForgeRegistries.BLOCKS.getKey(brain), xzAgnostic, structure, Arrays.asList(predicates), specialModel, false);
    }

    public static MultiblockPattern of(String name, MultiblockBlock brain, boolean xzAgnostic, boolean specialModel, boolean ignoreAirBlocks, MultiblockStructure structure, BlockPredicate... predicates) {
        return new MultiblockPattern(name, ForgeRegistries.BLOCKS.getKey(brain), xzAgnostic, structure, Arrays.asList(predicates), specialModel, ignoreAirBlocks);
    }

    public MultiblockBlock getManagingBlock() {
        return (MultiblockBlock) ForgeRegistries.BLOCKS.getValue(brainBlock);
    }

    public boolean canForm(Level level, Map<BlockPos, BlockState> blocks) throws MultiblockException {
        return true;
    }

    public boolean isSpecialModel() {
        return specialModel;
    }

    public boolean isXzAgnostic() {
        return xzAgnostic;
    }

    public MultiblockStructure getStructure() {
        return structure;
    }

    public List<BlockPredicate> getPredicates() {
        return new ArrayList<>(predicateMap.values());
    }

    public BlockPredicate getPredicateFromPos(BlockPos pos) {
        return predicateMap.get(pos);
    }

    public BlockPredicate getPredicateFromRotatedPos(Rotation rot, BlockPos rotPos) {
        for (BlockPredicate predicate : getPredicates()) {
            if (predicate.rotated(rot).equals(rotPos)) {
                return predicate;
            }
        }
        return null;
    }

    public BlockPredicate getAnchorPredicate(BlockPos anchorPos) {
        for (BlockPredicate value : predicateMap.values()) {
            if (value.isAnchor()) return value;
        }
        return null;
    }

    public List<BlockPredicate> getAnchorPredicates() {
        List<BlockPredicate> predicates = new ArrayList<>();
        for (BlockPredicate value : predicateMap.values()) {
            if (value.isAnchor()) predicates.add(value);
        }
        return predicates;
    }

    public String getName() {
        return name;
    }

    public boolean ignoringAirBlocks() {
        return ignoreAirBlocks;
    }

    // TODO

//    public static MultiblockPattern fromTag(CompoundTag tag) {
//        return new MultiblockPattern("", false, false, new MultiblockStructure(3, 3, 3), new ArrayList<>());
//    }

    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    public static MultiblockPattern fromJson(JsonObject json) {
        if (!json.has("pattern")) throw new JsonParseException("Invalid pattern");
        JsonArray array = json.getAsJsonArray("pattern");
        List<BlockPredicate> predicates = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            JsonArray valid = object.getAsJsonArray("valid_blocks");
            Block[] validBlocks = new Block[valid.size()];
            for (int i1 = 0; i1 < valid.size(); i1++) {
                validBlocks[i1] = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(valid.get(i1).getAsString()));
            }
            String pos = GsonHelper.getAsString(object, "position");

            String[] pos1 = pos.split(", ");

            BlockPos position = new BlockPos(Integer.parseInt(pos1[0]),Integer.parseInt(pos1[1]), Integer.parseInt(pos1[2]));
            predicates.add(new BlockPredicate(position, GsonHelper.getAsBoolean(object, "anchor", false), GsonHelper.getAsBoolean(object, "unique", false), validBlocks));
        }
        return new MultiblockPattern(
                GsonHelper.getAsString(json, "name"),
                new ResourceLocation(GsonHelper.getAsString(json, "brain")),
                GsonHelper.getAsBoolean(json, "xzAgnostic", false),
                new MultiblockStructure(json), predicates, GsonHelper.getAsBoolean(json, "specialModel", false),
                GsonHelper.getAsBoolean(json, "ignoreAirBlocks", false));
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray predicates = new JsonArray();
        for (BlockPredicate predicate : getPredicates()) {
            JsonObject blockData = new JsonObject();
            JsonArray blockArr = new JsonArray();
            for (Block valid : predicate.getValidBlocks()) {
                blockArr.add(ForgeRegistries.BLOCKS.getKey(valid).toString());
            }
            blockData.add("valid_blocks", blockArr);
            blockData.addProperty("position", predicate.getPosition().toShortString());
            blockData.addProperty("unique", predicate.isUnique());
            blockData.addProperty("anchor", predicate.isAnchor());
            predicates.add(blockData);
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("length", this.structure.getMax().getX());
        obj.addProperty("width", this.structure.getMax().getZ());
        obj.addProperty("height", this.structure.getMax().getY());
        json.addProperty("name", this.name);
        json.addProperty("brain", this.brainBlock.toString());
        json.addProperty("xzAgnostic", this.xzAgnostic);
        json.addProperty("specialModel", this.specialModel);
        json.addProperty("ignoreAirBlocks", this.ignoreAirBlocks);
        json.add("structure_size", obj);
        json.add("pattern", predicates);
        return json;
    }

}
