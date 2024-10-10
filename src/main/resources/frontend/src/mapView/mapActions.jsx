export function availableMapActions(data, isCollection, collection, manager, onCompleted) {
    if (isCollection && data.isOwner) {
        return [
            {
                text: "Remove from Global Collection",
                action: async () => {
                    if (window.confirm("Remove this map from the collection? Any placed maps will stay active.")) {
                        await manager.deleteCollection(data.id, collection);
                        onCompleted();
                    }
                }
            }
        ]
    }
    if (!isCollection) {
        return [
            {
                text: "Get",
                action: () => {
                    manager.giveMap(data.id);
                    onCompleted();
                }
            },
            {
                text: "Recategorise",
                action: async () => {
                    let catg = prompt("Name of category for this map:", data.category);
                    if (catg !== null) {
                        await manager.setCatg(data.id, catg);
                        onCompleted();
                    }
                }
            },
            {
                text: "Add to Global Collection",
                action: async () => {
                    let collection = prompt("Name of collection to add to:", data.category);
                    if (collection !== null) {
                        await manager.addToCollection(data.id, collection);
                        onCompleted();
                    }
                }
            },
            {
                text: "Delete",
                action: async () => {
                if (window.confirm("Delete this map? Any mapsigns remaining on the world will cease to function correctly, and the map will be deleted from all public collections as well.")) {
                    await manager.deleteMap(data.id);
                    onCompleted();
                }
            }
            }
        ]
    }
}