package nl.bamischrijft.blackmarket.manager.models;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class Log {

    public String uuid;

    public String itemUuid;

    public double balanceBefore;

    public double balanceAfter;

    public long dateTime;

}
