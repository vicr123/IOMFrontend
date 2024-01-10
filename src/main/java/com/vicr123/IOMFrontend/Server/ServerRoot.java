package com.vicr123.IOMFrontend.Server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loohp.imageframe.ImageFrame;
import com.loohp.imageframe.objectholders.ImageMap;
import com.loohp.imageframe.objectholders.URLImageMap;
import com.loohp.imageframe.objectholders.URLStaticImageMap;
import com.vicr123.IOMFrontend.Database.CollectionEntry;
import com.vicr123.IOMFrontend.Database.DatabaseManager;
import com.vicr123.IOMFrontend.Database.Map;
import com.vicr123.IOMFrontend.Database.Rotondo;
import com.vicr123.IOMFrontend.IOMFrontendPlugin;
import com.vicr123.IOMFrontend.Images.ImageManager;
import express.DynExpress;
import express.Express;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ServerRoot {
    private final IOMFrontendPlugin plugin;
    private final DatabaseManager db;
    private final ImageManager images;
    public Algorithm tokenAlgorithm;

    static class MapData {
        String name;
        String image;
        String category;
    }

    static class CollectionData {
        String name;
    }

    public ServerRoot(IOMFrontendPlugin plugin, DatabaseManager db, ImageManager images) {
        this.plugin = plugin;
        this.db = db;
        this.images = images;

        byte[] tokenSecret = new byte[32];
        new Random().nextBytes(tokenSecret);
        this.tokenAlgorithm = Algorithm.HMAC256(tokenSecret);

        Express app = new Express();
        app.use(new StaticFilesMiddleware());
        app.use(new PlayerMiddleware(plugin, tokenAlgorithm));
        app.bind(new Bindings());
        app.get("*", (req, res) -> {
            String path = req.getPath();
            if (path.equals("/")) path = "/index.html";

            try {
                URL resource = ServerRoot.class.getResource("/frontend/build" + path);
                if (resource == null) {
                    res.sendStatus(Status._404);
                    return;
                }

                URLConnection connection = resource.openConnection();
                res.streamFrom(connection.getContentLength(), connection.getInputStream(), MediaType.getByExtension(path.substring(path.lastIndexOf(".") + 1)));
            } catch (IOException e) {
                res.sendStatus(Status._404);
            }
        });
        app.listen(() -> plugin.getLogger().info("IOM server listening on port " + plugin.getConfig().getInt("port")), plugin.getConfig().getInt("port"));
    }

    public String tokenForPlayer(Player player) {
        return JWT.create()
                .withIssuer("IOM")
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(6)))
                .withClaim("sub", player.getUniqueId().toString())
                .sign(tokenAlgorithm);
    }

    public String writeRotondo(Map map, int direction) throws IOException, NoSuchAlgorithmException {
        var image = ImageIO.read(images.getImage(map.getPictureResource()));

        for (int i = 0; i < direction; i++) {
            image = rotate90(image);
        }

//        int w = image.getWidth();
//        int h = image.getHeight();
//        var transform = new AffineTransform();
//        transform.rotate(Math.PI / 2 * direction, w / 2, h / 2);
//        var op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//        var rotated = op.filter(image, null);

//        var newImage = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
//        n.createGraphics().drawImage(image, transform, null);

        var output = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", output);
        return images.putImage(output.toByteArray());
    }

    private BufferedImage rotate90(BufferedImage image) {
        var width = image.getWidth();
        var height = image.getHeight();

        var newImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                newImage.setRGB(height - y - 1, x, image.getRGB(x, y));
            }
        }

        return newImage;
    }

    class Bindings {
        @DynExpress(context = "/maps", method = RequestMethod.GET)
        public void getMaps(Request req, Response res) throws SQLException {
            Player player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            JsonArray rootArray = new JsonArray();

            List<Map> maps = db.getMapDao().queryForEq("associatedPlayer", player.getUniqueId().toString());
            for (Map map : maps) {
                MapToJsonArray(rootArray, map, player);
            }

            Gson gson = new Gson();
            res.send(gson.toJson(rootArray));
        }

        @DynExpress(context = "/maps", method = RequestMethod.POST)
        public void addMap(Request req, Response res) {
            try {
                Player player = (Player) req.getMiddlewareContent("player");
                if (player == null) {
                    res.sendStatus(Status._401);
                    return;
                }

                Gson gson = new Gson();
                MapData mapData = gson.fromJson(new InputStreamReader(req.getBody()), MapData.class);

                String imageHash = images.putImage(Base64.getDecoder().decode(mapData.image));

                var nonConflictName = mapData.name;
                int suffix = 1;

                while (ImageFrame.imageMapManager.getFromCreator(player.getUniqueId(), nonConflictName) != null) {
                    nonConflictName = mapData.name + "-" + suffix;
                    suffix++;
                }
                Map map = new Map();
                map.setName(nonConflictName);
                map.setAssociatedPlayer(player.getUniqueId().toString());
                map.setPictureResource(imageHash);
                map.setCategory(mapData.category);

                try {
                    var imageData = images.getImageData(imageHash);
                    var imageMap = URLStaticImageMap.create(ImageFrame.imageMapManager, nonConflictName, imageData.getImageUrl(), imageData.getBlockWidth(), imageData.getBlockHeight(), player.getUniqueId()).get();
                    ImageFrame.imageMapManager.addMap(imageMap);

                    try {
                        map.setId(imageMap.getMapIds().get(0));
                        db.getMapDao().create(map);

                        JsonObject obj = new JsonObject();
                        obj.addProperty("id", map.getId());

                        res.send(gson.toJson(obj));
                        res.sendStatus(Status._201);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        res.sendStatus(Status._500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    res.sendStatus(Status._500);
                }
            } catch (Exception e) {
                res.sendStatus(Status._500);
            }
        }

        @DynExpress(context = "/maps/:id", method = RequestMethod.PUT)
        public void updateMap(Request req, Response res) throws SQLException, IOException, NoSuchAlgorithmException {
            try {
                Player player = (Player) req.getMiddlewareContent("player");
                if (player == null) {
                    res.sendStatus(Status._401);
                    return;
                }

                Map map = db.getMapDao().queryForId(Long.valueOf(req.getParam("id")));
                if (map == null) {
                    res.sendStatus(Status._404);
                    return;
                }

                if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString())) {
                    res.sendStatus(Status._403);
                    return;
                }

                var imageMap = ImageFrame.imageMapManager.getFromCreator(player.getUniqueId()).stream().filter(im -> im.getMapIds().stream().anyMatch(id -> id == map.getId())).findAny().orElse(null);
                if (imageMap == null) {
                    res.sendStatus(Status._500);
                    return;
                }

                Gson gson = new Gson();
                JsonObject json = gson.fromJson(new InputStreamReader(req.getBody()), JsonObject.class);

                if (!json.has("image")) {
                    res.sendStatus(Status._400);
                    return;
                }

                String imageHash = images.putImage(Base64.getDecoder().decode(json.get("image").getAsString()));

                if (imageMap instanceof URLImageMap) {
                    updateImageMap(imageHash, (URLImageMap) imageMap);
                    map.setPictureResource(imageHash);
                    map.setId(imageMap.getMapIds().get(0));
                    db.getMapDao().update(map);
                }

                var rotondos = db.getRotondoDao().queryForEq("of_id", map.getId());
                for (var rotondo : rotondos) {
                    //Create rotondo variants of this map
                    var rimageHash = writeRotondo(map, rotondo.getDirection());
                    var rMap = ImageFrame.imageMapManager.getFromCreator(player.getUniqueId()).stream().filter(im -> im.getMapIds().stream().anyMatch(id -> id == rotondo.getId())).findAny().orElse(null);
                    if (rMap == null) continue;

                    if (rMap instanceof URLImageMap) {
                        updateImageMap(rimageHash, (URLImageMap) rMap);
                        rotondo.setId(imageMap.getMapIds().get(0));
                        db.getRotondoDao().update(rotondo);
                    }
                }

                res.sendStatus(Status._204);
            } catch (Throwable e) {
                e.printStackTrace();
                res.sendStatus(Status._500);
            }
        }

        @DynExpress(context = "/maps/:id/rotondo", method = RequestMethod.POST)
        public void rotondoMap(Request req, Response res) throws SQLException, IOException, NoSuchAlgorithmException {
            Player player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            Map map = db.getMapDao().queryForId(Long.valueOf(req.getParam("id")));
            if (map == null) {
                res.sendStatus(Status._404);
                return;
            }

            if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString())) {
                res.sendStatus(Status._403);
                return;
            }

            try {
                //Create rotondo variants of this map
                for (int i = 1; i < 4; i++) {
                    if (!db.getRotondoDao().queryForFieldValues(java.util.Map.of(
                            "of_id", map.getId(),
                            "direction", i
                    )).isEmpty()) {
                        continue;
                    }

                    var rotondoDirection = switch (i) {
                        case 1 -> "E";
                        case 2 -> "S";
                        case 3 -> "W";
                        default -> "";
                    };

                    var imageHash = writeRotondo(map, i);
                    int finalI = i;

                    var imageData = images.getImageData(imageHash);
                    var imageMap = URLStaticImageMap.create(ImageFrame.imageMapManager, map.getName() + "-rotondo-" + rotondoDirection, imageData.getImageUrl(), imageData.getBlockWidth(), imageData.getBlockHeight(), player.getUniqueId()).get();
                    ImageFrame.imageMapManager.addMap(imageMap);

                    try {
                        var rotondo = new Rotondo();
                        rotondo.setOf(map);
                        rotondo.setDirection(finalI);
                        rotondo.setId(imageMap.getMapIds().get(0));

                        db.getRotondoDao().create(rotondo);

                        JsonObject obj = new JsonObject();
                        obj.addProperty("id", map.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        res.sendStatus(Status._500);
                    }
                }

                res.sendStatus(Status._204);
            } catch (Exception e) {
                e.printStackTrace();
                res.sendStatus(Status._500);
            }
        }

        @DynExpress(context = "/maps/:id/category", method = RequestMethod.POST)
        public void recategoriseMap(Request req, Response res) throws SQLException {
            Player player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            Map map = db.getMapDao().queryForId(Long.valueOf(req.getParam("id")));
            if (map == null) {
                res.sendStatus(Status._404);
                return;
            }

            if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString())) {
                res.sendStatus(Status._403);
                return;
            }

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(new InputStreamReader(req.getBody()), JsonObject.class);
            if (!json.has("category")) {
                res.sendStatus(Status._400);
                return;
            }

            map.setCategory(json.get("category").getAsString());
            db.getMapDao().update(map);

            res.sendStatus(Status._204);
        }

        @DynExpress(context = "/maps/:id/name", method = RequestMethod.POST)
        public void renameMap(Request req, Response res) throws Exception {
            Player player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            Map map = db.getMapDao().queryForId(Long.valueOf(req.getParam("id")));
            if (map == null) {
                res.sendStatus(Status._404);
                return;
            }

            if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString())) {
                res.sendStatus(Status._403);
                return;
            }

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(new InputStreamReader(req.getBody()), JsonObject.class);
            if (!json.has("name")) {
                res.sendStatus(Status._400);
                return;
            }

            var imageMap = ImageFrame.imageMapManager.getFromCreator(player.getUniqueId()).stream().filter(im -> im.getMapIds().stream().anyMatch(id -> id == map.getId())).findAny().orElse(null);
            if (imageMap != null) {
                imageMap.rename(json.get("name").getAsString());
                map.setName(json.get("name").getAsString());
                db.getMapDao().update(map);
            }

            res.sendStatus(Status._204);
        }

        @DynExpress(context = "/maps/:id", method = RequestMethod.DELETE)
        public void deleteMap(Request req, Response res) throws SQLException {
            Player player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            Map map = db.getMapDao().queryForId(Long.valueOf(req.getParam("id")));
            if (map == null) {
                res.sendStatus(Status._404);
                return;
            }

            if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString())) {
                res.sendStatus(Status._403);
                return;
            }

            try {
                var rotondos = db.getRotondoDao().queryForEq("of_id", map.getId());

                var imageMaps = ImageFrame.imageMapManager.getFromCreator(player.getUniqueId()).stream().filter(im -> im.getMapIds().stream().anyMatch(id -> id == map.getId() || rotondos.stream().anyMatch(rotondo -> rotondo.getId() == id)));
                for (var imageMap : imageMaps.toList()) {
                    ImageFrame.imageMapManager.deleteMap(imageMap.getImageIndex());
                }

                //Remove the map from all collections
                List<CollectionEntry> toDelete = db.getCollectionMapDao().queryForAll().stream().filter(collectionEntry -> collectionEntry.getMap().getId() == map.getId()).collect(Collectors.toList());
                if (!toDelete.isEmpty()) db.getCollectionMapDao().delete(toDelete);

                db.getRotondoDao().delete(rotondos);
                db.getMapDao().delete(map);
                res.sendStatus(Status._204);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        @DynExpress(context = "/maps/:id/give", method = RequestMethod.GET)
        public void givePlayerMap(Request req, Response res) throws SQLException {
            Player player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            long idToObtain;
            String associatedPlayer;
            long mapId = Long.parseLong(req.getParam("id"));
            Map map = db.getMapDao().queryForId(mapId);
            if (map == null) {
                Rotondo rotondo = db.getRotondoDao().queryForId(mapId);
                if (rotondo == null) {
                    res.sendStatus(Status._404);
                    return;
                } else {
                    var rotondoOriginalMap = rotondo.getOf();
                    idToObtain = rotondo.getId();
                    associatedPlayer = rotondoOriginalMap.getAssociatedPlayer();
                }
            } else {
                idToObtain = map.getId();
                associatedPlayer = map.getAssociatedPlayer();
            }

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                ImageFrame.imageMapManager.getFromCreator(UUID.fromString(associatedPlayer)).stream().filter(im -> im.getMapIds().stream().anyMatch(id -> id == idToObtain)).findAny().ifPresent(imageMap -> ImageFrame.combinedMapItemHandler.giveCombinedMap(imageMap, player));
                res.sendStatus(Status._204);
            });
        }

        @DynExpress(context = "/maps/:id/collection", method = RequestMethod.POST)
        public void addMapToCollection(Request req, Response res) {
            try {
                Player player = (Player) req.getMiddlewareContent("player");
                if (player == null) {
                    res.sendStatus(Status._401);
                    return;
                }

                Map map = db.getMapDao().queryForId(Long.valueOf(req.getParam("id")));
                if (map == null) {
                    res.sendStatus(Status._404);
                    return;
                }

                if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString())) {
                    res.sendStatus(Status._403);
                    return;
                }

                Gson gson = new Gson();
                CollectionData collectionData = gson.fromJson(new InputStreamReader(req.getBody()), CollectionData.class);

                CollectionEntry entry = new CollectionEntry();
                entry.setName(collectionData.name);
                entry.setMap(map);
                db.getCollectionMapDao().create(entry);

                res.sendStatus(Status._201);
            } catch (Exception e) {
                res.sendStatus(Status._500);
            }
        }

        @DynExpress(context = "/maps/:id/collection/:collectionName", method = RequestMethod.DELETE)
        public void removeMapFromCollection(Request req, Response res) {
            try {
                Player player = (Player) req.getMiddlewareContent("player");
                if (player == null) {
                    res.sendStatus(Status._401);
                    return;
                }

                long mapId = Long.parseLong(req.getParam("id"));
                Map map = db.getMapDao().queryForId(mapId);
                if (map == null) {
                    res.sendStatus(Status._404);
                    return;
                }

                if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString())) {
                    res.sendStatus(Status._403);
                    return;
                }

                List<CollectionEntry> toDelete = db.getCollectionMapDao().queryForFieldValues(java.util.Map.of(
                        "name", req.getParam("collectionName"),
                        "map_id", mapId
                ));

                if (toDelete.isEmpty()) {
                    res.sendStatus(Status._404);
                    return;
                }

                db.getCollectionMapDao().delete(toDelete);

                res.sendStatus(Status._204);
            } catch (Exception e) {
                res.sendStatus(Status._500);
            }
        }

        @DynExpress(context = "/images/:hash", method = RequestMethod.GET)
        public void getImage(Request req, Response res) {
            try {
                res.setHeader("Cache-Control", "public, max-age=31536000");
                res.sendBytes(images.getImage(req.getParam("hash")).readAllBytes());
            } catch (IOException e) {
                res.sendStatus(Status._404);
            }
        }

        @DynExpress(context = "/collections", method = RequestMethod.GET)
        public void getCollections(Request req, Response res) {
            try {
                Player player = (Player) req.getMiddlewareContent("player");
                if (player == null) {
                    res.sendStatus(Status._401);
                    return;
                }

                java.util.Map<String, List<CollectionEntry>> collections = new HashMap<>();
                for (CollectionEntry collectionEntry : db.getCollectionMapDao().queryForAll()) {
                    collections.computeIfAbsent(collectionEntry.getName(), k -> new ArrayList<>()).add(collectionEntry);
                }

                JsonObject obj = new JsonObject();
                collections.forEach((collection, collectionEntries) -> {
                    JsonArray array = new JsonArray();
                    collectionEntries.stream().map(CollectionEntry::getMap).filter(Objects::nonNull).forEach(map -> {
                        MapToJsonArray(array, map, player);
                    });

                    obj.add(collection, array);
                });

                Gson gson = new Gson();
                res.send(gson.toJson(obj));
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        private void MapToJsonArray(JsonArray array, Map map, Player currentPlayer) {
            JsonObject mapObj = new JsonObject();
            mapObj.addProperty("name", map.getName());
            mapObj.addProperty("pictureResource", map.getPictureResource());
            mapObj.addProperty("id", map.getId());
            mapObj.addProperty("category", map.getCategory());
            mapObj.addProperty("isOwner", currentPlayer.getUniqueId().toString().equals(map.getAssociatedPlayer()));

            try {
                var rotondos = db.getRotondoDao().queryForEq("of_id", map.getId());
                var rotondosObject = new JsonObject();
                for (var rotondo : rotondos) {
                    rotondosObject.addProperty(String.valueOf(rotondo.getDirection()), String.valueOf(rotondo.getId()));
                }
                mapObj.add("rotondos", rotondosObject);
            } catch (SQLException ignored) {
            }

            array.add(mapObj);
        }
    }

    private void updateImageMap(String imageHash, URLImageMap imageMap) throws IOException {
        var imageData = images.getImageData(imageHash);

        var oldUrl = imageMap.getUrl();
        imageMap.setUrl(imageData.getImageUrl());
        try {
            imageMap.update();
        } catch (Throwable e) {
            imageMap.setUrl(oldUrl);
            e.printStackTrace();
        }
    }

    public String rootUrl() {
        return plugin.getConfig().getString("root");
    }
}
