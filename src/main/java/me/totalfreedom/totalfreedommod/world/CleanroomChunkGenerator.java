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

import static java.lang.System.arraycopy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class CleanroomChunkGenerator extends ChunkGenerator
{

    private static final Logger log = Bukkit.getLogger();
    private short[] layer;
    private byte[] layerDataValues;
    private boolean generateBedrockLayer;

    public CleanroomChunkGenerator()
    {
        this("64,stone");
    }

    public CleanroomChunkGenerator(String id)
    {
        if (id != null)
        {
            try
            {
                int y = 0;

                layer = new short[128]; // Default to 128, will be resized later if required
                layerDataValues = null;

                if ((!id.isEmpty()) && (id.charAt(0) == '.')) // Is the first character a '.'? If so, skip bedrock generation.
                {
                    id = id.substring(1); // Skip bedrock then and remove the .
                    generateBedrockLayer = false;
                }
                else // Guess not, bedrock at layer0 it is then.
                {
                    layer[y++] = (short) Material.BEDROCK.ordinal();
                    generateBedrockLayer = true;
                }

                if (!id.isEmpty())
                {
                    String[] tokens = id.split(",");

                    if ((tokens.length % 2) != 0)
                    {
                        throw new Exception();
                    }

                    for (int i = 0; i < tokens.length; i += 2)
                    {
                        int height = Integer.parseInt(tokens[i]);
                        if (height <= 0)
                        {
                            log.warning("[CleanroomGenerator] Invalid height '" + tokens[i] + "'. Using 64 instead.");
                            height = 64;
                        }

                        String[] materialTokens = tokens[i + 1].split(":", 2);
                        byte dataValue = 0;
                        if (materialTokens.length == 2)
                        {
                            try
                            {
                                // Lets try to read the data value
                                dataValue = Byte.parseByte(materialTokens[1]);
                            }
                            catch (Exception e)
                            {
                                log.warning("[CleanroomGenerator] Invalid Data Value '" + materialTokens[1] + "'. Defaulting to 0.");
                            }
                        }
                        Material mat = Material.matchMaterial(materialTokens[0]);
                        if (mat == null)
                        {
                            log.warning("[CleanroomGenerator] Invalid Block ID '" + materialTokens[0] + "'. Defaulting to stone.");
                            mat = Material.STONE;
                        }

                        if (!mat.isBlock())
                        {
                            log.warning("[CleanroomGenerator] Error, '" + materialTokens[0] + "' is not a block. Defaulting to stone.");
                            mat = Material.STONE;
                        }

                        if (y + height > layer.length)
                        {
                            short[] newLayer = new short[Math.max(y + height, layer.length * 2)];
                            arraycopy(layer, 0, newLayer, 0, y);
                            layer = newLayer;
                            if (layerDataValues != null)
                            {
                                byte[] newLayerDataValues = new byte[Math.max(y + height, layerDataValues.length * 2)];
                                arraycopy(layerDataValues, 0, newLayerDataValues, 0, y);
                                layerDataValues = newLayerDataValues;
                            }
                        }

                        Arrays.fill(layer, y, y + height, (short) mat.ordinal());
                        if (dataValue != 0)
                        {
                            if (layerDataValues == null)
                            {
                                layerDataValues = new byte[layer.length];
                            }
                            Arrays.fill(layerDataValues, y, y + height, dataValue);
                        }
                        y += height;
                    }
                }

                // Trim to size
                if (layer.length > y)
                {
                    short[] newLayer = new short[y];
                    arraycopy(layer, 0, newLayer, 0, y);
                    layer = newLayer;
                }
                if (layerDataValues != null && layerDataValues.length > y)
                {
                    byte[] newLayerDataValues = new byte[y];
                    arraycopy(layerDataValues, 0, newLayerDataValues, 0, y);
                    layerDataValues = newLayerDataValues;
                }
            }
            catch (Exception e)
            {
                log.severe("[CleanroomGenerator] Error parsing CleanroomGenerator ID '" + id + "'. using defaults '64,1': " + e.toString());
                e.printStackTrace();
                layerDataValues = null;
                layer = new short[65];
                layer[0] = (short) Material.BEDROCK.ordinal();
                Arrays.fill(layer, 1, 65, (short) Material.STONE.ordinal());
                generateBedrockLayer = true;
            }
        }
        else
        {
            layerDataValues = null;
            layer = new short[65];
            layer[0] = (short) Material.BEDROCK.ordinal();
            Arrays.fill(layer, 1, 65, (short) Material.STONE.ordinal());
            generateBedrockLayer = true;
        }
    }

    @Override
    public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData)
    {
        if (!generateBedrockLayer || layer.length == 0)
        {
            return;
        }

        int maxHeight = worldInfo.getMaxHeight();
        int minHeight = worldInfo.getMinHeight();

        // Only generate bedrock at y=0 (or minHeight if it's higher)
        int bedrockY = Math.max(minHeight, 0);
        if (bedrockY < maxHeight && layer[0] == (short) Material.BEDROCK.ordinal())
        {
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    chunkData.setBlock(x, bedrockY, z, Material.BEDROCK);
                }
            }
        }
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData)
    {
        int maxHeight = worldInfo.getMaxHeight();
        int minHeight = worldInfo.getMinHeight();

        if (layer.length > maxHeight)
        {
            log.warning("[CleanroomGenerator] Error, chunk height " + layer.length + " is greater than the world max height (" + maxHeight + "). Trimming to world max height.");
            short[] newLayer = new short[maxHeight];
            arraycopy(layer, 0, newLayer, 0, Math.min(layer.length, maxHeight));
            layer = newLayer;
        }

        // Start from 1 if bedrock is at index 0, otherwise start from 0
        int startY = (generateBedrockLayer && layer.length > 0 && layer[0] == (short) Material.BEDROCK.ordinal()) ? 1 : 0;

        for (int y = Math.max(minHeight, startY); y < layer.length && y < maxHeight; y++)
        {
            Material material = Material.values()[layer[y]];
            // Skip bedrock as it's handled in generateBedrock()
            if (y == 0 && material == Material.BEDROCK)
            {
                continue;
            }
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    chunkData.setBlock(x, y, z, material);
                }
            }
        }
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world)
    {
        if (layerDataValues != null)
        {
            return List.of((BlockPopulator) new CleanroomBlockPopulator(layerDataValues));
        }
        else
        {
            // This is the default, but just in case default populators change to stock minecraft populators by default...
            return new ArrayList<>();
        }
    }

    @Override
    public Location getFixedSpawnLocation(World world, @NotNull Random random)
    {
        if (!world.isChunkLoaded(0, 0))
        {
            world.loadChunk(0, 0);
        }

        if ((world.getHighestBlockYAt(0, 0) <= 0) && (world.getBlockAt(0, 0, 0).getType() == Material.AIR)) // SPACE!
        {
            return new Location(world, 0, 64, 0); // Lets allow people to drop a little before hitting the void then shall we?
        }

        return new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
    }
}
