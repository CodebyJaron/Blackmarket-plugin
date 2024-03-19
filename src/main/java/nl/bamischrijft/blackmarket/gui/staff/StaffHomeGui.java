package nl.bamischrijft.blackmarket.gui.staff;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StaffHomeGui extends Gui {

    private final BlackmarketPlugin plugin;
    private int page;

    public StaffHomeGui(Player player) {
        super(player, 6, "&c&lBlackmarket &8- &7Beheren");
        this.plugin = BlackmarketPlugin.getInstance();
        this.page = 0;
    }

    private final MenuScheme glassScheme = new MenuScheme().maskEmpty(5).mask("111111111");
    private final MenuScheme optionScheme = new MenuScheme().masks("111111111", "111111111", "111111111", "111111111", "111111111");
    private final MenuScheme paginationScheme = new MenuScheme().maskEmpty(5).mask("000101001");

    @Override
    public void redraw() {
        if (!isFirstDraw()) {
            clearItems();
        }

        Item glassItem = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                .durability(15).name(" ").buildItem().build();
        MenuPopulator glassPopulator = glassScheme.newPopulator(this);
        while(glassPopulator.hasSpace()) glassPopulator.accept(glassItem);

        MenuPopulator optionPopulator = optionScheme.newPopulator(this);
        List<Shop> shops = plugin.getShopManager().getShops();
        List<Item> items = shops.stream().map(shop -> ItemStackBuilder.of(Material.CHEST)
        .name("&b" + (Objects.isNull(shop.date) ? "Geen datum ingesteld" : shop.date)).lore("", "&7&nKlik om te bewerken").build(() -> new ShopManageGui(getPlayer(), shop).open()))
        .skip(page * 45L).limit(45).collect(Collectors.toList());
        items.forEach(optionPopulator::acceptIfSpace);

        Item na = ItemStackBuilder.of(Material.BARRIER).name("&c✖ Niet beschikbaar").buildItem().build();
        MenuPopulator paginationPopulator = paginationScheme.newPopulator(this);
        if (page != 0) {
            paginationPopulator.accept(ItemStackBuilder.of(Material.ARROW)
            .name("Vorige pagina").build(() ->
                    {
                        this.page--;
                        this.redraw();
                    }));
        } else {
            paginationPopulator.accept(na);
        }
        if (page < (int) Math.floor(shops.size() / 45d)) {
            paginationPopulator.accept(ItemStackBuilder.of(Material.ARROW)
                    .name("Volgende pagina").build(() ->
                    {
                        this.page++;
                        this.redraw();
                    }));
        } else {
            paginationPopulator.accept(na);
        }

        Item createItem = ItemStackBuilder.of(Material.BOOK_AND_QUILL)
                .name("&bNieuwe dag maken")
                .build(() -> {
                   Shop shop = new Shop();
                   shop.list = new ArrayList<>();
                   plugin.getShopManager().update(shop);
                   redraw();
                });
        paginationPopulator.accept(createItem);
    }

}
