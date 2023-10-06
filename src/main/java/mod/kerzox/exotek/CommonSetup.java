package mod.kerzox.exotek;

import mod.kerzox.exotek.common.datagen.ChunkDepositData;
import mod.kerzox.exotek.common.datagen.MultiblockPatternGenerator;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockValidator.loadStructureFiles;
import static mod.kerzox.exotek.common.datagen.ChunkDepositData.loadOreDeposits;

public class CommonSetup {

    public static void init(FMLCommonSetupEvent event) {
        MultiblockPatternGenerator.init();
        loadStructureFiles();
        loadOreDeposits();
    }



}
