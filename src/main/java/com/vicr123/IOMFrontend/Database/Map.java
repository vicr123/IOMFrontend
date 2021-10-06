package com.vicr123.IOMFrontend.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "maps")
public class Map {
    @DatabaseField(id = true)
    private long id;

    @DatabaseField
    private String associatedPlayer;

    @DatabaseField
    private String pictureResource;

    @DatabaseField
    private String name;

    @DatabaseField
    private String category;

    public String getPictureResource() {
        return pictureResource;
    }

    public void setPictureResource(String pictureResource) {
        this.pictureResource = pictureResource;
    }

    public String getAssociatedPlayer() {
        return associatedPlayer;
    }

    public void setAssociatedPlayer(String associatedPlayer) {
        this.associatedPlayer = associatedPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(long id) {
        this.id = id;
    }
}
