package nl.bamischrijft.blackmarket.manager.models;

import org.bukkit.entity.Player;
import org.mongodb.morphia.annotations.Embedded;

import java.util.List;
import java.util.stream.Collectors;

@Embedded
public class ShopItem {

    public String uuid;
    public String name;
    public String base64;
    public double price = 0;
    public int max = 0;
    public int stored = 0;
    public int playerLimit = 0;

    @Embedded
    public List<Log> logs;

    public boolean canPurchase(Player player) {
        String uuid = player.getUniqueId().toString();

        if (this.logs == null) return true;

        List<Log> logs = this.logs.stream().filter(log -> log.uuid.equals(uuid))
                .collect(Collectors.toList());

        return logs.size() < playerLimit;
    }

}
