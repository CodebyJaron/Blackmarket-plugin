package nl.bamischrijft.blackmarket;

import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import nl.bamischrijft.blackmarket.commands.BlackmarketCommand;
import nl.bamischrijft.blackmarket.listeners.NpcListener;
import nl.bamischrijft.blackmarket.manager.ShopManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BlackmarketPlugin extends ExtendedJavaPlugin {

    @Getter
    private static BlackmarketPlugin instance;

    @Getter
    private Economy economy;

    @Getter
    private ShopManager shopManager;

    @Override
    @SuppressWarnings("all")
    protected void enable() {
        // Plugin startup logic
        instance = this;

        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.SEVERE);
        Logger.getLogger("org.mongodb").setLevel(Level.SEVERE);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            saveResource("config.yml", true);
        }

        this.setupEconomy();

        this.shopManager = new ShopManager(this);

        this.bindModule(new NpcListener(this));
        this.bindModule(new BlackmarketCommand(this));
    }

    public void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        this.economy = rsp.getProvider();
    }

}
