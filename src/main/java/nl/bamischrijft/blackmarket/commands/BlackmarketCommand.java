package nl.bamischrijft.blackmarket.commands;

import lombok.AllArgsConstructor;
import me.lucko.helper.Commands;
import me.lucko.helper.command.argument.Argument;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.text3.Text;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.gui.staff.StaffHomeGui;
import nl.bamischrijft.blackmarket.manager.models.Shop;
import nl.bamischrijft.blackmarket.manager.models.ShopItem;
import nl.bamischrijft.blackmarket.util.BukkitSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
public class BlackmarketCommand implements TerminableModule {

    private final BlackmarketPlugin plugin;

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Commands.create()
                .assertPlayer("&cJe bent geen speler!")
                .assertPermission("blackmarket.admin")
                .handler(ctx -> {
                    Player player = ctx.sender();
                    Argument argument = ctx.arg(0);
                    if (argument.isPresent() && argument.parse(String.class).isPresent()) {
                        String subCommand = argument.parseOrFail(String.class);
                        Argument date = ctx.arg(1);
                        if (subCommand.equalsIgnoreCase("add") && date.parse(String.class).isPresent()) {
                            String dateString = date.parseOrFail(String.class);
                            PlayerInventory inventory = player.getInventory();

                            if (Objects.isNull(inventory.getItemInMainHand())) {
                                player.sendMessage(Text.colorize("&cJe hebt geen item in je hand."));
                                return;
                            }

                            Shop shop = plugin.getShopManager().findByDate(dateString);
                            if (Objects.isNull(shop)) {
                                player.sendMessage(Text.colorize("&cEr bestaat geen shop voor deze datum."));
                                return;
                            }

                            String base64 = BukkitSerialization.itemStackArrayToBase64(new ItemStack[]{ inventory.getItemInMainHand() });
                            ShopItem shopItem = new ShopItem();
                            shopItem.uuid = UUID.randomUUID().toString();
                            shopItem.base64 = base64;

                            if (Objects.isNull(shop.list)) {
                                shop.list = new ArrayList<>();
                            }
                            shop.list.add(shopItem);

                            plugin.getShopManager().update(shop);
                            player.sendMessage(Text.colorize("&3Je hebt succesvol dit toegevoegd aan de Blackmarket dag &b" + dateString + "&3."));
                            return;
                        }
                    }

                    StaffHomeGui gui = new StaffHomeGui(player);
                    gui.open();
                }).registerAndBind(consumer, "blackmarket", "bm", "blackm", "zwartemarkt");
    }

}
