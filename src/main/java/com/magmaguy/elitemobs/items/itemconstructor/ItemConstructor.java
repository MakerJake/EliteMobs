package com.magmaguy.elitemobs.items.itemconstructor;

import com.magmaguy.elitemobs.ChatColorConverter;
import com.magmaguy.elitemobs.config.ItemUpgradeSystemConfig;
import com.magmaguy.elitemobs.items.EliteItemLore;
import com.magmaguy.elitemobs.items.ItemTagger;
import com.magmaguy.elitemobs.items.customenchantments.SoulbindEnchantment;
import com.magmaguy.elitemobs.items.potioneffects.ElitePotionEffectContainer;
import com.magmaguy.elitemobs.mobconstructor.EliteEntity;
import com.magmaguy.elitemobs.utils.ItemStackGenerator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemConstructor {

    public static ItemStack constructItem(String rawName,
                                          Material material,
                                          HashMap<Enchantment, Integer> enchantments,
                                          HashMap<String, Integer> customEnchantments,
                                          List<String> potionEffects,
                                          List<String> lore,
                                          EliteEntity eliteEntity,
                                          Player player,
                                          boolean showItemWorth,
                                          int customModelID) {

        ItemStack itemStack;
        ItemMeta itemMeta;

        /*
        Construct initial item
         */
        itemStack = ItemStackGenerator.generateItemStack(material);
        itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColorConverter.convert(rawName));

        /*
        Generate item enchantments
        Note: This only applies enchantments up to a certain level, above that threshold item enchantments only exist
        in the item lore and get interpreted by the combat system
         */
        if (!enchantments.isEmpty())
            EnchantmentGenerator.generateEnchantments(itemMeta, enchantments);

        itemStack.setItemMeta(itemMeta);

        /*
        Generate item lore
         */
        if (!lore.isEmpty())
            ItemTagger.registerCustomLore(itemMeta, lore);

        //Tag the potion effects
        new ElitePotionEffectContainer(itemMeta, potionEffects);

        itemStack.setItemMeta(itemMeta);

        //Apply custom model id
        if (customModelID > 0) {
            itemMeta.setCustomModelData(customModelID);
            itemStack.setItemMeta(itemMeta);
        }

        return commonFeatures(itemStack, eliteEntity, player, enchantments, customEnchantments, showItemWorth);
    }

    /*
    For procedurally generated items
     */
    public static ItemStack constructItem(double itemTier, EliteEntity killedMob, Player player, boolean showItemWorth) {

        ItemStack itemStack;
        ItemMeta itemMeta;

        /*
        Generate material
         */
        Material itemMaterial = MaterialGenerator.generateMaterial(itemTier);

        /*
        Construct initial item
         */
        itemStack = ItemStackGenerator.generateItemStack(itemMaterial);
        itemMeta = itemStack.getItemMeta();

         /*
        Generate item enchantments
        Note: This only gets a list of enchantments to be applied later at the lore stage
         */
        HashMap<Enchantment, Integer> enchantmentMap = EnchantmentGenerator.generateEnchantments(itemTier, itemMaterial, itemMeta);

        /*
        Generate custom enchantments
        Note: This only gets a list of enchantments to be applied later at the lore stage
         */
        HashMap<String, Integer> customEnchantmentMap = EnchantmentGenerator.generateCustomEnchantments(itemTier, itemMaterial);

        /*
        Generate item name
         */
        itemMeta.setDisplayName(NameGenerator.generateName(itemMaterial));

        /*
        Colorize with MMO colors
         */
        itemStack.setItemMeta(itemMeta);
        ItemQualityColorizer.dropQualityColorizer(itemStack);

        return commonFeatures(itemStack, killedMob, player, enchantmentMap, customEnchantmentMap, showItemWorth);

    }

    private static ItemStack commonFeatures(ItemStack itemStack,
                                            EliteEntity eliteEntity,
                                            Player player,
                                            HashMap<Enchantment, Integer> enchantments,
                                            HashMap<String, Integer> customEnchantments,
                                            boolean showItemWorth) {

        ItemMeta itemMeta = itemStack.getItemMeta();

        //hide default lore
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        /*
        Register elite item
         */
        ItemTagger.registerEliteItem(itemMeta);

        /*
        Register item source for lore redraw
         */
        ItemTagger.registerItemSource(eliteEntity, itemMeta);

        //Tag the item
        ItemTagger.registerEnchantments(itemMeta, enchantments);
        ItemTagger.registerCustomEnchantments(itemMeta, customEnchantments);

        itemStack.setItemMeta(itemMeta);

        /*
        Add soulbind if applicable
         */
        SoulbindEnchantment.addEnchantment(itemStack, player);

        /*
        Update lore
         */
        new EliteItemLore(itemStack, showItemWorth);

        return itemStack;

    }

    public static ItemStack constructScrapItem(int scrapLevel,
                                               Player player,
                                               boolean showItemWorth) {

        ItemStack itemStack;
        ItemMeta itemMeta;

        /*
        Construct initial item
         */
        itemStack = ItemStackGenerator.generateItemStack(getScrapMaterial(scrapLevel));
        itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColorConverter.convert(ItemUpgradeSystemConfig.scrapItemName.replace("$level", scrapLevel + "")));

        /*
        Register the elite scrap level
         */
        ItemTagger.registerCustomEnchantment(itemMeta, "EliteScrap", scrapLevel);

        itemStack.setItemMeta(itemMeta);

        /*
        Generate item lore
         */
        ItemTagger.registerCustomLore(itemMeta, ItemUpgradeSystemConfig.scrapItemLore);

        itemStack.setItemMeta(itemMeta);

        return commonFeatures(itemStack, null, player, new HashMap<>(), new HashMap<>(), showItemWorth);
    }

    public static ItemStack constructUpgradeItem(int upgradeLevel,
                                                 Player player,
                                                 boolean showItemWorth) {

        ItemStack itemStack;
        ItemMeta itemMeta;

        /*
        Construct initial item
         */
        itemStack = ItemStackGenerator.generateItemStack(Material.HEART_OF_THE_SEA);
        itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColorConverter.convert(ItemUpgradeSystemConfig.upgradeItemName.replace("$level", upgradeLevel + "")));

        /*
        Register the elite scrap level
         */
        ItemTagger.registerCustomEnchantment(itemMeta, "EliteUpgradeItem", upgradeLevel);

        itemStack.setItemMeta(itemMeta);

        /*
        Generate item lore
         */
        List<String> parsedLore = new ArrayList<>();
        for (String string : ItemUpgradeSystemConfig.upgradeItemLore)
            parsedLore.add(string.replace("$orbLevel", upgradeLevel + "").replace("$itemLevel", (upgradeLevel - 1) + ""));
        ItemTagger.registerCustomLore(itemMeta, parsedLore);

        itemStack.setItemMeta(itemMeta);

        return commonFeatures(itemStack, null, player, new HashMap<>(), new HashMap<>(), showItemWorth);
    }

    private static Material getScrapMaterial(int scrapLevel) {
        int dyeTier = (int) Math.floor(scrapLevel / 13.0);
        switch (dyeTier) {
            case 0:
                return Material.WHITE_DYE;
            case 1:
                return Material.LIGHT_GRAY_DYE;
            case 2:
                return Material.GRAY_DYE;
            case 3:
                return Material.GREEN_DYE;
            case 4:
                return Material.LIGHT_BLUE_DYE;
            case 5:
                return Material.BLUE_DYE;
            case 6:
                return Material.CYAN_DYE;
            case 7:
                return Material.YELLOW_DYE;
            case 8:
                return Material.ORANGE_DYE;
            case 9:
                return Material.RED_DYE;
            case 10:
                return Material.PINK_DYE;
            case 11:
                return Material.MAGENTA_DYE;
            case 12:
                return Material.PURPLE_DYE;
            case 13:
                return Material.BROWN_DYE;
            case 14:
            default:
                return Material.BLACK_DYE;
        }
    }

}
