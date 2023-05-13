package com.chaseoes.firstjoinplus.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookUtil {

    // Regular expression to match URLs in the message
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+)");

    public static void openBook(Player player) {
        String message = LocaleAPI.getMessage(player, "rules");
        JsonArray pagesJson = JsonParser.parseString(message).getAsJsonArray();

        List<Component> pages = new ArrayList<>();
        for (JsonElement pageJson : pagesJson) {
            JsonObject pageObject = pageJson.getAsJsonObject();
            ComponentBuilder pageBuilder = Component.text();
            if (pageObject.has("text")) {
                pageBuilder.append(LegacyComponentSerializer.legacyAmpersand().deserialize(pageObject.get("text").getAsString()));
            }
            if (pageObject.has("color")) {
                int color = Color.decode(pageObject.get("color").getAsString()).getRGB();
                pageBuilder.color(TextColor.color(color));
            }
            if (pageObject.has("clickEvent")) {
                JsonObject clickEvent = pageObject.get("clickEvent").getAsJsonObject();
                if (clickEvent.has("action") && clickEvent.has("value")) {
                    String action = clickEvent.get("action").getAsString();
                    String value = clickEvent.get("value").getAsString();
                    switch (action) {
                        case "open_url":
                            pageBuilder.clickEvent(ClickEvent.openUrl(value));
                            break;
                        case "run_command":
                            pageBuilder.clickEvent(ClickEvent.runCommand(value));
                            break;
                        case "suggest_command":
                            pageBuilder.clickEvent(ClickEvent.suggestCommand(value));
                            break;
                        default:
                            // Invalid click event, do nothing
                            break;
                    }
                }
            }
            pages.add(pageBuilder.build());
        }

        Book book = Book.builder()
                .author(LegacyComponentSerializer.legacyAmpersand().deserialize(""))
                .title(LegacyComponentSerializer.legacyAmpersand().deserialize(""))
                .pages(pages)
                .build();

        player.openBook(book);
    }


    // Format a page of text with color and style
    private static Component formatPage(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    // Format a clickable link
    private static String formatLink(String url) {
        Component link = Component.text(url).color(NamedTextColor.BLUE)
                .clickEvent(ClickEvent.openUrl(url))
                .hoverEvent(Component.text("Click to open link").color(NamedTextColor.GRAY));
        return link.toString();
    }
}