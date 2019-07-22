/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.api.sockets;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SocketGem extends Comparable<SocketGem> {
    String getName();

    double getWeight();

    String getPrefix();

    String getSuffix();

    List<String> getLore();

    List<SocketEffect> getSocketEffects();

    List<ItemGroup> getItemGroups();

    ItemStack toItemStack(int amount);

    double getDistanceWeight();

    double getWeightPerLevel();

    boolean isBroadcast();

    boolean isTriggerable();

    String getTriggerText();

    double getBonusWeight();

    GemType getGemType();

    enum GemType {
        ON_HIT, ON_KILL, WHEN_HIT, ON_SNEAK, ON_CRIT, ON_EVADE;

        public static GemType fromName(String name) {
            for (GemType gemType : values()) {
                if (gemType.name().equals(name)) {
                    return gemType;
                }
            }
            return ON_HIT;
        }
    }
}
