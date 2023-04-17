package com.vicr123.IOMFrontend.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rotondos")
public class Rotondo {
    @DatabaseField(id = true)
    private long id;

    @DatabaseField(foreign = true, uniqueIndexName = "unique_of_direction", foreignAutoRefresh = true)
    private Map of;

    @DatabaseField(uniqueIndexName = "unique_of_direction")
    private int direction;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map getOf() {
        return of;
    }

    public void setOf(Map of) {
        this.of = of;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
