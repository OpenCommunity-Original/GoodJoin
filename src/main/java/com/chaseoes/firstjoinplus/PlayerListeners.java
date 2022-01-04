package com.chaseoes.firstjoinplus;

import com.chaseoes.firstjoinplus.utilities.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListeners implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void firstJoinDetection(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean existingPlayer = player.hasPlayedBefore();
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("settings.every-join-is-first-join")) {
            existingPlayer = false;
        }

        if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.reset-state.enabled")) {
            Utilities.debugPlayer(player, false);
        }

        if (!existingPlayer) {
            FirstJoinPlus.getInstance().getServer().getPluginManager().callEvent(new FirstJoinEvent(event));
            return;
        }
    }

}
