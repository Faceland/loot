/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.items;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.managers.NameManager;
import info.faceland.loot.api.managers.RarityManager;
import info.faceland.loot.data.BuiltItem;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.data.StatResponse;
import info.faceland.loot.managers.StatManager;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.tier.Tier;
import info.faceland.loot.utils.MaterialUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class LootItemBuilder implements ItemBuilder {

  private final StatManager statManager;
  private final RarityManager rarityManager;
  private final NameManager nameManager;

  private boolean built = false;
  private boolean specialStat;
  private Tier tier;
  private ItemRarity rarity;
  private int level;
  private Material material;
  private ItemGenerationReason itemGenerationReason = ItemGenerationReason.MONSTER;
  private LootRandom random = new LootRandom();

  private double specialStatChance;

  public static AttributeModifier MINUS_ONE_KB_RESIST = new AttributeModifier("NO_KB_RESIST",
      -1, Operation.ADD_NUMBER);

  public LootItemBuilder(LootPlugin plugin) {
    statManager = plugin.getStatManager();
    rarityManager = plugin.getRarityManager();
    nameManager = plugin.getNameManager();

    specialStatChance = plugin.getSettings()
        .getDouble("config.special-stats.pool-chance", 0.5D);
  }

  @Override
  public boolean isBuilt() {
    return built;
  }

  @Override
  public BuiltItem build() {
    if (isBuilt()) {
      throw new IllegalStateException("already built");
    }
    built = true;
    ItemStack stack;
    if (material == null) {
      Set<Material> set = tier.getAllowedMaterials();
      Material[] array = set.toArray(new Material[set.size()]);
      if (set.size() == 0) {
        throw new RuntimeException("array length is 0 for tier: " + tier.getName());
      }
      material = array[random.nextInt(array.length)];
    }
    stack = new ItemStack(material);
    List<String> lore = new ArrayList<>();

    lore.add(ChatColor.WHITE + "Level Requirement: " + level);
    lore.add(ChatColor.WHITE + "Tier: " + rarity.getColor() + rarity.getName() + " " + tier.getName());

    lore.add(statManager.getFinalStat(tier.getPrimaryStat(), level, rarity.getPower(), false).getStatString());
    lore.add(statManager.getFinalStat(getRandomSecondaryStat(), level, rarity.getPower(), false).getStatString());

    List<ItemStat> bonusStatList = new ArrayList<>(tier.getBonusStats());

    if (specialStat) {
      ItemStat stat;
      if (tier.getSpecialStats().size() > 0 && random.nextDouble() < specialStatChance) {
        stat = getRandomSpecialStat();
      } else {
        stat = bonusStatList.get(random.nextInt(bonusStatList.size()));
      }
      StatResponse rStat = statManager.getFinalStat(stat, level, rarity.getPower(), true);
      lore.add(rStat.getStatString());
    }

    int bonusStats = random.nextIntRange(rarity.getMinimumBonusStats(), rarity.getMaximumBonusStats());
    String prefix = nameManager.getRandomPrefix();
    float roll = 0;
    boolean statPrefix = random.nextDouble() > 0.35;
    for (int i = 0; i < bonusStats; i++) {
      ItemStat stat = bonusStatList.get(random.nextInt(bonusStatList.size()));
      StatResponse rStat = statManager.getFinalStat(stat, level, rarity.getPower(), false);
      lore.add(rStat.getStatString());
      bonusStatList.remove(stat);
      if (StringUtils.isNotBlank(rStat.getStatPrefix())) {
        if (statPrefix && rStat.getStatRoll() > 0.5 && rStat.getStatRoll() > roll) {
          roll = rStat.getStatRoll();
          prefix = rStat.getStatPrefix();
        }
      }
    }

    for (int i = 0; i < rarity.getEnchantments(); i++) {
      lore.add(ChatColor.BLUE + "(Enchantable)");
    }

    int sockets = random.nextIntRange(rarity.getMinimumSockets(), rarity.getMaximumSockets());
    for (int i = 0; i < sockets; i++) {
      lore.add(ChatColor.GOLD + "(Socket)");
    }

    for (int i = 0; i < rarity.getExtenderSlots(); i++) {
      lore.add(ChatColor.DARK_AQUA + "(+)");
    }

    String suffix;
    boolean statSuffix = random.nextDouble() > 0.35;
    if (!statSuffix || tier.getItemSuffixes().size() == 0) {
      suffix = nameManager.getRandomSuffix();
    } else {
      suffix = tier.getItemSuffixes().get(random.nextInt(tier.getItemSuffixes().size()));
    }

    ItemStackExtensionsKt.setDisplayName(stack, rarity.getColor() + prefix + " " + suffix);
    ItemStackExtensionsKt.setLore(stack, lore);
    ItemStackExtensionsKt.addItemFlags(stack, ItemFlag.HIDE_ATTRIBUTES);

    switch (material) {
      case NETHERITE_HELMET:
      case NETHERITE_CHESTPLATE:
      case NETHERITE_LEGGINGS:
      case NETHERITE_BOOTS:
        ItemMeta iMeta = stack.getItemMeta();
        iMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
            LootItemBuilder.MINUS_ONE_KB_RESIST);
        stack.setItemMeta(iMeta);
    }

    MaterialUtil.applyTierLevelData(stack, tier, level);

    return new BuiltItem(stack, rarity.getLivedTicks());
  }

  @Override
  public ItemBuilder withRarity(ItemRarity r) {
    rarity = r;
    return this;
  }

  @Override
  public ItemBuilder withLevel(int l) {
    level = l;
    return this;
  }

  @Override
  public ItemBuilder withSpecialStat(boolean b) {
    specialStat = b;
    return this;
  }

  @Override
  public ItemBuilder withTier(Tier t) {
    tier = t;
    return this;
  }

  @Override
  public ItemBuilder withMaterial(Material m) {
    material = m;
    return this;
  }

  @Override
  public ItemBuilder withItemGenerationReason(ItemGenerationReason reason) {
    itemGenerationReason = reason;
    if (itemGenerationReason == ItemGenerationReason.IDENTIFYING) {
      double totalWeight = 0D;
      for (ItemRarity rarity : rarityManager.getLoadedRarities().values()) {
        totalWeight += rarity.getIdWeight();
      }
      double chosenWeight = random.nextDouble() * totalWeight;
      double currentWeight = 0D;
      for (ItemRarity rarity : rarityManager.getLoadedRarities().values()) {
        currentWeight += rarity.getIdWeight();
        if (currentWeight >= chosenWeight) {
          this.rarity = rarity;
        }
      }
    }
    return this;
  }

  private ItemStat getRandomSecondaryStat() {
    return tier.getSecondaryStats().get(random.nextInt(tier.getSecondaryStats().size()));
  }

  private ItemStat getRandomSpecialStat() {
    return tier.getSpecialStats().get(random.nextInt(tier.getSpecialStats().size()));
  }
}
