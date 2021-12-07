package org.abos.fabricmc.time;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Properties;

/**
 * This class manages the mod configuration. The configuration consists of two parts: The static configuration
 * and the dynamic configuration. The static configuration cannot be changed from within a game instance, but instead
 * is supposed to be changed manually by an admin on the server side. The dynamic configuration can be controlled
 * via {@link GameRules}, but defaults can be set in the static configuration.
 */
// watch out when changing this class, it is probably the most delicate in the entire mod
public final class Config {

    public static final String FILE_NAME = "time.properties";

    public static final String FILE_DESC = "Configuration File for "+Time.MOD_NAME+" >="+Time.MOD_VERSION;

    public static final Path FILE_PATH = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

    public static final String SUBSTITUTE_DEFAULT_MSG = "{} is not a legal value for {}, default {} will be used";

    /*
     * How to add property:
     * 1. add the static variants
     * 2. add default initialization to static {...}
     * 3. add the non-static variants
     * 4. if it's a rule, add rule initialization to constructor
     * 5. add sanitizing part in sanitize method; load method doesn't need to be touched
     * 6. add language asset for the rule
     */

    //----------------------------------------------------------
    // each custom property (static)
    //----------------------------------------------------------

    // the name of the game rule
    public static final String TU_INCREASE_STR = "tu_increase"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String TU_INCREASE_DEFAULT_STR = TU_INCREASE_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int TU_INCREASE_DEFAULT = 1;

    // the name of the game rule
    public static final String DRAGON_EGG_SYPHON_TICKS_STR = "dragon_egg_syphon_ticks"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String DRAGON_EGG_SYPHON_TICKS_DEFAULT_STR = DRAGON_EGG_SYPHON_TICKS_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int DRAGON_EGG_SYPHON_TICKS_DEFAULT = 120; // *100 = half an in-game day at normal tick rate

    // the name of the game rule
    public static final String TICKS_PER_EXTRACTED_TU_STR = "ticks_per_extracted_tu"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String TICKS_PER_EXTRACTED_TU_DEFAULT_STR = TICKS_PER_EXTRACTED_TU_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int TICKS_PER_EXTRACTED_TU_DEFAULT = 1000; // an in-game day hour at normal tick rate

    // the name of the game rule
    public static final String PERCENTAGE_SHARD_CROP_STR = "percentage_shard_crop"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String PERCENTAGE_SHARD_CROP_DEFAULT_STR = PERCENTAGE_SHARD_CROP_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int PERCENTAGE_SHARD_CROP_DEFAULT = 10; // in percent

    // the name of the game rule
    public static final String PERCENTAGE_SHARD_STORAGE_STR = "percentage_shard_storage"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String PERCENTAGE_SHARD_STORAGE_DEFAULT_STR = PERCENTAGE_SHARD_STORAGE_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int PERCENTAGE_SHARD_STORAGE_DEFAULT = 100; // in percent
    // the block gets destroyed, so 100% is recommended

    // the name of the game rule
    public static final String PERCENTAGE_SHARD_MOB_STR = "percentage_shard_mob"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String PERCENTAGE_SHARD_MOB_DEFAULT_STR = PERCENTAGE_SHARD_MOB_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int PERCENTAGE_SHARD_MOB_DEFAULT = 5; // in percent

    // the name of the game rule
    public static final String PERCENTAGE_SHARD_CATTLE_STR = "percentage_shard_cattle"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String PERCENTAGE_SHARD_CATTLE_DEFAULT_STR = PERCENTAGE_SHARD_CATTLE_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int PERCENTAGE_SHARD_CATTLE_DEFAULT = 50; // in percent

    // the name of the game rule
    public static final String PERCENTAGE_SHARD_SPAWNER_STR = "percentage_shard_spawner"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String PERCENTAGE_SHARD_SPAWNER_DEFAULT_STR = PERCENTAGE_SHARD_SPAWNER_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int PERCENTAGE_SHARD_SPAWNER_DEFAULT = 100; // in percent
    // the spawner gets destroyed, so 100% is recommended

