package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class GenerateLanguage extends LanguageProvider {

    public GenerateLanguage(PackOutput output, String locale) {
        super(output, Exotek.MODID, locale);
    }

    @Override
    protected void addTranslations() {
//        Material.MATERIALS.forEach((s, material) -> {
//            String materialName = Character.toUpperCase(material.getName().charAt(0)) + material.getName().substring(1);
//            for (Material.Component component : material.getComponents()) {
//                String componentName = materialName + " " + Character.toUpperCase(
//                        component.toString().charAt(0)) +
//                        component.toString().substring(1);
//
//                if (component.isBlock())
//                    add(material.getComponentPair(component).getFirst().get(), componentName);
//                else if (!component.isFluid()) {
//                    add(material.getComponentPair(component).getSecond().get(), componentName);
//                }
//            }
//        });
    }
}
