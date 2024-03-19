package nl.bamischrijft.blackmarket.gui;

import me.lucko.helper.Schedulers;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.text3.Text;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.manager.models.Log;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import nl.bamischrijft.blackmarket.manager.models.ShopItem;
import nl.bamischrijft.blackmarket.util.BalanceUtil;
import nl.bamischrijft.blackmarket.util.BukkitSerialization;
import nl.bamischrijft.blackmarket.util.DateUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HomeGui extends Gui {

    private final BlackmarketPlugin plugin;
    private final Shop shop;

    public HomeGui(Player player, BlackmarketPlugin plugin) {
        super(player, 6, Text.colorize("&c&lBlackmarket &8- &7"
        + DateUtil.format(new Date(), "EEE, d MMM")));
        this.plugin = plugin;
        this.shop = plugin.getShopManager().findByDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        Schedulers.sync().runRepeating(this::redraw, 0, 20).bindWith(plugin);
    }

    private final MenuScheme glassScheme = new MenuScheme()
            .masks(
                    "111111111",
                    "110000011",
                    "110000011",
                    "111111111",
                    "111010111",
                    "111111111"
            );
    private final MenuScheme optionsScheme = new MenuScheme()
            .maskEmpty(4).mask("000101000");
    private final MenuScheme itemsScheme = new MenuScheme()
            .maskEmpty(1).masks("001111100", "001111100");

    @Override
    public void redraw() {
        if (!isFirstDraw()) {
            clearItems();
        }

        MenuPopulator glassPopulator = glassScheme.newPopulator(this);
        Item glass = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(15)
                .name(" ").buildItem().build();
        while(glassPopulator.hasSpace()) {
            glassPopulator.accept(glass);
        }

        List<ShopItem> shopItemList = Objects.isNull(shop) ? Collections.emptyList() : Objects.isNull(shop.list) ?
                Collections.emptyList() : shop.list;
        Item na = ItemStackBuilder.of(Material.BARRIER).name("&c✖ Niet beschikbaar").buildItem().build();
        List<Item> items = shopItemList.stream().map(shopItem -> {
            ItemStack itemStack;
            try {
                itemStack = BukkitSerialization.itemStackArrayFromBase64(shopItem.base64)[0];
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
            return ItemStackBuilder.of(itemStack).lore("", "&3➜ &7Prijs: &b" + BalanceUtil.convert(shopItem.price), "&3➜ &7Vooraad: &b" + shopItem.stored + "&8/&b"
                    + shopItem.max, "", "&3&nKlik om te kopen")
                    .build(() -> {

                        if (shopItem.stored <= 0) {
                            getPlayer().sendMessage(Text.colorize("&cEr is geen voorraad meer van dit item!"));
                            return;
                        }

                        if (!shopItem.canPurchase(getPlayer())) {
                            getPlayer().sendMessage(Text.colorize("&cJe zit al op de limiet aankopen van dit item!"));
                            return;
                        }

                        if (!plugin.getEconomy().has(getPlayer(), shopItem.price)) {
                            getPlayer().sendMessage(Text.colorize("&cJe hebt geen genoeg geld om deze aankoop te voltooien."));
                            return;
                        }

                        if (getPlayer().getInventory().firstEmpty() == -1) {
                            getPlayer().sendMessage(Text.colorize("&cJe hebt geen ruimte voor dit item!"));
                            return;
                        }

                        double balanceBefore = plugin.getEconomy().getBalance(getPlayer());
                        double balanceAfter = balanceBefore - shopItem.price;

                        Log log = new Log();
                        log.itemUuid = shopItem.uuid;
                        log.dateTime = System.currentTimeMillis();
                        log.uuid = getPlayer().getUniqueId().toString();
                        log.balanceBefore = balanceBefore;
                        log.balanceAfter = balanceAfter;
                        if (Objects.isNull(shopItem.logs)) {
                            shopItem.logs = new ArrayList<>();
                        }
                        shopItem.stored--;
                        shopItem.logs.add(log);
                        save(shopItem);

                        plugin.getEconomy().withdrawPlayer(getPlayer(), shopItem.price);
                        getPlayer().getInventory().addItem(itemStack);
                        getPlayer().sendMessage(Text.colorize("&3Je hebt succesvol dit item gekocht voor &b" + BalanceUtil.convert(shopItem.price)
                        + "&3."));
                    });
        }).filter(Objects::nonNull).limit(10).collect(Collectors.toList());
        while(items.size() != 10) {
            items.add(na);
        }
        MenuPopulator populator = itemsScheme.newPopulator(this);
        items.forEach(populator::acceptIfSpace);

        Date nextBlackMarket = DateUtil.getDateOffset(new Date(), 1, Calendar.DAY_OF_MONTH);
        MenuPopulator options = optionsScheme.newPopulator(this);
        Item balanceView = ItemStackBuilder.of(Material.BOOK)
                .name("&3&lBanksaldo")
                .lore("", "&b" + BalanceUtil.convert(plugin.getEconomy().getBalance(getPlayer()))
                ).buildItem().build();
        Item marketInfo = ItemStackBuilder.of(Material.WATCH)
                .name("&3&lNieuwe Blackmarket")
                .lore("", "&7Over &b" + DateUtil.getDateFormatting(new Date(), nextBlackMarket)).buildItem().build();
        Arrays.stream(new Item[]{ balanceView, marketInfo }).forEach(options::acceptIfSpace);
    }

    private void save(ShopItem shopItem) {
        int index = getIndex(shopItem.uuid);
        if (index >= 0) {
            shop.list.set(index, shopItem);
        }
        plugin.getShopManager().update(shop);
        redraw();
    }

    private int getIndex(String uuid) {
        for (int i = 0; i < shop.list.size(); i++) {
            if (shop.list.get(i).uuid.equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

}
