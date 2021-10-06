package com.vicr123.IOMFrontend;

import com.vicr123.IOMFrontend.Commands.IOMCommand;
import com.vicr123.IOMFrontend.Database.DatabaseManager;
import com.vicr123.IOMFrontend.Images.ImageManager;
import com.vicr123.IOMFrontend.Server.ServerRoot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class IOMFrontendPlugin extends JavaPlugin {
    ServerRoot server;
    ImageManager images;
    DatabaseManager db;

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();

        db = new DatabaseManager();
        images = new ImageManager(this);
        server = new ServerRoot(this, db, images);

        Objects.requireNonNull(getCommand("iom")).setExecutor(new IOMCommand(server, db, this));

        getLogger().info("IOMFrontend is ready!");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        getLogger().info("IOMFrontend is stopped!");
    }
}
