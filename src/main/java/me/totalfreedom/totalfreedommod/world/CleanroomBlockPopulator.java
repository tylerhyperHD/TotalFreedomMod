/*
 * Cleanroom Generator
 * Copyright (C) 2011-2012 nvx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.totalfreedom.totalfreedommod.world;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class CleanroomBlockPopulator extends BlockPopulator
{

    byte[] layerDataValues;

    protected CleanroomBlockPopulator(byte[] layerDataValues)
    {
        this.layerDataValues = layerDataValues;
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion)
    {
        if (layerDataValues != null)
        {
            int x = chunkX << 4;
            int z = chunkZ << 4;

            for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight() && y < layerDataValues.length; y++)
            {
                byte dataValue = layerDataValues[y];
                if (dataValue == 0)
                {
                    continue;
                }
                for (int xx = 0; xx < 16; xx++)
                {
                    for (int zz = 0; zz < 16; zz++)
                    {
                        int blockX = x + xx;
                        int blockZ = z + zz;
                        if (limitedRegion.isInRegion(blockX, y, blockZ))
                        {
                            BlockData blockData = limitedRegion.getBlockData(blockX, y, blockZ);
                            if (blockData.getMaterial() != Material.AIR)
                            {
                                limitedRegion.setBlockData(blockX, y, blockZ, blockData.getMaterial().createBlockData());
                            }
                        }
                    }
                }
            }
        }
    }
}
