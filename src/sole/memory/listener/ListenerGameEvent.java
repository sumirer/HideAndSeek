package sole.memory.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import sole.memory.HideAndSeek;
import sole.memory.event.GamePlayerDeathEvent;
import sole.memory.event.GamePlayerJoinGameEvent;
import sole.memory.event.GameTimeUpdateEvent;
import sole.memory.seek.GamePlayer;

public class ListenerGameEvent implements Listener {

    private HideAndSeek plugin;
    public ListenerGameEvent(HideAndSeek hideAndSeek){
        this.plugin = hideAndSeek;
    }

    @EventHandler
    public void onGamePlayerDeathEvent(GamePlayerDeathEvent event){

    }

    @EventHandler
    public void onGamePlayerJoinGameEvent(GamePlayerJoinGameEvent event){
        plugin.addGamePlayer(event.getPlayer());
        event.getRoom().addPlayerToList(event.getPlayer());
    }

    @EventHandler
    public void onGameTimeUpdateEvent(GameTimeUpdateEvent event){
        if (event.getTime()==(event.getRoom().GAME_NOW_TIME-2)) {
            event.getRoom().sendMessageToRoomPlayer("游戏开始", GamePlayer.MESSAGE_TITLE);
        }
    }
}
