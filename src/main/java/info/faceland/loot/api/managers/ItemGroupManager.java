/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.api.managers;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.tier.Tier;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;

public interface ItemGroupManager {

    Set<ItemGroup> getItemGroups();

    void addItemGroup(ItemGroup itemGroup);

    void removeItemGroup(String name);

    ItemGroup getItemGroup(String name);

    void addMaterialGroup(Material material, Set<Tier> tierIds);

    void removeMaterialGroup(Material material);

    Set<Tier> getMaterialGroup(Material material);

    Set<ItemGroup> getMatchingItemGroups(Material m);

    Map<Material, Set<Tier>> getMaterialGroups();

}
