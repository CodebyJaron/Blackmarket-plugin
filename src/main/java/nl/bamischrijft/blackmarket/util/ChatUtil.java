package nl.bamischrijft.blackmarket.util;

import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.text3.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.Consumer;

public class ChatUtil {

    public static void chatInput(Player player, String question, Consumer<AsyncPlayerChatEvent> onComplete) {
        CompositeTerminable compositeTerminable = CompositeTerminable.create();

        // make sure any open inventory is closed prior to chat process
        player.closeInventory();
        player.sendMessage(Text.colorize(question));

        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> e.getPlayer().equals(player))
                .expireAfter(1)
                .handler(e -> {
                    // Cancel the event
                    e.setCancelled(true);

                    // Accept the consumer
                    onComplete.accept(e);
                }).bindWith(compositeTerminable);
    }

}
