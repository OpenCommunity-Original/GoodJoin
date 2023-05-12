package com.chaseoes.firstjoinplus;

import com.chaseoes.firstjoinplus.utils.FormatUtil;
import com.chaseoes.firstjoinplus.utils.LocaleAPI;
import com.chaseoes.firstjoinplus.utils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
                    List<String> messages = LocaleAPI.getMessagesList(player, "welcome_message");
                    for (String message : messages) {
                        if (message.contains("[button:")) {
                            // Extract the link, text, and button properties
                            int startIndex = message.indexOf("[button:") + 8;
                            int endIndex = message.indexOf(", ", startIndex);
                            String link = message.substring(startIndex, endIndex);
                            startIndex = message.indexOf("format:", endIndex);
                            endIndex = message.indexOf(", ", startIndex);
                            TextDecoration decoration = TextDecoration.valueOf(message.substring(startIndex + 7, endIndex));
                            startIndex = message.indexOf("color:", endIndex);
                            endIndex = message.indexOf(", ", startIndex);
                            TextColor color = TextColor.fromHexString(message.substring(startIndex + 6, endIndex));
                            String text = message.substring(message.lastIndexOf(", ") + 3, message.length() - 2);

                            // Create the button TextComponent
                            TextComponent button = Component.text(text)
                                    .decoration(decoration, true)
                                    .color(color)
                                    .clickEvent(ClickEvent.openUrl(link))
                                    .hoverEvent(HoverEvent.showText(Component.text(text)));

                            // Create the message TextComponent
                            TextComponent textComponent = Component.text()
                                    .content("")
                                    .build();

                            // Append the button to the message
                            textComponent = textComponent.append(button);

                            // Send the message with the appended button
                            player.sendMessage(textComponent);
                        } else {
                            // Create the message TextComponent
                            TextComponent textComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(message);

                            // Send the message as a regular chat message
                            player.sendMessage(textComponent);
                        }
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
