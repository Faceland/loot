/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.menu.upgrade;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.data.UpgradeScroll;
import info.faceland.loot.enchantments.EnchantmentTome;
import info.faceland.loot.items.prefabs.ArcaneEnhancer;
import info.faceland.loot.items.prefabs.PurifyingScroll;
import info.faceland.loot.menu.BlankIcon;
import info.faceland.loot.utils.MaterialUtil;
import info.faceland.loot.utils.NumberUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import land.face.strife.data.champion.LifeSkillType;
import land.face.strife.util.PlayerDataUtil;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantMenu extends ItemMenu {

  private LootPlugin plugin;

  private int baseEnhanceRequirement;
  private int playerPointHelmetMergeCost;
  private int enhanceReqPerTwenty;

  private ItemStack selectedEquipment;
  private ItemStack selectedUpgradeItem;

  private ConfirmIcon confirmIcon;
  private EquipmentIcon equipmentIcon;
  private UpgradeItemIcon upgradeItemIcon;

  private String validEnchant;
  private String invalidEnchant;
  private String validUpgrade;
  private String invalidUpgrade;
  private String validExtend;
  private String invalidExtend;
  private String itemNotEnchanted;
  private String alreadyEnhanced;
  private String noEnhanceLevel;
  private String validEnhance;
  private String validPurity;
  private String noHelmetMerge;
  private String validHelmetMerge;
  private List<String> noEquipmentLore;
  private List<String> noUpgradeItemLore;
  private List<String> validEnchantLore;
  private List<String> noEnchantTagLore;
  private List<String> badTomeTypeLore;
  private List<String> invalidUpgradeLore;
  private List<String> badScrollRangeLore;
  private List<String> validUpgradeLore;
  private List<String> validExtendLore;
  private List<String> invalidExtendLore;
  private List<String> itemNotEnchantedLore;
  private List<String> alreadyEnhancedLore;
  private List<String> noEnhanceLevelLore;
  private List<String> validEnhanceLore;
  private List<String> validPurityLore;
  private List<String> noHelmetMergeLore;
  private List<String> validHelmetMergeLore;

  private ItemStack blankItem;

  private static final DecimalFormat DF = new DecimalFormat("###.#");

  public EnchantMenu(LootPlugin plugin) {
    super(TextUtils.color(plugin.getSettings().getString("language.menu.menu-name",
        "&0&lUpgrade Items!")), Size.fit(27), plugin);

    this.plugin = plugin;

    baseEnhanceRequirement = plugin.getSettings().getInt("config.enhancement.base-level-req", 6);
    playerPointHelmetMergeCost = plugin.getSettings().getInt("config.helmet-merge-cost", 320);
    enhanceReqPerTwenty = plugin.getSettings()
        .getInt("config.enhancement.level-req-per-ten-levels", 9);

    validEnchant = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-enchant-name", "aaaa"));
    validUpgrade = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-upgrade-name", "aaaa"));
    validExtend = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-extend-name", "aaaa"));
    invalidEnchant = TextUtils.color(plugin.getSettings()
        .getString("language.menu.invalid-enchant-name", "aaaa"));
    invalidUpgrade = TextUtils.color(plugin.getSettings()
        .getString("language.menu.invalid-upgrade-name", "aaaa"));
    invalidExtend = TextUtils.color(plugin.getSettings()
        .getString("language.menu.invalid-extend-name", "aaaa"));
    itemNotEnchanted = TextUtils.color(plugin.getSettings()
        .getString("language.menu.not-enchanted-name", "aaaa"));
    alreadyEnhanced = TextUtils.color(plugin.getSettings()
        .getString("language.menu.already-enhanced-name", "aaaa"));
    noEnhanceLevel = TextUtils.color(plugin.getSettings()
        .getString("language.menu.no-enhance-level-name", "aaaa"));
    validEnhance = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-enhance-name", "aaaa"));
    validPurity = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-purity-name", "aaaa"));
    noHelmetMerge = TextUtils.color(plugin.getSettings()
        .getString("language.menu.no-helmet-merge", "aaaa"));
    validHelmetMerge = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-helmet-merge", "aaaa"));

    noEquipmentLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-equipment"));
    noUpgradeItemLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-upgrade"));
    validEnchantLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-enchant-lore"));
    noEnchantTagLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-tag-lore"));
    badTomeTypeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.type-mismatch-lore"));
    invalidUpgradeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.invalid-upgrade-lore"));
    badScrollRangeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.invalid-plus-range-lore"));
    validUpgradeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-upgrade-lore"));
    validExtendLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-extend-lore"));
    invalidExtendLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.invalid-extend-lore"));
    itemNotEnchantedLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.not-enchanted-lore"));
    alreadyEnhancedLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.already-enhanced-lore"));
    noEnhanceLevelLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-enhance-level-lore"));
    validEnhanceLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-enhance-lore"));
    validPurityLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-purity-lore"));
    noHelmetMergeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-helmet-merge-lore"));
    validHelmetMergeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-helmet-merge-lore"));

    blankItem = new ItemStack(Material.AIR);

    confirmIcon = new ConfirmIcon(this);
    equipmentIcon = new EquipmentIcon(this);
    upgradeItemIcon = new UpgradeItemIcon(this);

    fillEmptySlots(new BlankIcon());
    setItem(10, equipmentIcon);
    setItem(12, upgradeItemIcon);
    setItem(16, confirmIcon);
  }

  public void setSelectedEquipment(Player player, ItemStack selectedEquipment) {
    this.selectedEquipment = selectedEquipment;
    equipmentIcon.getIcon().setAmount(selectedEquipment.getAmount());
    equipmentIcon.getIcon().setType(selectedEquipment.getType());
    equipmentIcon.getIcon().setItemMeta(selectedEquipment.getItemMeta());
    equipmentIcon.setDisplayName(ItemStackExtensionsKt.getDisplayName(selectedEquipment));
    updateConfirmIcon(player);
  }

  public void setSelectedUpgradeItem(Player player, ItemStack selectedUpgradeItem) {
    this.selectedUpgradeItem = selectedUpgradeItem;
    upgradeItemIcon.getIcon().setAmount(selectedUpgradeItem.getAmount());
    upgradeItemIcon.getIcon().setType(selectedUpgradeItem.getType());
    upgradeItemIcon.getIcon().setItemMeta(selectedUpgradeItem.getItemMeta());
    upgradeItemIcon.setDisplayName(ItemStackExtensionsKt.getDisplayName(selectedUpgradeItem));
    updateConfirmIcon(player);
  }

  private void updateConfirmIcon(Player player) {
    confirmIcon.getIcon().setType(Material.BARRIER);
    List<String> lore = new ArrayList<>();
    if (selectedEquipment == null || selectedEquipment.getType() == Material.AIR) {
      confirmIcon.setDisplayName(TextUtils.color("&eNo Equipment Item..."));
      lore.addAll(noEquipmentLore);
      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (selectedUpgradeItem == null || selectedUpgradeItem.getType() == Material.AIR) {
      confirmIcon.setDisplayName(TextUtils.color("&eNo Upgrade Item..."));
      lore.addAll(noUpgradeItemLore);
      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (MaterialUtil.isNormalHead(selectedUpgradeItem)) {
      if (LootPlugin.getInstance().getPlayerPointsAPI() == null) {
        confirmIcon.setDisplayName(ChatColor.RED + "UNAVAILABLE");
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), new ArrayList<>());
        return;
      }
      if (!MaterialUtil.isHelmet(selectedEquipment)) {
        confirmIcon.setDisplayName(noHelmetMerge);
        lore.addAll(noHelmetMergeLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validHelmetMerge);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);
      lore.addAll(validHelmetMergeLore);
      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (MaterialUtil.isEnchantmentItem(selectedUpgradeItem)) {
      if (!MaterialUtil.hasEnchantmentTag(selectedEquipment)) {
        confirmIcon.setDisplayName(invalidEnchant);
        lore.addAll(noEnchantTagLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      if (!MaterialUtil.isMatchingGroup(MaterialUtil.getEnchantmentItem(selectedUpgradeItem),
          selectedEquipment.getType())) {
        confirmIcon.setDisplayName(invalidEnchant);
        lore.addAll(badTomeTypeLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validEnchant);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);

      EnchantmentTome tome = MaterialUtil.getEnchantmentItem(selectedUpgradeItem);
      if (StringUtils.isNotBlank(tome.getStat())) {
        int itemLevel = MaterialUtil.getLevelRequirement(selectedEquipment);
        double enchantLevel = PlayerDataUtil
            .getEffectiveLifeSkill(player, LifeSkillType.ENCHANTING, true);
        double effectiveLevel = Math.max(1, Math.min(enchantLevel, itemLevel));
        ItemStat stat = LootPlugin.getInstance().getStatManager().getStat(tome.getStat());
        double rarityBonus = MaterialUtil.getBaseEnchantBonus(enchantLevel);

        String minStat = ChatColor.stripColor(
            TextUtils
                .color((plugin.getStatManager().getMinStat(stat, effectiveLevel, rarityBonus))));
        String maxStat = ChatColor.stripColor(TextUtils
            .color(plugin.getStatManager().getMaxStat(stat, effectiveLevel, 1 + rarityBonus)));
        for (String s : validEnchantLore) {
          lore.add(s.replace("{min}", minStat).replace("{max}", maxStat));
        }
      }
      if (!tome.getEnchantments().isEmpty()) {
        lore.add(TextUtils.color("&fApplied Enchantments:"));
        for (Enchantment enchantment : tome.getEnchantments().keySet()) {
          lore.add(TextUtils.color(" &9" + WordUtils.capitalizeFully(enchantment.getKey()
              .getKey()) + " " + NumberUtil.toRoman(tome.getEnchantments().get(enchantment))));
        }
      }

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (MaterialUtil.isExtender(selectedUpgradeItem)) {
      if (!MaterialUtil
          .canBeExtended(new ArrayList<>(ItemStackExtensionsKt.getLore(selectedEquipment)))) {
        confirmIcon.setDisplayName(invalidExtend);
        lore.addAll(invalidExtendLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validExtend);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);
      lore.addAll(validExtendLore);

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (selectedUpgradeItem.isSimilar(ArcaneEnhancer.get())) {
      if (!MaterialUtil.isEnchanted(selectedEquipment)) {
        confirmIcon.setDisplayName(itemNotEnchanted);
        lore.addAll(itemNotEnchantedLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      if (MaterialUtil.isArcaneEnchanted(selectedEquipment)) {
        confirmIcon.setDisplayName(alreadyEnhanced);
        lore.addAll(alreadyEnhancedLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }

      int enchantingLevel = plugin.getStrifePlugin().getChampionManager().getChampion(player)
          .getLifeSkillLevel(LifeSkillType.ENCHANTING);
      if (enchantingLevel < getEnhanceRequirement()) {
        confirmIcon.setDisplayName(noEnhanceLevel);
        lore.addAll(noEnhanceLevelLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validEnhance);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);
      lore.addAll(validEnhanceLore);

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (selectedUpgradeItem.isSimilar(PurifyingScroll.get())) {
      if (!MaterialUtil.isEnchanted(selectedEquipment)) {
        confirmIcon.setDisplayName(itemNotEnchanted);
        lore.addAll(itemNotEnchantedLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validPurity);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);
      lore.addAll(validPurityLore);

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    UpgradeScroll scroll = plugin.getScrollManager().getScroll(selectedUpgradeItem);
    if (scroll != null) {
      if (!MaterialUtil.isUpgradePossible(selectedEquipment)) {
        confirmIcon.setDisplayName(invalidUpgrade);
        lore.addAll(invalidUpgradeLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      String equipName = ItemStackExtensionsKt.getDisplayName(selectedEquipment);
      int itemPlus = MaterialUtil.getUpgradeLevel(equipName);
      if (!MaterialUtil.meetsUpgradeRange(scroll, itemPlus)) {
        confirmIcon.setDisplayName(invalidUpgrade);
        lore.addAll(badScrollRangeLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validUpgrade);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);
      if (selectedEquipment.getType() == Material.BOOK
          || selectedEquipment.getType() == Material.ARROW) {
        itemPlus += 3;
      } else {
        itemPlus += 1;
      }
      itemPlus = Math.min(itemPlus, 15);
      double successChance = Math.min(100, 100 * MaterialUtil.getSuccessChance(player, itemPlus,
          selectedUpgradeItem, scroll));
      double maxDura = selectedEquipment.getType().getMaxDurability();
      double maxPercent = MaterialUtil.getMaxFailureDamagePercent(scroll, itemPlus);
      double maxDamage = maxDura * maxPercent;
      double damage = selectedEquipment.getDurability();
      double killChance = 0;
      double damageChance = 0;
      if (successChance < 99.9) {
        double failChance = 100 - successChance;
        if (maxDura <= 1) {
          killChance = Math.max(0, maxPercent - 1) * 100;
        } else {
          killChance = failChance * Math.max(0, ((damage + maxDamage) - maxDura) / maxDamage);
        }
        damageChance = failChance - killChance;
      }

      for (String s : validUpgradeLore) {
        lore.add(s.replace("{succ}", DF.format(successChance))
            .replace("{dam}", DF.format(damageChance))
            .replace("{ded}", DF.format(killChance)));
      }

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
    }
  }

  boolean doUpgrade(Player player) {
    if (selectedEquipment == null || selectedEquipment.getType() == Material.AIR) {
      player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 0.8f);
      return false;
    }
    if (MaterialUtil.isNormalHead(selectedUpgradeItem) && MaterialUtil
        .isHelmet(selectedEquipment)) {
      if (plugin.getPlayerPointsAPI() == null) {
        MessageUtils.sendMessage(player, "&eSorry! FaceGems are on strike?!");
        return false;
      }
      MaterialUtil.convertToHead(player, selectedUpgradeItem, selectedEquipment);
    } else if (MaterialUtil.isEnchantmentItem(selectedUpgradeItem)) {
      MaterialUtil.enchantItem(player, selectedUpgradeItem, selectedEquipment);
    } else if (plugin.getScrollManager().getScroll(selectedUpgradeItem) != null) {
      MaterialUtil.upgradeItem(player, selectedUpgradeItem, selectedEquipment);
    } else if (MaterialUtil.isExtender(selectedUpgradeItem)) {
      MaterialUtil.extendItem(player, selectedEquipment, selectedUpgradeItem);
    } else if (selectedUpgradeItem.isSimilar(ArcaneEnhancer.get())) {
      MaterialUtil.enhanceEnchantment(player, selectedEquipment, selectedUpgradeItem);
    } else if (selectedUpgradeItem.isSimilar(PurifyingScroll.get())) {
      MaterialUtil.purifyItem(player, selectedEquipment, selectedUpgradeItem);
    } else {
      player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 0.8f);
      return false;
    }
    setSelectedUpgradeItem(player, selectedUpgradeItem);
    setSelectedEquipment(player, selectedEquipment);
    return true;
  }

  ItemStack getBlankItem() {
    return blankItem;
  }

  private int getEnhanceRequirement() {
    int tier = (int) Math.floor(((double) MaterialUtil.getLevelRequirement(selectedEquipment)) / 10);
    return baseEnhanceRequirement + tier * enhanceReqPerTwenty;
  }

}

/*
00 01 02 03 04 05 06 07 08
09 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
*/
