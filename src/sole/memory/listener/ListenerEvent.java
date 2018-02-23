package sole.memory.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;
import sole.memory.HideAndSeek;
import sole.memory.event.GamePlayerDeathEvent;
import sole.memory.event.GamePlayerQuitGameEvent;
import sole.memory.room.Room;
import sole.memory.seek.GamePlayer;

public class ListenerEvent implements Listener {

    private HideAndSeek plugin;

    public ListenerEvent(HideAndSeek hideAndSeek){
        this.plugin = hideAndSeek;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Player player = event.getEntity();
        if (plugin.hasGamePlayer(player)) return;
        GamePlayerDeathEvent gamePlayerDeathEvent = new GamePlayerDeathEvent(plugin.getGamePlayer(player));
        Server.getInstance().getPluginManager().callEvent(gamePlayerDeathEvent);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (!plugin.hasGamePlayer(player)) return;
        Room room = plugin.getGamePlayer(player).room;
        if (room.game_status==Room.ROOM_STATUS_START || room.game_status==Room.ROOM_STATUS_WAIT){
            GamePlayerQuitGameEvent playerQuitGameEvent = new GamePlayerQuitGameEvent(room,plugin.getGamePlayer(player));
            Server.getInstance().getPluginManager().callEvent(playerQuitGameEvent);
        }
    }

    @EventHandler
    public void onPlayerRunCommandEvent(PlayerCommandPreprocessEvent event){
        Player player= event.getPlayer();
        if (plugin.hasGamePlayer(player)){
            event.setCancelled();
            plugin.getGamePlayer(player).sendMessage(TextFormat.RED+"你不能在游戏内执行命令", GamePlayer.MESSAGE_POPUP);
        }
    }

    @EventHandler
    public void onPlayerLevelChangeEvent(EntityLevelChangeEvent event){
        Entity player = event.getEntity();
        if (player instanceof Player){
            if (plugin.hasGamePlayer((Player) player)){
                event.setCancelled();
                ;
            }
        }
    }

    @EventHandler
    public void onPlayerChooseItemEvent(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        if (!plugin.hasGamePlayer(player)) return;
       // if (event.getItem().getId())
    }
}
