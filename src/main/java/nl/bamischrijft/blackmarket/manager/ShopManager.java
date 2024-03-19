package nl.bamischrijft.blackmarket.manager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.Getter;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.manager.dao.ShopDAO;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class ShopManager {

    private final MongoClient mc;
    private final Morphia morphia;
    private final Datastore datastore;

    private final ShopDAO shopDAO;

    public ShopManager(BlackmarketPlugin plugin) {
        this.mc = new MongoClient(new MongoClientURI(plugin.getConfig().getString("mongodb.uri")));

        this.morphia = new Morphia();
        this.morphia.map(Shop.class);

        datastore = morphia.createDatastore(mc, plugin.getConfig().getString("mongodb.database"));
        datastore.ensureIndexes();

        this.shopDAO = new ShopDAO(datastore);
    }

    public List<Shop> getShops() {
        return shopDAO.find().asList();
    }

    public void update(Shop shop) {
        shopDAO.save(shop);
    }

    public Shop findByDate(String date) {
        return shopDAO.findOne("date", date);
    }

}
