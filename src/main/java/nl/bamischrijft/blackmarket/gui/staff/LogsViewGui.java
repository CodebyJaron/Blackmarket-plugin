package nl.bamischrijft.blackmarket.gui.staff;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.gui.staff.item.ItemEditGui;
import nl.bamischrijft.blackmarket.manager.models.Log;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import nl.bamischrijft.blackmarket.manager.models.ShopItem;
import nl.bamischrijft.blackmarket.util.BalanceUtil;
import nl.bamischrijft.blackmarket.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class LogsViewGui extends Gui {

    private final Shop shop;
    private final ShopItem shopItem;

    private int page = 0;

    private final MenuScheme glassScheme = new MenuScheme().maskEmpty(2).mask("111111111");
    private final MenuScheme optionsScheme = new MenuScheme().maskEmpty(3).mask("000010000");
    private final MenuScheme paginationScheme = new MenuScheme().maskEmpty(3).mask("100000001");
    private final MenuScheme itemScheme = new MenuScheme().masks("111111111", "111111111");

    public LogsViewGui(Player player, Shop shop, ShopItem shopItem) {
        super(player, 4, "&c&lLogs &8- &7" + TextUtil.truncate(shop.id.toString(), 16));

        BlackmarketPlugin plugin = BlackmarketPlugin.getInstance();

        this.shop = shop;
        this.shopItem = shopItem;
    }

    @Override
    public void redraw() {
        Item glass = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(15).name(" ").buildItem().build();
        MenuPopulator glassPopulator = glassScheme.newPopulator(this);
        while(glassPopulator.hasSpace()) glassPopulator.accept(glass);

        List<Log> logs = shopItem.logs;
        if (Objects.isNull(logs)) {
            logs = new ArrayList<>();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM");
        List<Item> items = logs.stream().sorted(Comparator.comparingLong(value -> value.dateTime)).map(log ->
                ItemStackBuilder.of(Material.PAPER).name("&b&l" + simpleDateFormat.format(new Date(log.dateTime)))
        .lore("", "&3Item ID: &7" + log.itemUuid, "&3Speler: &7" + Bukkit.getOfflinePlayer(UUID.fromString(log.uuid)).getName(), "",
                "&3Saldo vóór betaling: &7" + BalanceUtil.convert(log.balanceBefore), "&3Saldo na betaling: &7" + BalanceUtil.convert(log.balanceAfter)).buildItem().build())
                .skip(page * 18L).limit(18).collect(Collectors.toList());
        MenuPopulator itemPopulator = itemScheme.newPopulator(this);
        items.forEach(itemPopulator::acceptIfSpace);

        MenuPopulator optionPopulator = optionsScheme.newPopulator(this);
        Item back = ItemStackBuilder.of(Material.LADDER).name("Terug naar vorige menu")
                .build(() -> new ItemEditGui(getPlayer(), shop, shopItem).open());
        optionPopulator.acceptIfSpace(back);

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
        if (page < (int) Math.floor(logs.size() / 45d)) {
            paginationPopulator.accept(ItemStackBuilder.of(Material.ARROW)
                    .name("Volgende pagina").build(() ->
                    {
                        this.page++;
                        this.redraw();
                    }));
        } else {
            paginationPopulator.accept(na);
        }
    }

}
