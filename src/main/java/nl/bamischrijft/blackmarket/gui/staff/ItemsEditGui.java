package nl.bamischrijft.blackmarket.gui.staff;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import nl.bamischrijft.blackmarket.gui.staff.item.ItemEditGui;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import nl.bamischrijft.blackmarket.manager.models.ShopItem;
import nl.bamischrijft.blackmarket.util.BukkitSerialization;
import nl.bamischrijft.blackmarket.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class ItemsEditGui extends Gui {

    private final Shop shop;

    public ItemsEditGui(Player player, Shop shop) {
        super(player, 4, "&c&lItems &8- &7" + TextUtil.truncate(shop.id.toString(), 16));
        this.shop = shop;
    }

    private final MenuScheme glassScheme = new MenuScheme().maskEmpty(2).mask("111111111");
    private final MenuScheme optionsScheme = new MenuScheme().maskEmpty(3).mask("000010100");
    private final MenuScheme itemScheme = new MenuScheme().masks("111111111", "111111111");

    @Override
    public void redraw() {
        Item glass = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).durability(15).name(" ").buildItem().build();
        MenuPopulator glassPopulator = glassScheme.newPopulator(this);
        while(glassPopulator.hasSpace()) glassPopulator.accept(glass);

        MenuPopulator itemPopulator = itemScheme.newPopulator(this);
        List<ShopItem> list = Objects.isNull(shop.list) ? Collections.emptyList() : shop.list;
        list.stream().map(shopItem -> {
            ItemStack stack;
            try {
                stack = BukkitSerialization.itemStackArrayFromBase64(shopItem.base64)[0];
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }

            return ItemStackBuilder.of(stack).build(() -> {
                ItemEditGui itemEditGui = new ItemEditGui(getPlayer(), shop, shopItem);
                itemEditGui.open();
            });
        }).filter(Objects::nonNull).limit(10).forEach(itemPopulator::acceptIfSpace);

        Item back = ItemStackBuilder.of(Material.LADDER).name("Terug naar vorige menu")
                .build(() -> new ShopManageGui(getPlayer(), shop).open());
        Item addItemInfo = ItemStackBuilder.of(Material.FEATHER).name("&b&lItem toevoegen?")
                .lore("", "&3➊ &7Pak een &bitem &7vast in je hand.", "&3➋ &7Typ &b/bm add <shopdatum> &7en het item wordt toegevoegd.")
                .buildItem().build();
        MenuPopulator optionPopulator = optionsScheme.newPopulator(this);
        Arrays.stream(new Item[]{back,addItemInfo}).forEach(optionPopulator::acceptIfSpace);
    }

}
