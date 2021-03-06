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
package info.faceland.loot.tier;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.tier.TierBuilder;
import info.faceland.loot.data.ItemStat;
import java.util.List;
import java.util.Set;
import land.face.market.data.PlayerMarketState.FilterFlagA;

public final class LootTierBuilder implements TierBuilder {

  private boolean built = false;
  private Tier tier;

  public LootTierBuilder(String id) {
    tier = new Tier(id);
  }

  @Override
  public boolean isBuilt() {
    return built;
  }

  @Override
  public Tier build() {
    if (isBuilt()) {
      throw new IllegalStateException("already built");
    }
    built = true;
    return tier;
  }

  @Override
  public TierBuilder withLevelRequirement(boolean b) {
    tier.setLevelRequirement(b);
    return this;
  }

  @Override
  public TierBuilder withName(String s) {
    tier.setName(s);
    return this;
  }

  @Override
  public TierBuilder withPrimaryStat(ItemStat itemStat) {
    tier.setPrimaryStat(itemStat);
    return this;
  }

  @Override
  public TierBuilder withSecondaryStats(List<ItemStat> itemStats) {
    tier.setSecondaryStats(itemStats);
    return this;
  }

  @Override
  public TierBuilder withBonusStats(List<ItemStat> itemStats) {
    tier.setBonusStats(itemStats);
    return this;
  }

  @Override
  public TierBuilder withSpecialStats(List<ItemStat> itemStats) {
    tier.setSpecialStats(itemStats);
    return this;
  }

  @Override
  public TierBuilder withSpawnWeight(double d) {
    tier.setSpawnWeight(d);
    return this;
  }

  @Override
  public TierBuilder withIdentifyWeight(double d) {
    tier.setIdentifyWeight(d);
    return this;
  }

  @Override
  public TierBuilder withStartingCustomData(int i) {
    tier.setCustomDataStart(i);
    return this;
  }

  @Override
  public TierBuilder withCustomDataInterval(int i) {
    tier.setCustomDataInterval(i);
    return this;
  }

  @Override
  public TierBuilder withFilterFlag(FilterFlagA filterFlag) {
    tier.setFilterFlag(filterFlag);
    return this;
  }

  @Override
  public TierBuilder withItemGroups(Set<ItemGroup> s) {
    tier.setItemGroups(s);
    return this;
  }
}
