package nl.bamischrijft.blackmarket.manager.dao;

import nl.bamischrijft.blackmarket.manager.models.Shop;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class ShopDAO extends BasicDAO<Shop, String> {

    public ShopDAO(Datastore ds) {
        super(Shop.class, ds);
    }

}
