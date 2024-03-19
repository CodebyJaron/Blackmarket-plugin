package nl.bamischrijft.blackmarket.gui.staff.item;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.text3.Text;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.gui.staff.ItemsEditGui;
import nl.bamischrijft.blackmarket.gui.staff.LogsViewGui;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import nl.bamischrijft.blackmarket.manager.models.ShopItem;
import nl.bamischrijft.blackmarket.util.BalanceUtil;
import nl.bamischrijft.blackmarket.util.BukkitSerialization;
import nl.bamischrijft.blackmarket.util.ChatUtil;
import nl.bamischrijft.blackmarket.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class ItemEditGui extends Gui {

    private final BlackmarketPlugin plugin;

    private final Shop shop;
    private final ShopItem shopItem;

    private final MenuScheme glassScheme = new MenuScheme().masks("111111111", "101100001", "111111111", "111101111");
    private final MenuScheme buttonScheme = new MenuScheme().maskEmpty(1).mask("010011110").maskEmpty(1).mask("000010000");

    public ItemEditGui(Player player, Shop shop, ShopItem shopItem) {
        super(player, 4, "&c&lItems &8- &7" + TextUtil.truncate(shop.id.toString(), 16));
        this.plugin = BlackmarketPlugin.getInstance();
        this.shop = shop;
        this.shopItem = shopItem;
    }

    @Override
    public void redraw() {
        if (!isFirstDraw()) {
            clearItems();
        }

        Item glass = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(15).name(" ").buildItem().build();
        MenuPopulator glassPopulator = glassScheme.newPopulator(this);
        while(glassPopulator.hasSpace()) glassPopulator.accept(glass);

        ItemStack itemStack;
        try {
            itemStack = BukkitSerialization.itemStackArrayFromBase64(shopItem.base64)[0];
        } catch (Exception exception) {
            exception.printStackTrace();
            getPlayer().sendMessage(Text.colorize("&cEr is iets fout gegaan."));
            close();
            return;
        }
        Item itemDisplay = ItemStackBuilder.of(itemStack).lore("", "&7Prijs: &f" +
                BalanceUtil.convert(shopItem.price), "&7Voorraad: &f" + shopItem.stored + "&8/&f" + shopItem.max).buildItem().build();
        Item voorraad = ItemStackBuilder.of(Material.STONE_BUTTON).name("&b&lVoorraad").lore("", "&7Huidige waarde: &f" +
                shopItem.stored + "&8/&f" + shopItem.max, "", "&bLinkermuisknop &7is &3+1", "&bRechtermuisknop &7is &3-1")
                .build(() -> {
                    shopItem.stored--;
                    shopItem.max--;
                    save();
                },  () -> {
                    shopItem.stored++;
                    shopItem.max++;
                    save();
                });
        Item logs = ItemStackBuilder.of(Material.KNOWLEDGE_BOOK).name("&b&lLogs").lore("", "&7Totale documenten: &f" +
                (Objects.isNull(shopItem.logs) ? "0" : shopItem.logs.size()))
                .build(() -> new LogsViewGui(getPlayer(), shop, shopItem).open());
        Item price = ItemStackBuilder.of(Material.GHAST_TEAR).name("&b&lPrijs").lore("", "&7Huidige waarde: &f" + BalanceUtil
        .convert(shopItem.price), "", "&3&nKlik om te bewerken").build(() -> {
            ChatUtil.chatInput(getPlayer(), "&3Wat wil je instellen als nieuwe &bprijs &3voor dit item?",
                    event ->
                    {
                        String message = event.getMessage();

                        double number;
                        try {
                            number = Double.parseDouble(message);
                        } catch (NumberFormatException exception) {
                            getPlayer().sendMessage(Text.colorize("&cDit is geen geldig getal!"));
                            new ItemEditGui(getPlayer(), shop, shopItem).open();
                            return;
                        }

                        getPlayer().sendMessage(Text.colorize("&3Succesvol de prijs &bgeupdate &3van dit item naar &b"
                        + BalanceUtil.convert(number) + "&3."));
                        shopItem.price = number;
                        save();
                        new ItemEditGui(getPlayer(), shop, shopItem).open();
                    });
        });
        Item playerLimit = ItemStackBuilder.of(Material.WOOD_BUTTON).name("&b&lSpeler Limiet").lore("", "&7Huidige waarde: &f" +
                shopItem.playerLimit, "", "&bLinkermuisknop &7is &3+1", "&bRechtermuisknop &7is &3-1")
                .build(() -> {
                    shopItem.playerLimit--;
                    save();
                },  () -> {
                    shopItem.playerLimit++;
                    save();
                });
        Item back = ItemStackBuilder.of(Material.LADDER).name("Terug naar vorige menu")
                .build(() -> new ItemsEditGui(getPlayer(), shop).open());

        MenuPopulator buttonPopulator = buttonScheme.newPopulator(this);
        Arrays.stream(new Item[]{ itemDisplay, voorraad, price, logs, playerLimit, back }).forEach(buttonPopulator::acceptIfSpace);
    }

    private void save() {
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
