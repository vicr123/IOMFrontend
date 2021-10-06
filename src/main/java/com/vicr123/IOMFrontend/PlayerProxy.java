package com.vicr123.IOMFrontend;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.*;

public class PlayerProxy implements Player {
    Player player;

    public PlayerProxy(Player player) {
        this.player = player;
    }

    public void getMaps() {
        player.getServer().dispatchCommand(this, "maptool list");
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public void setDisplayName(String name) {
        player.setDisplayName(name);
    }

    @Override
    public String getPlayerListName() {
        return player.getPlayerListName();
    }

    @Override
    public void setPlayerListName(String name) {
        player.setPlayerListName(name);
    }

    @Override
    public String getPlayerListHeader() {
        return player.getPlayerListHeader();
    }

    @Override
    public String getPlayerListFooter() {
        return player.getPlayerListFooter();
    }

    @Override
    public void setPlayerListHeader(String header) {
        player.setPlayerListHeader(header);
    }

    @Override
    public void setPlayerListFooter(String footer) {
        player.setPlayerListFooter(footer);
    }

    @Override
    public void setPlayerListHeaderFooter(String header, String footer) {
        player.setPlayerListHeaderFooter(header, footer);
    }

    @Override
    public void setCompassTarget(Location loc) {
        player.setCompassTarget(loc);
    }

    @Override
    public Location getCompassTarget() {
        return player.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress() {
        return player.getAddress();
    }

    @Override
    public void sendRawMessage(String message) {
        player.sendRawMessage(message);
    }

    @Override
    public void kickPlayer(String message) {
        player.kickPlayer(message);
    }

    @Override
    public void chat(String msg) {
        player.chat(msg);
    }

    @Override
    public boolean performCommand(String command) {
        return player.performCommand(command);
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return player.isOnGround();
    }

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public void setSneaking(boolean sneak) {
        player.setSneaking(sneak);
    }

    @Override
    public boolean isSprinting() {
        return player.isSprinting();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        player.setSprinting(sprinting);
    }

    @Override
    public void saveData() {
        player.saveData();
    }

    @Override
    public void loadData() {
        player.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
        player.setSleepingIgnored(isSleeping);
    }

    @Override
    public boolean isSleepingIgnored() {
        return player.isSleepingIgnored();
    }

    @Override
    public Location getBedSpawnLocation() {
        return player.getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        player.setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean force) {
        player.setBedSpawnLocation(location, force);
    }

    @Override
    @Deprecated
    public void playNote(Location loc, byte instrument, byte note) {
        player.playNote(loc, instrument, note);
    }

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        player.playNote(loc, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        player.playSound(location, sound, volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        player.playSound(location, sound, volume, pitch);
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        player.playSound(location, sound, category, volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, SoundCategory category, float volume, float pitch) {
        player.playSound(location, sound, category, volume, pitch);
    }

    @Override
    public void stopSound(Sound sound) {
        player.stopSound(sound);
    }

    @Override
    public void stopSound(String sound) {
        player.stopSound(sound);
    }

    @Override
    public void stopSound(Sound sound, SoundCategory category) {
        player.stopSound(sound, category);
    }

    @Override
    public void stopSound(String sound, SoundCategory category) {
        player.stopSound(sound, category);
    }

    @Override
    @Deprecated
    public void playEffect(Location loc, Effect effect, int data) {
        player.playEffect(loc, effect, data);
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
        player.playEffect(loc, effect, data);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location loc, Material material, byte data) {
        player.sendBlockChange(loc, material, data);
    }

    @Override
    public void sendBlockChange(Location loc, BlockData block) {
        player.sendBlockChange(loc, block);
    }

    @Override
    public void sendBlockDamage(Location loc, float progress) {
        player.sendBlockDamage(loc, progress);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        return player.sendChunkChange(loc, sx, sy, sz, data);
    }

    @Override
    public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException {
        player.sendSignChange(loc, lines);
    }

    @Override
    public void sendSignChange(Location loc, String[] lines, DyeColor dyeColor) throws IllegalArgumentException {
        player.sendSignChange(loc, lines, dyeColor);
    }

    @Override
    public void sendMap(MapView map) {
        player.sendMap(map);
    }

    @Override
    public void updateInventory() {
        player.updateInventory();
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        player.setPlayerTime(time, relative);
    }

    @Override
    public long getPlayerTime() {
        return player.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return player.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return player.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        player.resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(WeatherType type) {
        player.setPlayerWeather(type);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return player.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        player.resetPlayerWeather();
    }

    @Override
    public void giveExp(int amount) {
        player.giveExp(amount);
    }

    @Override
    public void giveExpLevels(int amount) {
        player.giveExpLevels(amount);
    }

    @Override
    public float getExp() {
        return player.getExp();
    }

    @Override
    public void setExp(float exp) {
        player.setExp(exp);
    }

    @Override
    public int getLevel() {
        return player.getLevel();
    }

    @Override
    public void setLevel(int level) {
        player.setLevel(level);
    }

    @Override
    public int getTotalExperience() {
        return player.getTotalExperience();
    }

    @Override
    public void setTotalExperience(int exp) {
        player.setTotalExperience(exp);
    }

    @Override
    public void sendExperienceChange(float progress) {
        player.sendExperienceChange(progress);
    }

    @Override
    public void sendExperienceChange(float progress, int level) {
        player.sendExperienceChange(progress, level);
    }

    @Override
    public boolean getAllowFlight() {
        return player.getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean flight) {
        player.setAllowFlight(flight);
    }

    @Override
    @Deprecated
    public void hidePlayer(Player player) {
        this.player.hidePlayer(player);
    }

    @Override
    public void hidePlayer(Plugin plugin, Player player) {
        this.player.hidePlayer(plugin, player);
    }

    @Override
    @Deprecated
    public void showPlayer(Player player) {
        this.player.showPlayer(player);
    }

    @Override
    public void showPlayer(Plugin plugin, Player player) {
        this.player.showPlayer(plugin, player);
    }

    @Override
    public boolean canSee(Player player) {
        return this.player.canSee(player);
    }

    @Override
    public boolean isFlying() {
        return player.isFlying();
    }

    @Override
    public void setFlying(boolean value) {
        player.setFlying(value);
    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        player.setFlySpeed(value);
    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        player.setWalkSpeed(value);
    }

    @Override
    public float getFlySpeed() {
        return player.getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return player.getWalkSpeed();
    }

    @Override
    @Deprecated
    public void setTexturePack(String url) {
        player.setTexturePack(url);
    }

    @Override
    public void setResourcePack(String url) {
        player.setResourcePack(url);
    }

    @Override
    public void setResourcePack(String url, byte[] hash) {
        player.setResourcePack(url, hash);
    }

    @Override
    public Scoreboard getScoreboard() {
        return player.getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        player.setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled() {
        return player.isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean scale) {
        player.setHealthScaled(scale);
    }

    @Override
    public void setHealthScale(double scale) throws IllegalArgumentException {
        player.setHealthScale(scale);
    }

    @Override
    public double getHealthScale() {
        return player.getHealthScale();
    }

    @Override
    public Entity getSpectatorTarget() {
        return player.getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(Entity entity) {
        player.setSpectatorTarget(entity);
    }

    @Override
    @Deprecated
    public void sendTitle(String title, String subtitle) {
        player.sendTitle(title, subtitle);
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void resetTitle() {
        player.resetTitle();
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count) {
        player.spawnParticle(particle, location, count);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count) {
        player.spawnParticle(particle, x, y, z, count);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
        player.spawnParticle(particle, location, count, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
        player.spawnParticle(particle, x, y, z, count, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        player.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        player.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        player.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        player.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data);
    }

    @Override
    public AdvancementProgress getAdvancementProgress(Advancement advancement) {
        return player.getAdvancementProgress(advancement);
    }

    @Override
    public int getClientViewDistance() {
        return player.getClientViewDistance();
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
    public String getLocale() {
        return player.getLocale();
    }

    @Override
    public void updateCommands() {
        player.updateCommands();
    }

    @Override
    public void openBook(ItemStack book) {
        player.openBook(book);
    }

    @Override
    public Spigot spigot() {
        return player.spigot();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public PlayerInventory getInventory() {
        return player.getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return player.getEnderChest();
    }

    @Override
    public MainHand getMainHand() {
        return player.getMainHand();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        return player.setWindowProperty(prop, value);
    }

    @Override
    public InventoryView getOpenInventory() {
        return player.getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        return player.openInventory(inventory);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean force) {
        return player.openWorkbench(location, force);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean force) {
        return player.openEnchanting(location, force);
    }

    @Override
    public void openInventory(InventoryView inventory) {
        player.openInventory(inventory);
    }

    @Override
    public InventoryView openMerchant(Villager trader, boolean force) {
        return player.openMerchant(trader, force);
    }

    @Override
    public InventoryView openMerchant(Merchant merchant, boolean force) {
        return player.openMerchant(merchant, force);
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    @Deprecated
    public ItemStack getItemInHand() {
        return player.getItemInHand();
    }

    @Override
    @Deprecated
    public void setItemInHand(ItemStack item) {
        player.setItemInHand(item);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return player.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        player.setItemOnCursor(item);
    }

    @Override
    public boolean hasCooldown(Material material) {
        return player.hasCooldown(material);
    }

    @Override
    public int getCooldown(Material material) {
        return player.getCooldown(material);
    }

    @Override
    public void setCooldown(Material material, int ticks) {
        player.setCooldown(material, ticks);
    }

    @Override
    public int getSleepTicks() {
        return player.getSleepTicks();
    }

    @Override
    public boolean sleep(Location location, boolean force) {
        return player.sleep(location, force);
    }

    @Override
    public void wakeup(boolean setSpawnLocation) {
        player.wakeup(setSpawnLocation);
    }

    @Override
    public Location getBedLocation() {
        return player.getBedLocation();
    }

    @Override
    public GameMode getGameMode() {
        return player.getGameMode();
    }

    @Override
    public void setGameMode(GameMode mode) {
        player.setGameMode(mode);
    }

    @Override
    public boolean isBlocking() {
        return player.isBlocking();
    }

    @Override
    public boolean isHandRaised() {
        return player.isHandRaised();
    }

    @Override
    public int getExpToLevel() {
        return player.getExpToLevel();
    }

    @Override
    public float getAttackCooldown() {
        return player.getAttackCooldown();
    }

    @Override
    public boolean discoverRecipe(NamespacedKey recipe) {
        return player.discoverRecipe(recipe);
    }

    @Override
    public int discoverRecipes(Collection<NamespacedKey> recipes) {
        return player.discoverRecipes(recipes);
    }

    @Override
    public boolean undiscoverRecipe(NamespacedKey recipe) {
        return player.undiscoverRecipe(recipe);
    }

    @Override
    public int undiscoverRecipes(Collection<NamespacedKey> recipes) {
        return player.undiscoverRecipes(recipes);
    }

    @Override
    public boolean hasDiscoveredRecipe(NamespacedKey recipe) {
        return player.hasDiscoveredRecipe(recipe);
    }

    @Override
    public Set<NamespacedKey> getDiscoveredRecipes() {
        return player.getDiscoveredRecipes();
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityLeft() {
        return player.getShoulderEntityLeft();
    }

    @Override
    @Deprecated
    public void setShoulderEntityLeft(Entity entity) {
        player.setShoulderEntityLeft(entity);
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityRight() {
        return player.getShoulderEntityRight();
    }

    @Override
    @Deprecated
    public void setShoulderEntityRight(Entity entity) {
        player.setShoulderEntityRight(entity);
    }

    @Override
    public boolean dropItem(boolean dropAll) {
        return player.dropItem(dropAll);
    }

    @Override
    public float getExhaustion() {
        return player.getExhaustion();
    }

    @Override
    public void setExhaustion(float value) {
        player.setExhaustion(value);
    }

    @Override
    public float getSaturation() {
        return player.getSaturation();
    }

    @Override
    public void setSaturation(float value) {
        player.setSaturation(value);
    }

    @Override
    public int getFoodLevel() {
        return player.getFoodLevel();
    }

    @Override
    public void setFoodLevel(int value) {
        player.setFoodLevel(value);
    }

    @Override
    public int getSaturatedRegenRate() {
        return player.getSaturatedRegenRate();
    }

    @Override
    public void setSaturatedRegenRate(int ticks) {
        player.setSaturatedRegenRate(ticks);
    }

    @Override
    public int getUnsaturatedRegenRate() {
        return player.getUnsaturatedRegenRate();
    }

    @Override
    public void setUnsaturatedRegenRate(int ticks) {
        player.setUnsaturatedRegenRate(ticks);
    }

    @Override
    public int getStarvationRate() {
        return player.getStarvationRate();
    }

    @Override
    public void setStarvationRate(int ticks) {
        player.setStarvationRate(ticks);
    }

    @Override
    public double getEyeHeight() {
        return player.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean ignorePose) {
        return player.getEyeHeight(ignorePose);
    }

    @Override
    public Location getEyeLocation() {
        return player.getEyeLocation();
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
        return player.getLineOfSight(transparent, maxDistance);
    }

    @Override
    public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
        return player.getTargetBlock(transparent, maxDistance);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
        return player.getLastTwoTargetBlocks(transparent, maxDistance);
    }

    @Override
    public Block getTargetBlockExact(int maxDistance) {
        return player.getTargetBlockExact(maxDistance);
    }

    @Override
    public Block getTargetBlockExact(int maxDistance, FluidCollisionMode fluidCollisionMode) {
        return player.getTargetBlockExact(maxDistance, fluidCollisionMode);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance) {
        return player.rayTraceBlocks(maxDistance);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance, FluidCollisionMode fluidCollisionMode) {
        return player.rayTraceBlocks(maxDistance, fluidCollisionMode);
    }

    @Override
    public int getRemainingAir() {
        return player.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int ticks) {
        player.setRemainingAir(ticks);
    }

    @Override
    public int getMaximumAir() {
        return player.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int ticks) {
        player.setMaximumAir(ticks);
    }

    @Override
    public int getArrowCooldown() {
        return player.getArrowCooldown();
    }

    @Override
    public void setArrowCooldown(int ticks) {
        player.setArrowCooldown(ticks);
    }

    @Override
    public int getArrowsInBody() {
        return player.getArrowsInBody();
    }

    @Override
    public void setArrowsInBody(int count) {
        player.setArrowsInBody(count);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return player.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int ticks) {
        player.setMaximumNoDamageTicks(ticks);
    }

    @Override
    public double getLastDamage() {
        return player.getLastDamage();
    }

    @Override
    public void setLastDamage(double damage) {
        player.setLastDamage(damage);
    }

    @Override
    public int getNoDamageTicks() {
        return player.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int ticks) {
        player.setNoDamageTicks(ticks);
    }

    @Override
    public Player getKiller() {
        return player.getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return player.addPotionEffect(effect);
    }

    @Override
    @Deprecated
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return player.addPotionEffect(effect, force);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return player.addPotionEffects(effects);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType type) {
        return player.hasPotionEffect(type);
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType type) {
        return player.getPotionEffect(type);
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
        player.removePotionEffect(type);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return player.getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity other) {
        return player.hasLineOfSight(other);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return player.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean remove) {
        player.setRemoveWhenFarAway(remove);
    }

    @Override
    public EntityEquipment getEquipment() {
        return player.getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean pickup) {
        player.setCanPickupItems(pickup);
    }

    @Override
    public boolean getCanPickupItems() {
        return player.getCanPickupItems();
    }

    @Override
    public boolean isLeashed() {
        return player.isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return player.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity holder) {
        return player.setLeashHolder(holder);
    }

    @Override
    public boolean isGliding() {
        return player.isGliding();
    }

    @Override
    public void setGliding(boolean gliding) {
        player.setGliding(gliding);
    }

    @Override
    public boolean isSwimming() {
        return player.isSwimming();
    }

    @Override
    public void setSwimming(boolean swimming) {
        player.setSwimming(swimming);
    }

    @Override
    public boolean isRiptiding() {
        return player.isRiptiding();
    }

    @Override
    public boolean isSleeping() {
        return player.isSleeping();
    }

    @Override
    public void setAI(boolean ai) {
        player.setAI(ai);
    }

    @Override
    public boolean hasAI() {
        return player.hasAI();
    }

    @Override
    public void attack(Entity target) {
        player.attack(target);
    }

    @Override
    public void swingMainHand() {
        player.swingMainHand();
    }

    @Override
    public void swingOffHand() {
        player.swingOffHand();
    }

    @Override
    public void setCollidable(boolean collidable) {
        player.setCollidable(collidable);
    }

    @Override
    public boolean isCollidable() {
        return player.isCollidable();
    }

    @Override
    public Set<UUID> getCollidableExemptions() {
        return player.getCollidableExemptions();
    }

    @Override
    public <T> T getMemory(MemoryKey<T> memoryKey) {
        return player.getMemory(memoryKey);
    }

    @Override
    public <T> void setMemory(MemoryKey<T> memoryKey, T memoryValue) {
        player.setMemory(memoryKey, memoryValue);
    }

    @Override
    public EntityCategory getCategory() {
        return player.getCategory();
    }

    @Override
    public void setInvisible(boolean invisible) {
        player.setInvisible(invisible);
    }

    @Override
    public boolean isInvisible() {
        return player.isInvisible();
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        return player.getAttribute(attribute);
    }

    @Override
    public void damage(double amount) {
        player.damage(amount);
    }

    @Override
    public void damage(double amount, Entity source) {
        player.damage(amount, source);
    }

    @Override
    public double getHealth() {
        return player.getHealth();
    }

    @Override
    public void setHealth(double health) {
        player.setHealth(health);
    }

    @Override
    public double getAbsorptionAmount() {
        return player.getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(double amount) {
        player.setAbsorptionAmount(amount);
    }

    @Override
    @Deprecated
    public double getMaxHealth() {
        return player.getMaxHealth();
    }

    @Override
    @Deprecated
    public void setMaxHealth(double health) {
        player.setMaxHealth(health);
    }

    @Override
    @Deprecated
    public void resetMaxHealth() {
        player.resetMaxHealth();
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public Location getLocation(Location loc) {
        return player.getLocation(loc);
    }

    @Override
    public void setVelocity(Vector velocity) {
        player.setVelocity(velocity);
    }

    @Override
    public Vector getVelocity() {
        return player.getVelocity();
    }

    @Override
    public double getHeight() {
        return player.getHeight();
    }

    @Override
    public double getWidth() {
        return player.getWidth();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return player.getBoundingBox();
    }

    @Override
    public boolean isInWater() {
        return player.isInWater();
    }

    @Override
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        player.setRotation(yaw, pitch);
    }

    @Override
    public boolean teleport(Location location) {
        return player.teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        return player.teleport(location, cause);
    }

    @Override
    public boolean teleport(Entity destination) {
        return player.teleport(destination);
    }

    @Override
    public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
        return player.teleport(destination, cause);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return player.getNearbyEntities(x, y, z);
    }

    @Override
    public int getEntityId() {
        return player.getEntityId();
    }

    @Override
    public int getFireTicks() {
        return player.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return player.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int ticks) {
        player.setFireTicks(ticks);
    }

    @Override
    public void remove() {
        player.remove();
    }

    @Override
    public boolean isDead() {
        return player.isDead();
    }

    @Override
    public boolean isValid() {
        return player.isValid();
    }

    @Override
    public Server getServer() {
        return player.getServer();
    }

    @Override
    public boolean isPersistent() {
        return player.isPersistent();
    }

    @Override
    public void setPersistent(boolean persistent) {
        player.setPersistent(persistent);
    }

    @Override
    @Deprecated
    public Entity getPassenger() {
        return player.getPassenger();
    }

    @Override
    @Deprecated
    public boolean setPassenger(Entity passenger) {
        return player.setPassenger(passenger);
    }

    @Override
    public List<Entity> getPassengers() {
        return player.getPassengers();
    }

    @Override
    public boolean addPassenger(Entity passenger) {
        return player.addPassenger(passenger);
    }

    @Override
    public boolean removePassenger(Entity passenger) {
        return player.removePassenger(passenger);
    }

    @Override
    public boolean isEmpty() {
        return player.isEmpty();
    }

    @Override
    public boolean eject() {
        return player.eject();
    }

    @Override
    public float getFallDistance() {
        return player.getFallDistance();
    }

    @Override
    public void setFallDistance(float distance) {
        player.setFallDistance(distance);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
        player.setLastDamageCause(event);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return player.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public int getTicksLived() {
        return player.getTicksLived();
    }

    @Override
    public void setTicksLived(int value) {
        player.setTicksLived(value);
    }

    @Override
    public void playEffect(EntityEffect type) {
        player.playEffect(type);
    }

    @Override
    public EntityType getType() {
        return player.getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return player.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return player.leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return player.getVehicle();
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        player.setCustomNameVisible(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return player.isCustomNameVisible();
    }

    @Override
    public void setGlowing(boolean flag) {
        player.setGlowing(flag);
    }

    @Override
    public boolean isGlowing() {
        return player.isGlowing();
    }

    @Override
    public void setInvulnerable(boolean flag) {
        player.setInvulnerable(flag);
    }

    @Override
    public boolean isInvulnerable() {
        return player.isInvulnerable();
    }

    @Override
    public boolean isSilent() {
        return player.isSilent();
    }

    @Override
    public void setSilent(boolean flag) {
        player.setSilent(flag);
    }

    @Override
    public boolean hasGravity() {
        return player.hasGravity();
    }

    @Override
    public void setGravity(boolean gravity) {
        player.setGravity(gravity);
    }

    @Override
    public int getPortalCooldown() {
        return player.getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(int cooldown) {
        player.setPortalCooldown(cooldown);
    }

    @Override
    public Set<String> getScoreboardTags() {
        return player.getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(String tag) {
        return player.addScoreboardTag(tag);
    }

    @Override
    public boolean removeScoreboardTag(String tag) {
        return player.removeScoreboardTag(tag);
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return player.getPistonMoveReaction();
    }

    @Override
    public BlockFace getFacing() {
        return player.getFacing();
    }

    @Override
    public Pose getPose() {
        return player.getPose();
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        player.setMetadata(metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return player.getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return player.hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        player.removeMetadata(metadataKey, owningPlugin);
    }

    @Override
    public void sendMessage(String message) {
        //TODO: Override
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        player.sendMessage(messages);
    }

    @Override
    public void sendMessage(UUID sender, String message) {
        player.sendMessage(sender, message);
    }

    @Override
    public void sendMessage(UUID sender, String[] messages) {
        player.sendMessage(sender, messages);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return player.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return player.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return player.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return player.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return player.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return player.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return player.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return player.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        player.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        player.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return player.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public void setOp(boolean value) {
        player.setOp(value);
    }

    @Override
    public String getCustomName() {
        return player.getCustomName();
    }

    @Override
    public void setCustomName(String name) {
        player.setCustomName(name);
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return player.getPersistentDataContainer();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return player.launchProjectile(projectile);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        return player.launchProjectile(projectile, velocity);
    }

    @Override
    public boolean isConversing() {
        return player.isConversing();
    }

    @Override
    public void acceptConversationInput(String input) {
        player.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return player.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        player.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        player.abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(UUID sender, String message) {
        player.sendRawMessage(sender, message);
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public boolean isBanned() {
        return player.isBanned();
    }

    @Override
    public boolean isWhitelisted() {
        return player.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean value) {
        player.setWhitelisted(value);
    }

    @Override
    public Player getPlayer() {
        return player.getPlayer();
    }

    @Override
    public long getFirstPlayed() {
        return player.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return player.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return player.hasPlayedBefore();
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        player.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        player.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        player.incrementStatistic(statistic, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        player.decrementStatistic(statistic, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
        player.setStatistic(statistic, newValue);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return player.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return player.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {
        player.setStatistic(statistic, material, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        player.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return player.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        player.decrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        player.setStatistic(statistic, entityType, newValue);
    }

    @Override
    public Map<String, Object> serialize() {
        return player.serialize();
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        player.sendPluginMessage(source, channel, message);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return player.getListeningPluginChannels();
    }
}
