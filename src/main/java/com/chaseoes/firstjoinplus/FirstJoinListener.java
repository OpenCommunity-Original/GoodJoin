package com.chaseoes.firstjoinplus;

import com.chaseoes.firstjoinplus.utils.BookUtil;
import com.chaseoes.firstjoinplus.utils.LocaleAPI;
import com.chaseoes.firstjoinplus.utils.Utilities;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FirstJoinListener implements Listener {

    public static void openBook(Player player) {
        Book book = Book.builder()
                .author(LegacyComponentSerializer.legacyAmpersand().deserialize(""))
                .title(LegacyComponentSerializer.legacyAmpersand().deserialize(""))
                .addPage(Component.text(LocaleAPI.getMessage(player, "rules")))
                .build();

        player.openBook(book);
    }

    @EventHandler
    public void onFirstJoin(final FirstJoinEvent event) {
        final Player player = event.getPlayer();

        FirstJoinPlus.getInstance().getServer().getScheduler().runTaskLater(FirstJoinPlus.getInstance(), () -> {

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
                        String buttonType = message.substring(startIndex, endIndex);
                        String buttonProperties = message.substring(endIndex + 2, message.length() - 1);

                        String link = null;
                        String command = null;
                        String book = null;
                        TextDecoration decoration = null;
                        TextColor color = null;
                        String text = null;

                        // Parse the button properties based on the button type
                        if (buttonType.startsWith("http")) {
                            link = buttonType;
                        } else if (buttonType.startsWith("command")) {
                            command = buttonType.substring(buttonType.indexOf(":") + 1);
                            command = command.replace("%player%", player.getName());
                            System.out.println("DEBUG: " + command);
                        } else if (buttonType.startsWith("book")) {
                            book = buttonType.substring(buttonType.indexOf(":") + 1);
                        }

                        String[] properties = buttonProperties.split(", ");
                        for (String property : properties) {
                            if (property.startsWith("format:")) {
                                decoration = TextDecoration.valueOf(property.substring(7));
                            } else if (property.startsWith("color:")) {
                                color = TextColor.fromHexString(property.substring(6));
                            } else {
                                text = property;
                            }
                        }

                        // Create the button TextComponent
                        TextComponent button = null;
                        if (link != null) {
                            button = Component.text(text)
                                    .decoration(decoration, true)
                                    .color(color)
                                    .clickEvent(ClickEvent.openUrl(link))
                                    .hoverEvent(HoverEvent.showText(Component.text(text)));
                        } else if (command != null) {
                            String finalCommand = command;
                            button = Component.text(text)
                                    .decoration(decoration, true)
                                    .color(color)
                                    .clickEvent(ClickEvent.callback(f -> player.getServer().dispatchCommand(player.getServer().getConsoleSender(), finalCommand), ClickCallback.Options.builder().lifetime(Duration.ofSeconds(300)).build()))
                                    .hoverEvent(HoverEvent.showText(Component.text(text)));
                        } else if (book != null) {
                            button = Component.text(text)
                                    .decoration(decoration, true)
                                    .color(color)
                                    .clickEvent(ClickEvent.callback(f -> BookUtil.openBook(player), ClickCallback.Options.builder().uses(20).build()))
                                    .hoverEvent(HoverEvent.showText(Component.text(text)));
                        }

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
                List<PotionEffect> effects = new ArrayList<>();
                for (String s : FirstJoinPlus.getInstance().getConfig().getStringList("on-first-join.apply-potion-effects.effects")) {
                    String[] effect = s.split("\\:");
                    effects.add(new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()), Integer.parseInt(effect[2]) * 20, (Integer.parseInt(effect[1])) - 1));
                }
                player.addPotionEffects(effects);
            }
        }, FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.delay-everything-below-by"));
    }

}
