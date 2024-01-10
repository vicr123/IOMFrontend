package com.vicr123.IOMFrontend.Commands;

import com.loohp.imageframe.ImageFrame;
import com.vicr123.IOMFrontend.Database.DatabaseManager;
import com.vicr123.IOMFrontend.Database.Map;
import com.vicr123.IOMFrontend.IOMFrontendPlugin;
import com.vicr123.IOMFrontend.Server.ServerRoot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;

public class IOMCommand implements CommandExecutor {
    ServerRoot server;
    DatabaseManager db;
    IOMFrontendPlugin plugin;

    public IOMCommand(ServerRoot server, DatabaseManager db, IOMFrontendPlugin plugin) {
        this.server = server;
        this.db = db;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;

        sender.sendMessage("Syncing maps...");

        ArrayList<Map> toDelete = new ArrayList<>();
        ArrayList<Map> toCreate = new ArrayList<>();

        //First, attempt to clean up the map database
        //Delete any maps that are not found (deleted in-game)
        try {
            for (Map map : db.getMapDao().queryForEq("associatedPlayer", player.getUniqueId().toString())) {
                var imageMap = ImageFrame.imageMapManager.getFromCreator(player.getUniqueId()).stream().filter(im -> im.getMapIds().stream().anyMatch(id -> id == map.getId())).findAny().orElse(null);
                if (imageMap == null) toDelete.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Import any new maps that are not found (created in-game)
        for (var imageMap : ImageFrame.imageMapManager.getFromCreator(player.getUniqueId())) {
            try {
                if (db.getMapDao().queryForAll().stream().noneMatch(map -> ImageFrame.imageMapManager.getFromMapId((int) map.getId()) != null) &&
                        db.getRotondoDao().queryForAll().stream().noneMatch(rotondo -> ImageFrame.imageMapManager.getFromMapId((int) rotondo.getId()) != null)) {
                    //This map is not found
                    Map map = new Map();
                    map.setId(imageMap.getMapIds().get(0));
                    map.setName(imageMap.getName());
                    map.setAssociatedPlayer(player.getUniqueId().toString());
                    map.setPictureResource("x");

                    toCreate.add(map);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                db.getMapDao().delete(toDelete);
                db.getMapDao().create(toCreate);
            } catch (SQLException e) {
                sender.sendMessage("An error occurred syncing your maps. The maps on the web UI may not be up to date.");
                e.printStackTrace();
            }

            TextComponent component = new TextComponent("Click to edit your maps");
            component.setColor(ChatColor.GREEN);
            component.setUnderlined(true);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, server.rootUrl() + "?auth=" + server.tokenForPlayer((Player) sender)));
            sender.spigot().sendMessage(component);
        });

        return true;
    }
}
