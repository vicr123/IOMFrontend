package com.vicr123.IOMFrontend.Database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseManager {
    private Dao<Map, Long> mapDao;

    public DatabaseManager() {
        try {
            JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource("jdbc:sqlite:iomfrontend.db");

            TableUtils.createTableIfNotExists(connectionSource, Map.class);
            mapDao = DaoManager.createDao(connectionSource, Map.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Map, Long> getMapDao() {
        return mapDao;
    }
}
