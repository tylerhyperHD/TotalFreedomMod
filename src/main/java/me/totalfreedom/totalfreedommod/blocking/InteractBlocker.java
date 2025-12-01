package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractBlocker extends FreedomService
{

    public InteractBlocker(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        switch (event.getAction())
        {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
            {
                handleRightClick(event);
                break;
            }

            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
            {
                //
                break;
            }
        }
    }

    private void handleRightClick(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final Material material = event.getMaterial();

        // Check if material is any type of sign
        if (material == Material.OAK_SIGN || material == Material.OAK_WALL_SIGN ||
            material == Material.SPRUCE_SIGN || material == Material.SPRUCE_WALL_SIGN ||
            material == Material.BIRCH_SIGN || material == Material.BIRCH_WALL_SIGN ||
            material == Material.JUNGLE_SIGN || material == Material.JUNGLE_WALL_SIGN ||
            material == Material.ACACIA_SIGN || material == Material.ACACIA_WALL_SIGN ||
            material == Material.DARK_OAK_SIGN || material == Material.DARK_OAK_WALL_SIGN ||
            material == Material.CRIMSON_SIGN || material == Material.CRIMSON_WALL_SIGN ||
            material == Material.WARPED_SIGN || material == Material.WARPED_WALL_SIGN ||
            material == Material.MANGROVE_SIGN || material == Material.MANGROVE_WALL_SIGN ||
            material == Material.CHERRY_SIGN || material == Material.CHERRY_WALL_SIGN ||
            material == Material.BAMBOO_SIGN || material == Material.BAMBOO_WALL_SIGN)
        {
            player.sendMessage(ChatColor.GRAY + "Sign interaction is currently disabled.");
            return;
        }

        switch (material)
        {
            case WATER_BUCKET:
            {
                if (plugin.al.isAdmin(player) || ConfigEntry.ALLOW_WATER_PLACE.getBoolean())
                {
                    break;
                }

                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Water buckets are currently disabled.");
                event.setCancelled(true);
                break;
            }

            case LAVA_BUCKET:
            {
                if (plugin.al.isAdmin(player) || ConfigEntry.ALLOW_LAVA_PLACE.getBoolean())
                {
                    break;
                }

                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Lava buckets are currently disabled.");
                event.setCancelled(true);
                break;
            }

            case TNT_MINECART:
            {
                if (ConfigEntry.ALLOW_TNT_MINECARTS.getBoolean())
                {
                    break;
                }

                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                player.sendMessage(ChatColor.GRAY + "TNT minecarts are currently disabled.");
                event.setCancelled(true);
                break;
            }
        }
    }
}
