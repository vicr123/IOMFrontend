package com.vicr123.IOMFrontend.Commands;

import com.vicr123.IOMFrontend.Server.ServerRoot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IOMCommand implements CommandExecutor {
    ServerRoot server;

    public IOMCommand(ServerRoot server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        TextComponent component = new TextComponent("Click to edit your maps");
        component.setColor(ChatColor.GREEN);
        component.setUnderlined(true);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, server.rootUrl() + "?auth=" + server.tokenForPlayer((Player) sender)));
        sender.spigot().sendMessage(component);
        return true;
    }
}
