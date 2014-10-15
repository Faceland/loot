/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

package info.faceland.loot.listeners;

import com.google.common.base.CharMatcher;
import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.items.prefabs.UpgradeScroll;
import info.faceland.loot.math.LootRandom;
import info.faceland.messaging.Chatty;
import info.faceland.utils.StringConverter;
import info.faceland.utils.StringListUtils;
import info.faceland.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;

import java.util.ArrayList;
import java.util.List;

public final class InteractListener implements Listener {

    private final LootPlugin plugin;
    private LootRandom random;

    public InteractListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            event.setCancelled(true);
            Chatty.sendMessage((Player) event.getPlayer(), plugin.getSettings().getString("language.enchant.no-open", ""));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCursor() == null
            || event.getCurrentItem().getType() == Material.AIR || event.getCursor().getType() == Material.AIR ||
            !(event.getWhoClicked() instanceof Player) || event.getClick() != ClickType.RIGHT) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        HiltItemStack currentItem = new HiltItemStack(event.getCurrentItem());
        HiltItemStack cursor = new HiltItemStack(event.getCursor());

        if (cursor.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
            String gemName = ChatColor.stripColor(cursor.getName().replace(ChatColor.GOLD + "Socket Gem - ", ""));
            SocketGem gem = plugin.getSocketGemManager().getSocketGem(gemName);

            if (gem == null) {
                return;
            }

            if (!plugin.getItemGroupManager().getMatchingItemGroups(currentItem.getType()).containsAll(
                    gem.getItemGroups())) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.socket.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }

            List<String> lore = currentItem.getLore();
            List<String> strippedLore = StringListUtils.stripColor(lore);
            if (!strippedLore.contains("(Socket)")) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.socket.needs-sockets", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = strippedLore.indexOf("(Socket)");

            lore.remove(index);
            lore.addAll(index, TextUtils.color(gem.getLore()));

            currentItem.setLore(lore);

            // strip color, check against that
            // k
            String name = currentItem.getName();
            int level = getLevel(name);
            name = name.replace("+" + level + " ", "");
            ChatColor start = getFirstColor(name);
            String format = "%s%s%s";
            name = String.format(format, start + (level > 0 ? "+" + level + " " : "") +
                                         (!gem.getPrefix().isEmpty() ? gem.getPrefix() + " " : ""),
                                 name + (!gem.getSuffix().isEmpty() ? " " : ""),
                                 start + gem.getSuffix() + ChatColor.getLastColors(name));
            currentItem.setName(TextUtils.color(name));

            Chatty.sendMessage(player, plugin.getSettings().getString("language.socket.success", ""));
            player.playSound(player.getEyeLocation(), Sound.ORB_PICKUP, 1L, 2.0F);
        } else if (cursor.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ")) {
            String stoneName = ChatColor.stripColor(
                    cursor.getName().replace(ChatColor.BLUE + "Enchantment Tome - ", ""));
            EnchantmentTome stone = plugin.getEnchantmentStoneManager().getEnchantmentStone(stoneName);

            if (!isBlockWithinRadius(Material.ENCHANTMENT_TABLE, event.getWhoClicked().getLocation(), 5)) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.enchant.no-enchantment-table", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }

            if (stone == null) {
                return;
            }

            if (!plugin.getItemGroupManager().getMatchingItemGroups(currentItem.getType()).containsAll(
                    stone.getItemGroups())) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.enchant.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }

            List<String> lore = currentItem.getLore();
            List<String> strippedLore = StringListUtils.stripColor(lore);
            if (!strippedLore.contains("(Enchantable)")) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.enchant.needs-enchantable", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = strippedLore.indexOf("(Enchantable)");

            List<String> added = new ArrayList<>();
            for (int i = 0; i < random.nextIntRange(stone.getMinStats(), stone.getMaxStats()); i++) {
                added.add(stone.getLore().get(random.nextInt(stone.getLore().size())));
            }

            lore.remove(index);
            lore.addAll(index, TextUtils.color(added));

            currentItem.setLore(lore);

            currentItem.addUnsafeEnchantments(stone.getEnchantments());

            Chatty.sendMessage(player, plugin.getSettings().getString("language.enchant.success", ""));
            player.playSound(player.getEyeLocation(), Sound.PORTAL_TRAVEL, 1L, 2.0F);
        } else if (cursor.getName().equals(ChatColor.DARK_AQUA + "Socket Extender")) {
            List<String> lore = currentItem.getLore();
            List<String> stripColor = StringListUtils.stripColor(lore);
            if (!stripColor.contains("(+)")) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.extend.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = stripColor.indexOf("(+)");
            lore.set(index, ChatColor.GOLD + "(Socket)");
            currentItem.setLore(lore);

            Chatty.sendMessage(player, plugin.getSettings().getString("language.extend.success", ""));
            player.playSound(player.getEyeLocation(), Sound.PORTAL_TRAVEL, 1L, 2.0F);
        } else if (cursor.getName().equals(ChatColor.DARK_PURPLE + "Identity Tome")) {
            if (!currentItem.getName().equals(ChatColor.WHITE + "Unidentified Item")) {
                return;
            }
            Material m = currentItem.getType();
            currentItem = plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.IDENTIFYING)
                                .withMaterial(m).build();

            Chatty.sendMessage(player, plugin.getSettings().getString("language.identify.success", ""));
            player.playSound(player.getEyeLocation(), Sound.PORTAL_TRAVEL, 1L, 2.0F);
        } else if (cursor.getName().endsWith("Upgrade Scroll")) {
            if (currentItem.getName().equals(ChatColor.DARK_AQUA + "Socket Extender") ||
                currentItem.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ") ||
                currentItem.getName().startsWith(ChatColor.GOLD + "Socket Gem -") ||
                currentItem.getName().equals(ChatColor.AQUA + "Charm of Protection")) {
                return;
            }
            String name = ChatColor.stripColor(cursor.getName().replace("Upgrade Scroll", "")).trim();
            UpgradeScroll.ScrollType type = UpgradeScroll.ScrollType.getByName(name);
            if (type == null) {
                return;
            }
            name = currentItem.getName();
            int level = getLevel(name), lev = level;
            if (level < type.getMinimumLevel() || level > type.getMaximumLevel()) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.upgrade.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            boolean succeed = false;
            List<String> strip = StringListUtils.stripColor(currentItem.getLore());
            for (String s : strip) {
                if (s.startsWith("+")) {
                    succeed = true;
                    break;
                }
            }
            if (!succeed) {
                return;
            }
//            int index = InventoryUtil.firstAtLeast(player.getInventory(), new ProtectionCharm(), 1);
            if (random.nextDouble() < type.getChanceToDestroy()) {
//                if (index == -1) {
                Chatty.sendMessage(player, plugin.getSettings().getString("language.upgrade.destroyed", ""));
                player.playSound(player.getEyeLocation(), Sound.ITEM_BREAK, 1F, 1F);
                currentItem = null;
//                } else {
//                    ItemStack inInv = player.getInventory().getItem(index);
//                    inInv.setAmount(inInv.getAmount() - 1);
//                    player.getInventory().setItem(index, inInv.getAmount() > 0 ? inInv : null);
//                    Chatty.sendMessage(player, plugin.getSettings().getString("language.upgrade.failure", ""));
//                    Chatty.sendMessage(player, plugin.getSettings().getString("language.upgrade.consumed", ""));
//                    player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
//                }
//                succeed = false;
            }
            if (currentItem != null) {
                if (level == 0) {
                    level++;
                    name = getFirstColor(name) + ("+" + level) + " " + name;
                    currentItem.setName(name);
                } else {
                    level++;
                    name = name.replace("+" + lev, "+" + String.valueOf(level));
                    currentItem.setName(name);
                    if (level >= 7 && currentItem.getEnchantments().isEmpty()) {
                        currentItem.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
                    }
                }
                List<String> lore = currentItem.getLore();
                for (int i = 0; i < lore.size(); i++) {
                    String s = lore.get(i);
                    String ss = ChatColor.stripColor(s);
                    if (!ss.startsWith("+")) {
                        continue;
                    }
                    String loreLev = CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(ss, ' ').split(" ")[0];
                    int loreLevel = StringConverter.toInt(loreLev) + 1;
                    lore.set(i, s.replace("+" + loreLev, "+" + loreLevel));
                    break;
                }
                currentItem.setLore(lore);
                Chatty.sendMessage(player, plugin.getSettings().getString("language.upgrade.success", ""));
                player.playSound(player.getEyeLocation(), Sound.LEVEL_UP, 1F, 2F);
            }
        } else {
            return;
        }

        event.setCurrentItem(currentItem);
        cursor.setAmount(cursor.getAmount() - 1);
        event.setCursor(cursor.getAmount() == 0 ? null : cursor);
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        player.updateInventory();
    }

    private boolean isBlockWithinRadius(Material material, Location location, int radius) {
        int minX = location.getBlockX() - radius;
        int maxX = location.getBlockX() + radius;
        int minY = location.getBlockY() - radius;
        int maxY = location.getBlockY() + radius;
        int minZ = location.getBlockZ() - radius;
        int maxZ = location.getBlockZ() + radius;
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ChatColor getFirstColor(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (!s.substring(i, i + 1).equals(ChatColor.COLOR_CHAR + "")) {
                continue;
            }
            ChatColor c = ChatColor.getByChar(s.substring(i + 1, i + 2));
            if (c != null) {
                return c;
            }
        }
        return ChatColor.RESET;
    }

    private int getLevel(String name) {
        String lev = CharMatcher.DIGIT.or(CharMatcher.is('-')).retainFrom(ChatColor.stripColor(name));
        return StringConverter.toInt(lev, 0);
    }

}
