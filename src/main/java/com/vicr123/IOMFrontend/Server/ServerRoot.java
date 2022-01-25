package com.vicr123.IOMFrontend.Server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vicr123.IOMFrontend.Database.CollectionEntry;
import com.vicr123.IOMFrontend.Database.DatabaseManager;
import com.vicr123.IOMFrontend.Database.Map;
import com.vicr123.IOMFrontend.IOMFrontendPlugin;
import com.vicr123.IOMFrontend.Images.ImageManager;
import com.vicr123.IOMFrontend.PlayerProxy;
import express.DynExpress;
import express.Express;
import express.http.HttpRequestHandler;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;
import fr.moribus.imageonmap.image.ImageRendererExecutor;
import fr.moribus.imageonmap.image.ImageUtils;
import fr.moribus.imageonmap.map.ImageMap;
import fr.moribus.imageonmap.map.MapManager;
import fr.moribus.imageonmap.map.MapManagerException;
import fr.moribus.imageonmap.map.PosterMap;
import fr.zcraft.imageonmap.quartzlib.components.worker.WorkerCallback;
import org.bukkit.entity.Player;

import javax.naming.Context;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
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

    class Bindings {
        @DynExpress(context = "/maps")
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

            ImageMap[] mapsArray = MapManager.getMaps(player.getUniqueId());

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

                Map map = new Map();
                map.setName(mapData.name);
                map.setAssociatedPlayer(player.getUniqueId().toString());
                map.setPictureResource(imageHash);
                map.setCategory(mapData.category);

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        ImageRendererExecutor.render(new URL("http://localhost:" + plugin.getConfig().getInt("port") + "/images/" + imageHash), ImageUtils.ScalingType.NONE, player.getUniqueId(), 0, 0, new WorkerCallback<ImageMap>() {
                            @Override
                            public void finished(ImageMap imageMap) {
                                try {
                                    imageMap.rename(mapData.name);

                                    map.setId(imageMap.getMapsIDs()[0]);
                                    db.getMapDao().create(map);

                                    JsonObject obj = new JsonObject();
                                    obj.addProperty("id", map.getId());

                                    res.send(gson.toJson(obj));
                                    res.sendStatus(Status._201);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    res.sendStatus(Status._500);
                                }
                            }

                            @Override
                            public void errored(Throwable throwable) {
                                res.sendStatus(Status._500);
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        res.sendStatus(Status._500);
                    }
                });
            } catch (Exception e) {
                res.sendStatus(Status._500);
            }
        }

        @DynExpress(context = "/maps/:id", method = RequestMethod.PUT)
        public void updateMap(Request req, Response res) throws SQLException, IOException, NoSuchAlgorithmException {
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

            ImageMap imageMap = Arrays.stream(MapManager.getMaps(player.getUniqueId())).filter(im -> Arrays.stream(im.getMapsIDs()).anyMatch(id -> id == map.getId())).findAny().orElse(null);
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

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    ImageRendererExecutor.update(new URL("http://localhost:" + plugin.getConfig().getInt("port")  + "/images/" + imageHash), ImageUtils.ScalingType.NONE, player.getUniqueId(), imageMap, 0, 0, new WorkerCallback<>() {
                        @Override
                        public void finished(ImageMap imageMap) {
                            try {
                                map.setPictureResource(imageHash);
                                map.setId(imageMap.getMapsIDs()[0]);

                                db.getMapDao().update(map);
                                res.sendStatus(Status._204);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                                res.sendStatus(Status._500);
                            }
                        }

                        @Override
                        public void errored(Throwable throwable) {
                            throwable.printStackTrace();
                            res.sendStatus(Status._500);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            });
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
        public void renameMap(Request req, Response res) throws SQLException {
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

            ImageMap imageMap = Arrays.stream(MapManager.getMaps(player.getUniqueId())).filter(im -> Arrays.stream(im.getMapsIDs()).anyMatch(id -> id == map.getId())).findAny().orElse(null);
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

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    ImageMap imageMap = Arrays.stream(MapManager.getMaps(player.getUniqueId())).filter(im -> Arrays.stream(im.getMapsIDs()).anyMatch(id -> id == map.getId())).findAny().orElse(null);
                    if (imageMap != null) {
                        MapManager.clear(player.getInventory(), imageMap);
                        MapManager.deleteMap(imageMap);
                    }

                    db.getMapDao().delete(map);
                    res.sendStatus(Status._204);
                } catch (SQLException | MapManagerException throwables) {
                    throwables.printStackTrace();
                }
            });
        }

        @DynExpress(context = "/maps/:id/give")
        public void givePlayerMap(Request req, Response res) throws SQLException {
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

            if (!map.getAssociatedPlayer().equals(player.getUniqueId().toString()) || db.getCollectionMapDao().queryForEq("map_id", map.getId()).isEmpty()) {
                res.sendStatus(Status._403);
                return;
            }

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                ImageMap imageMap = Arrays.stream(MapManager.getMaps(player.getUniqueId())).filter(im -> Arrays.stream(im.getMapsIDs()).anyMatch(id -> id == map.getId())).findAny().orElse(null);
                if (imageMap != null) {
                    imageMap.give(player);
                }

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

                Map map = db.getMapDao().queryForId(Long.valueOf(req.getParam("id")));
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
                        "map_id", map.getId()
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
                res.sendBytes(images.getImage(req.getParam("hash")).readAllBytes());
            } catch (IOException e) {
                res.sendStatus(Status._404);
            }
        }

        @DynExpress(context = "/collections")
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
                    collectionEntries.stream().map(CollectionEntry::getMap).forEach(map -> {
                        MapToJsonArray(array, map, player);
                    });

                    obj.add(collection, array);
                });

                Gson gson = new Gson();
                res.send(gson.toJson(obj));
            } catch (SQLException e) {
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
            array.add(mapObj);
        }
    }

    public String rootUrl() {
        return plugin.getConfig().getString("root");
    }
}
