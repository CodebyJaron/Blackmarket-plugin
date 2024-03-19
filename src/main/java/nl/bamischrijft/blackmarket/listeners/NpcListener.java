package nl.bamischrijft.blackmarket.listeners;

import lombok.AllArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import nl.bamischrijft.blackmarket.BlackmarketPlugin;
import nl.bamischrijft.blackmarket.gui.HomeGui;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;

@AllArgsConstructor
public class NpcListener implements TerminableModule {

    private final BlackmarketPlugin plugin;

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Events.subscribe(PlayerInteractAtEntityEvent.class)
                .filter(e -> e.getHand().equals(EquipmentSlot.HAND))
                .filter(e -> e.getRightClicked() instanceof Player)
                .filter(e -> e.getRightClicked().getCustomName().equals(plugin.getConfig().getString("citizens.npc-name")))
                .handler(e -> {
                    e.setCancelled(true);

                    HomeGui gui = new HomeGui(e.getPlayer(), plugin);
                    gui.open();
                })
        .bindWith(consumer);
    }

}
