package com.chaseoes.firstjoinplus;

import com.chaseoes.firstjoinplus.utilities.Utilities;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FirstJoinListener implements Listener {

    @EventHandler
    public void onFirstJoin(final FirstJoinEvent event) {
        final Player player = event.getPlayer();

        FirstJoinPlus.getInstance().getServer().getScheduler().runTaskLater(FirstJoinPlus.getInstance(), new Runnable() {
            public void run() {

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.first-join-kit.enabled")) {
                    for (ItemStack i : Utilities.getFirstJoinKit()) {
                        player.getInventory().addItem(i);
                    }
                }

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.give-written-books.enabled")) {
                    for (ItemStack i : Utilities.getWrittenBooks(player)) {
                        player.getInventory().addItem(i);
                    }
                }

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.give-experience.enabled")) {
                    player.setLevel(FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.give-experience.level-amount"));
                }

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.send-messages.enabled")) {
                    for (String message : FirstJoinPlus.getInstance().getConfig().getStringList("on-first-join.send-messages.messages")) {
                        player.sendMessage(Utilities.replaceVariables(message, player));
                    }
                }

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.fun-stuff.play-sound.enabled")) {
                    for (Player p : FirstJoinPlus.getInstance().getServer().getOnlinePlayers()) {
                        if (p.hasPermission(FirstJoinPlus.getInstance().getConfig().getString("on-first-join.fun-stuff.play-sound.listen-permission"))) {
                            Sound s = Sound.valueOf(FirstJoinPlus.getInstance().getConfig().getString("on-first-join.fun-stuff.play-sound.sound-name").toUpperCase());
                            p.playSound(p.getLocation(), s, 1, 1);
                        }
                    }
                }

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.run-commands.enabled")) {
                    for (String command : FirstJoinPlus.getInstance().getConfig().getStringList("on-first-join.run-commands.commands")) {
                        String cmnd = Utilities.replaceVariables(command, player);
                        player.performCommand(cmnd);
                    }
                }

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.run-console-commands.enabled")) {
                    for (String command : FirstJoinPlus.getInstance().getConfig().getStringList("on-first-join.run-console-commands.commands")) {
                        String cmnd = Utilities.replaceVariables(command, player);
                        FirstJoinPlus.getInstance().getServer().dispatchCommand(FirstJoinPlus.getInstance().getServer().getConsoleSender(), cmnd);
                    }
                }

                if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.apply-potion-effects.enabled")) {
                    List<PotionEffect> effects = new ArrayList<PotionEffect>();
                    for (String s : FirstJoinPlus.getInstance().getConfig().getStringList("on-first-join.apply-potion-effects.effects")) {
                        String[] effect = s.split("\\:");
                        effects.add(new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()), Integer.parseInt(effect[2]) * 20, (Integer.parseInt(effect[1])) - 1));
                    }
                    player.addPotionEffects(effects);
                }
            }
        }, FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.delay-everything-below-by"));
    }

}
