package com.vicr123.IOMFrontend.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "collectionentries")
public class CollectionEntry {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(uniqueIndexName = "unique_name_map")
    private String name;

    @DatabaseField(foreign = true, uniqueIndexName = "unique_name_map", foreignAutoRefresh = true)
    private Map map;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
