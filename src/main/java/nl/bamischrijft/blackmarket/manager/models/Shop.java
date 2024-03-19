package nl.bamischrijft.blackmarket.manager.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

@Entity(value = "Shops", noClassnameStored = true)
public class Shop {

    @Id
    public ObjectId id;

    public String date;

    @Embedded
    public List<ShopItem> list;

}
