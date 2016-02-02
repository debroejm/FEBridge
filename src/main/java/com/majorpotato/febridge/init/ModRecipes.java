package com.majorpotato.febridge.init;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModRecipes {

    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockShop, 1, 0), "iei", "gig", "ggg", 'g', "paneGlass", 'i', "ingotGold", 'e', Blocks.enchanting_table));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockService, 1, 0), "sis", "iri", "sis", 's', Blocks.stonebrick, 'i', "ingotGold", 'r', "blockRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockTollGate, 1, 0), " s ", "ifi", " s ", 's', Blocks.stonebrick, 'i', "ingotGold", 'f', Blocks.fence_gate));
    }
}
