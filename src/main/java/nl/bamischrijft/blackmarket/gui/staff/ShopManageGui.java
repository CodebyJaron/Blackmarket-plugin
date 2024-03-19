package nl.bamischrijft.blackmarket.gui.staff;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.text3.Text;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import nl.bamischrijft.blackmarket.util.ChatUtil;
import nl.bamischrijft.blackmarket.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;

public class ShopManageGui extends Gui {

    private final BlackmarketPlugin plugin;
    private final Shop shop;

    public ShopManageGui(Player player, Shop shop) {
        super(player, 3, "&c&lBlackmarket &8- &7" + TextUtil.truncate(shop.id.toString(), 16));
        this.plugin = BlackmarketPlugin.getInstance();
        this.shop = shop;
    }

    private final MenuScheme glassScheme = new MenuScheme().masks("111111111", "110111011", "111111111");
    private final MenuScheme optionsScheme = new MenuScheme().maskEmpty(1).mask("001000100");

    @Override
    public void redraw() {
        if (!isFirstDraw()) {
            clearItems();
        }

        Item glass = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(15).name(" ").buildItem().build();
        MenuPopulator glassPopulator = glassScheme.newPopulator(this);
        while(glassPopulator.hasSpace()) glassPopulator.accept(glass);
        
        Item date = ItemStackBuilder.of(Material.WATCH).name("&b&lDatum").lore("", "&7Huidige waarde: &f" + (Objects.isNull(shop.date) ? "Geen" : shop.date),
                "", "&3&nKlik om te bewerken").build(() ->
                ChatUtil.chatInput(getPlayer(), "&3Wat wil je als nieuwe &bdatum &3instellen voor deze Blackmarket dag? (&bdd/mm/yyyy&3)",
                        event ->
                        {
                            String message = event.getMessage();

                            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            sdf.setLenient(false);

                            try {
                                sdf.parse(message);
                            } catch (ParseException exception) {
                                getPlayer().sendMessage(Text.colorize("&cDit is geen geldige datum, hou je aan het format (dd/mm/yyyy)"));
                                new ShopManageGui(getPlayer(), shop).open();
                                return;
                            }

                            shop.date = message;
                            plugin.getShopManager().update(shop);
                            getPlayer().sendMessage(Text.colorize("&3Je hebt succesvol de datum veranderd naar &b" + message + "&3."));
                            new ShopManageGui(getPlayer(), shop).open();
                        }));
        Item items = ItemStackBuilder.of(Material.CHEST).name("&b&lItems").lore("", "&3&nKlik om te bewerken")
                .build(() -> new ItemsEditGui(getPlayer(), shop).open());
        MenuPopulator optionPopulator = optionsScheme.newPopulator(this);
        Arrays.stream(new Item[]{date,items}).forEach(optionPopulator::acceptIfSpace);
    }

}
