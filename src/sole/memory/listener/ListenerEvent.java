package sole.memory.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import sole.memory.HideAndSeek;
import sole.memory.event.GamePlayerDeathEvent;
import sole.memory.event.GamePlayerQuitGameEvent;
import sole.memory.room.Room;

public class ListenerEvent implements Listener {

    private HideAndSeek plugin;

    public ListenerEvent(HideAndSeek hideAndSeek){
        this.plugin = hideAndSeek;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Player player = event.getEntity();
        if (!plugin.isGamePlayer(player)) return;
        GamePlayerDeathEvent gamePlayerDeathEvent = new GamePlayerDeathEvent(plugin.getGamePlayer(player));
        Server.getInstance().getPluginManager().callEvent(gamePlayerDeathEvent);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (!plugin.isGamePlayer(player)) return;
        Room room = plugin.getGamePlayer(player).room;
        if (room.game_status==Room.ROOM_STATUS_START || room.game_status==Room.ROOM_STATUS_WAIT){
            GamePlayerQuitGameEvent playerQuitGameEvent = new GamePlayerQuitGameEvent(room,plugin.getGamePlayer(player));
            Server.getInstance().getPluginManager().callEvent(playerQuitGameEvent);
        }
    }

}
