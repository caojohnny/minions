package com.gmail.woodyc40.minions.data;

import com.eatthepath.uuid.FastUUID;
import com.gmail.woodyc40.minions.Minions;
import com.gmail.woodyc40.minions.mode.Mode;
import com.gmail.woodyc40.minions.mode.ModeProvider;
import com.gmail.woodyc40.minions.mode.ModeType;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.UUID;
import java.util.logging.Level;

import static java.lang.String.format;

public class DataManager {
    private static final String CREATE_TABLE_STRING =
            "CREATE TABLE IF NOT EXISTS `%s` (" +
                    "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "`owner` VARCHAR(36) NOT NULL, " +
                    "`inventory` TEXT NULL, " +
                    "`experience` INT NOT NULL DEFAULT 0, " +
                    "`mode` VARCHAR(20) NULL" +
                    ")";
    private static final String SELECT_DATA_STRING =
            "SELECT * FROM `%s` WHERE `uuid`=?";
    private static final String INSERT_DATA_STRING =
            "INSERT INTO `%s` (uuid, owner, inventory, experience, mode) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE `uuid`=?, `owner`=?, `inventory`=?, `experience`=?, `mode`=?";
    private static final String DELETE_DATA_STRING =
            "DELETE FROM `%s` WHERE `uuid`=?";

    private final Minions plugin;
    private final ModeProvider mp;
    private final DataSource dataSource;
    private final String table;

    public DataManager(Minions plugin, ModeProvider mp, DataSource dataSource, String table) {
        this.plugin = plugin;
        this.mp = mp;
        this.dataSource = dataSource;
        this.table = table;
    }

    public void createTable() {
        try (Connection con = this.dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(format(CREATE_TABLE_STRING, this.table))) {
            ps.execute();
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error occurred creating table", e);
        }
    }

    @Nullable
    public MinionData getData(UUID persistentId) {
        try (Connection con = this.dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(format(SELECT_DATA_STRING, this.table))) {
            ps.setString(1, FastUUID.toString(persistentId));

            MinionData data = new MinionData();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UUID owner = FastUUID.parseUUID(rs.getString(2));
                    String inventoryB64 = rs.getString(3);
                    int experience = rs.getInt(4);
                    String modeString = rs.getString(5);

                    Inventory inventory = null;
                    if (inventoryB64 != null) {
                        ItemStack[] contents = fromB64(inventoryB64);

                        inventory = Bukkit.createInventory(null, contents.length);
                        inventory.setContents(contents);
                    }

                    Mode mode = null;
                    if (modeString != null) {
                        ModeType type = ModeType.valueOf(modeString);
                        mode = this.mp.provideMode(type);
                    }

                    data.setOwner(owner);
                    data.setInventory(inventory);
                    data.setExperience(experience);
                    data.setMode(mode);

                    return data;
                }
            }

            return data;
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error occurred SELECTing data", e);
            return null;
        }
    }

    public void writeData(UUID persistentId, MinionData data) {
        try (Connection con = this.dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(format(INSERT_DATA_STRING, this.table))) {
            String persistentIdString = FastUUID.toString(persistentId);
            String ownerString = FastUUID.toString(data.getOwner());
            String inventoryB64 = data.getInventory() == null ? null :
                    toB64(data.getInventory().getContents());
            int experience = data.getExperience();
            String modeString = data.getMode() == null ? null :
                    data.getMode().getType().name();

            ps.setString(1, persistentIdString);
            ps.setString(2, ownerString);
            if (inventoryB64 == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setObject(3, inventoryB64, Types.VARCHAR);
            }
            ps.setInt(4, experience);
            if (modeString == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setObject(5, modeString, Types.VARCHAR);
            }
            ps.setString(6, persistentIdString);
            ps.setString(7, ownerString);
            if (inventoryB64 == null) {
                ps.setNull(8, Types.VARCHAR);
            } else {
                ps.setObject(8, inventoryB64, Types.VARCHAR);
            }
            ps.setInt(9, experience);
            if (modeString == null) {
                ps.setNull(10, Types.VARCHAR);
            } else {
                ps.setObject(10, modeString, Types.VARCHAR);
            }

            ps.executeUpdate();
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error occurred INSERTing data", e);
        }
    }

    public void clearData(UUID persistentId) {
        try (Connection con = this.dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(format(DELETE_DATA_STRING, this.table))) {
            ps.setString(1, FastUUID.toString(persistentId));
            ps.executeUpdate();
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error occurred DELETEing data", e);
        }
    }

    public void close() {
        ((HikariDataSource) this.dataSource).close();
    }

    // Lifted code off of: https://gist.github.com/graywolf336/8153678
    private String toB64(ItemStack... items) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeInt(items.length);

        for (int i = 0; i < items.length; i++) {
            dataOutput.writeObject(items[i]);
        }

        dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    private ItemStack[] fromB64(String data) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            return items;
        }
    }
}
