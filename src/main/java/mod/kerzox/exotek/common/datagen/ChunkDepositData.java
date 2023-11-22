package mod.kerzox.exotek.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.capability.deposit.OreDeposit;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ChunkDepositData {

    public static Map<String, OreDeposit> ORE_VEINS = new HashMap<>();

    public static final Path DEPOSIT_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.GAMEDIR.get().resolve(Exotek.MODID + "/deposits"));
    public static final Path ORE_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.GAMEDIR.get().resolve(Exotek.MODID + "/deposits/ore"));
    public static final Path FLUID_DIR = DEPOSIT_DIR.resolve("/fluid");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static void parseJson(File file) {
        try {
            if (!file.isDirectory()) {
                BufferedReader readIn = new BufferedReader(new InputStreamReader((new FileInputStream(file))));
                OreDeposit deposit = OreDeposit.fromJson(GsonHelper.parse(readIn));
                ORE_VEINS.put(deposit.getName(), deposit);
                System.out.println("Loaded: " + deposit.getName());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadOreDeposits() {
        try {
            System.out.println("Loading ore deposits from file...");
            ORE_VEINS.clear();
            Files.walk(ORE_DIR).forEach(path -> {
                parseJson(path.toFile());
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