    // the name of the game rule
    public static final String COMPACT_FARM_TICKS_STR = "compact_farm_ticks"; // if changed, also change language assets
    // the name of the default value for the game rule
    public static final String COMPACT_FARM_TICKS_DEFAULT_STR = COMPACT_FARM_TICKS_STR + "_default";
    // the default game rule default in case config couldn't be loaded
    public static final int COMPACT_FARM_TICKS_DEFAULT = 250; // an in-game quarter-hour at normal tick rate

    //----------------------------------------------------------
    // Properties field and default Properties field
    //----------------------------------------------------------

    /**
     * Stores the default config. Do NOT change this variable during runtime, only use by getter!
     * @see #getDefaultProperties()
     */
    private static final Properties DEFAULT_PROPERTIES;

    // this is the place where the default properties are hard-coded
    static {
        DEFAULT_PROPERTIES = new Properties();
        DEFAULT_PROPERTIES.setProperty(TU_INCREASE_DEFAULT_STR, Integer.toString(TU_INCREASE_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(DRAGON_EGG_SYPHON_TICKS_DEFAULT_STR, Integer.toString(DRAGON_EGG_SYPHON_TICKS_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(TICKS_PER_EXTRACTED_TU_DEFAULT_STR, Integer.toString(TICKS_PER_EXTRACTED_TU_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(PERCENTAGE_SHARD_CROP_DEFAULT_STR, Integer.toString(PERCENTAGE_SHARD_CROP_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(PERCENTAGE_SHARD_STORAGE_DEFAULT_STR, Integer.toString(PERCENTAGE_SHARD_STORAGE_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(PERCENTAGE_SHARD_MOB_DEFAULT_STR, Integer.toString(PERCENTAGE_SHARD_MOB_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(PERCENTAGE_SHARD_CATTLE_DEFAULT_STR, Integer.toString(PERCENTAGE_SHARD_CATTLE_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(PERCENTAGE_SHARD_SPAWNER_DEFAULT_STR, Integer.toString(PERCENTAGE_SHARD_SPAWNER_DEFAULT));
        DEFAULT_PROPERTIES.setProperty(COMPACT_FARM_TICKS_DEFAULT_STR, Integer.toString(COMPACT_FARM_TICKS_DEFAULT));
    }

    /**
     * Returns the config defaults that are used in case a config file couldn't be found or if the file
     * is missing some values.
     * @return An empty {@link Properties} object defaulting to the hard-coded config defaults. This
     * object can safely be altered, the hard-coded config defaults are not affected.
     * @see Properties#Properties(Properties)
     */
    public static Properties getDefaultProperties() {
        return new Properties(DEFAULT_PROPERTIES);
    }

    /**
     * The properties of this config, containing for example defaults for rules. Don't change during runtime
     * except in the constructor.
     * @see #Config()
     */
    private final Properties properties;

    //----------------------------------------------------------
    // each custom property (non-static)
    //----------------------------------------------------------

    // the game rule
    private final GameRules.Key<GameRules.IntRule> tuIncreaseRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getTUIncreaseRule() {return tuIncreaseRule;}
    // the game rule default
    public int getTUIncreaseDefault() {return Integer.parseInt(properties.getProperty(TU_INCREASE_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> dragonEggSyphonTicksRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getDragonEggSyphonTicksRule() {return dragonEggSyphonTicksRule;}
    // the game rule default
    public int getDragonEggSyphonTicksDefault() {return Integer.parseInt(properties.getProperty(DRAGON_EGG_SYPHON_TICKS_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> ticksPerExtractedTURule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getTicksPerExtractedTURule() {return ticksPerExtractedTURule;}
    // the game rule default
    public int getTicksPerExtractedTUDefault() {return Integer.parseInt(properties.getProperty(TICKS_PER_EXTRACTED_TU_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> percentageShardCropRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getPercentageShardCropRule() {return percentageShardCropRule;}
    // the game rule default
    public int getPercentageShardCropDefault() {return Integer.parseInt(properties.getProperty(PERCENTAGE_SHARD_CROP_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> percentageShardStorageRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getPercentageShardStorageRule() {return percentageShardStorageRule;}
    // the game rule default
    public int getPercentageShardStorageDefault() {return Integer.parseInt(properties.getProperty(PERCENTAGE_SHARD_STORAGE_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> percentageShardMobRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getPercentageShardMobRule() {return percentageShardMobRule;}
    // the game rule default
    public int getPercentageShardMobDefault() {return Integer.parseInt(properties.getProperty(PERCENTAGE_SHARD_MOB_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> percentageShardCattleRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getPercentageShardCattleRule() {return percentageShardCattleRule;}
    // the game rule default
    public int getPercentageShardCattleDefault() {return Integer.parseInt(properties.getProperty(PERCENTAGE_SHARD_CATTLE_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> percentageShardSpawnerRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getPercentageShardSpawnerRule() {return percentageShardSpawnerRule;}
    // the game rule default
    public int getPercentageShardSpawnerDefault() {return Integer.parseInt(properties.getProperty(PERCENTAGE_SHARD_SPAWNER_DEFAULT_STR));}

    // the game rule
    private final GameRules.Key<GameRules.IntRule> compactFarmTicksRule;
    // the game rule accessor
    public GameRules.Key<GameRules.IntRule> getCompactFarmTicksRule() {return compactFarmTicksRule;}
    // the game rule default
    public int getCompactFarmTicksDefault() {return Integer.parseInt(properties.getProperty(COMPACT_FARM_TICKS_DEFAULT_STR));}

    //----------------------------------------------------------
    // Constructor
    //----------------------------------------------------------

    public Config() {
        properties = loadProperties();
        tuIncreaseRule = GameRuleRegistry.register(TU_INCREASE_STR, GameRules.Category.UPDATES,
                GameRuleFactory.createIntRule(getTUIncreaseDefault()));
        dragonEggSyphonTicksRule = GameRuleRegistry.register(DRAGON_EGG_SYPHON_TICKS_STR, GameRules.Category.UPDATES,
                GameRuleFactory.createIntRule(getDragonEggSyphonTicksDefault()));
        ticksPerExtractedTURule = GameRuleRegistry.register(TICKS_PER_EXTRACTED_TU_STR, GameRules.Category.UPDATES,
                GameRuleFactory.createIntRule(getTicksPerExtractedTUDefault()));
        percentageShardCropRule = GameRuleRegistry.register(PERCENTAGE_SHARD_CROP_STR, GameRules.Category.DROPS,
                GameRuleFactory.createIntRule(getPercentageShardCropDefault()));
        percentageShardStorageRule = GameRuleRegistry.register(PERCENTAGE_SHARD_STORAGE_STR, GameRules.Category.DROPS,
                GameRuleFactory.createIntRule(getPercentageShardStorageDefault()));
        percentageShardMobRule = GameRuleRegistry.register(PERCENTAGE_SHARD_MOB_STR, GameRules.Category.DROPS,
                GameRuleFactory.createIntRule(getPercentageShardMobDefault()));
        percentageShardCattleRule = GameRuleRegistry.register(PERCENTAGE_SHARD_CATTLE_STR, GameRules.Category.DROPS,
                GameRuleFactory.createIntRule(getPercentageShardCattleDefault()));
        percentageShardSpawnerRule = GameRuleRegistry.register(PERCENTAGE_SHARD_SPAWNER_STR, GameRules.Category.DROPS,
                GameRuleFactory.createIntRule(getPercentageShardSpawnerDefault()));
        compactFarmTicksRule = GameRuleRegistry.register(COMPACT_FARM_TICKS_STR, GameRules.Category.UPDATES,
                GameRuleFactory.createIntRule(getCompactFarmTicksDefault()));
    }

    //----------------------------------------------------------
    // Sanitizer and Loader methods
    //----------------------------------------------------------

    /**
     * Sanitizes a given {@link Properties} object against the needs of this config, replacing each missing or malformed
     * property as needed.
     * @param properties the properties to sanitize, not {@code null} and won't be modified by this method
     * @return a sanitized version of the input {@code properties}
     */
    // don't change #properties after sanitizing
    @Contract(value = "null -> fail", pure = true)
    protected Properties sanitize(Properties properties) {
        Utils.requireNonNull(properties, "properties");
        Properties sanitized = getDefaultProperties();
        String property = null;
        // sanitize time increase default
        try {
            property = properties.getProperty(TU_INCREASE_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(TU_INCREASE_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property, TU_INCREASE_DEFAULT_STR, TU_INCREASE_DEFAULT);
        }
        // sanitize dragon egg syphon ticks default
        try {
            property = properties.getProperty(DRAGON_EGG_SYPHON_TICKS_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(DRAGON_EGG_SYPHON_TICKS_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property,DRAGON_EGG_SYPHON_TICKS_DEFAULT_STR,DRAGON_EGG_SYPHON_TICKS_DEFAULT);
        }
        // sanitize dragon egg syphon ticks default
        try {
            property = properties.getProperty(TICKS_PER_EXTRACTED_TU_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(TICKS_PER_EXTRACTED_TU_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property,TICKS_PER_EXTRACTED_TU_DEFAULT_STR,TICKS_PER_EXTRACTED_TU_DEFAULT);
        }
        // sanitize crop shard percentage
        try {
            property = properties.getProperty(PERCENTAGE_SHARD_CROP_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(PERCENTAGE_SHARD_CROP_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property, PERCENTAGE_SHARD_CROP_DEFAULT_STR,PERCENTAGE_SHARD_CROP_DEFAULT);
        }
        // sanitize storage shard percentage
        try {
            property = properties.getProperty(PERCENTAGE_SHARD_STORAGE_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(PERCENTAGE_SHARD_STORAGE_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property, PERCENTAGE_SHARD_STORAGE_DEFAULT_STR,PERCENTAGE_SHARD_STORAGE_DEFAULT);
        }
        // sanitize (hostile) mob shard percentage
        try {
            property = properties.getProperty(PERCENTAGE_SHARD_MOB_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(PERCENTAGE_SHARD_MOB_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property, PERCENTAGE_SHARD_MOB_DEFAULT_STR,PERCENTAGE_SHARD_MOB_DEFAULT);
        }
        // sanitize (friendly) mob shard percentage
        try {
            property = properties.getProperty(PERCENTAGE_SHARD_CATTLE_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(PERCENTAGE_SHARD_CATTLE_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property, PERCENTAGE_SHARD_CATTLE_DEFAULT_STR,PERCENTAGE_SHARD_CATTLE_DEFAULT);
        }
        // sanitize spawner shard percentage
        try {
            property = properties.getProperty(PERCENTAGE_SHARD_SPAWNER_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(PERCENTAGE_SHARD_SPAWNER_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property, PERCENTAGE_SHARD_SPAWNER_DEFAULT_STR,PERCENTAGE_SHARD_SPAWNER_DEFAULT);
        }
        // sanitize compact farm ticks default
        try {
            property = properties.getProperty(COMPACT_FARM_TICKS_DEFAULT_STR);
            Integer.parseInt(property);
            sanitized.setProperty(COMPACT_FARM_TICKS_DEFAULT_STR,property);
        }
        catch (NumberFormatException ex) {
            Time.LOGGER.warn(SUBSTITUTE_DEFAULT_MSG,property,COMPACT_FARM_TICKS_DEFAULT_STR,COMPACT_FARM_TICKS_DEFAULT);
        }
        // collect unused properties and log them
        Hashtable<?, ?> unused = new Hashtable<>(properties);
        for (Object key : sanitized.keySet())
            unused.remove(key);
        if (!unused.isEmpty())
            Time.LOGGER.warn("Found some unknown properties in the {} config file", Time.MOD_NAME);
        // return sanitized String
        return sanitized;
    }

    /**
     * Loads the configuration from {@link #FILE_PATH} and fills up missing configurations via {@link #getDefaultProperties()}.
     * Note that this method is called in the constructor immediately.
     * @return the (possibly defaulted) configuration properties, not {@code null}
     */
    @NotNull
    protected Properties loadProperties() {
        Properties fileProperties = null;
        // load the properties from the file
        try {
            if (!Files.exists(FILE_PATH)) {
                Time.LOGGER.warn("No config file for {} could be found at {}, one will be created.", Time.MOD_NAME, FILE_PATH.toAbsolutePath());
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH.toFile()))) {
                    assert bw != null;
                    // only exception to use this field directly because defaults are not stored
                    DEFAULT_PROPERTIES.store(bw,FILE_DESC);
                }
                catch (IOException ex) {
                    Time.LOGGER.warn("Config file for "+Time.MOD_NAME+" couldn't be created!", ex);
                }
                catch (ClassCastException ex) { // shouldn't happen
                    Time.LOGGER.error("The hard-coded defaults are broken! This shouldn't happen! Remove "+Time.MOD_NAME+" mod and contact the developer!");
                    throw new IllegalStateException("The hard-coded defaults are broken! Send the stack trace to the developer!", ex);
                }
            } // -> if FILE_PATH doesn't exist
            else {
                if (Files.isDirectory(FILE_PATH)) {
                    Time.LOGGER.warn("Supposed config file for {} at {} is a directory. Please clean up!", Time.MOD_NAME, FILE_PATH.toAbsolutePath());
                } // -> if FILE_PATH is a directory
                else if (Files.isRegularFile(FILE_PATH)) {
                    boolean finishedReading = false;
                    try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH.toFile()))) {
                        fileProperties = new Properties();
                        assert br != null;
                        fileProperties.load(br);
                        br.close(); // makes sure to flush the reader
                        finishedReading = true;
                        // if adding to this code, make sure to check the IllegalArgumentException in the catch
                    }
                    catch (IOException ex) {
                        Time.LOGGER.warn("Config file for "+Time.MOD_NAME+" couldn't be completely read!", ex);
                    }
                    catch (IllegalArgumentException ex) {
                        Time.LOGGER.warn("A malformed Unicode escape appeared in the "+Time.MOD_NAME+" config file!",ex);
                    }
                    finally {
                        if (!finishedReading)
                            fileProperties = null;
                    }
                } // -> if FILE_PATH is regular file
                else {
                    Time.LOGGER.warn("{} config file at {} couldn't be read for some reason.", Time.MOD_NAME, FILE_PATH.toAbsolutePath());
                } // -> if FILE_PATH is weird
            } // -> if FILE_PATH does exist in some way
        }
        catch (SecurityException ex) {
            Time.LOGGER.warn("{} config file at {} couldn't be accessed.", Time.MOD_NAME, FILE_PATH.toAbsolutePath());
        }
        if (fileProperties == null) {
            fileProperties = getDefaultProperties();
            // other exception for logging purposes
            Time.LOGGER.info("Since {} config file couldn't be read, defaults are loaded: {}", Time.MOD_NAME, DEFAULT_PROPERTIES);
            // defaults are expected to be meaningful (looking at you, static {...}), so no further check
            return fileProperties;
        }
        else {
            Time.LOGGER.info("{} config file has been read in successfully.", Time.MOD_NAME);
            return sanitize(fileProperties);
        }
    }

}
