package sole.memory.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.TextFormat;
import sole.memory.HideAndSeek;
import sole.memory.event.*;
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
        event.getRoom().createBossBarToPlayer(event.getPlayer());
    }

    @EventHandler
    public void onGameTimeUpdateEvent(GameTimeUpdateEvent event){
        if (event.getTime()==(event.getRoom().GAME_NOW_TIME-2)) {
            event.getRoom().sendMessageToRoomPlayer("游戏开始", GamePlayer.MESSAGE_TITLE);
        }
    }

    @EventHandler
    public void onGamePlayerDataUpdateEvent(GamePlayerDataUpdateEvent event){
        //保持房间内外玩家数据一致
        plugin.updateGamePlayer(event.getPlayer());
    }

    @EventHandler
    public void onGamePlayerQutGame(GamePlayerQuitGameEvent event){
        event.getRoom().sendMessageToRoomPlayer(TextFormat.DARK_AQUA+"玩家"+event.getPlayer().getName()+"退出了游戏",GamePlayer.MESSAGE_TITLE);
        event.getRoom().removeGamePlayer(event.getPlayer());
        plugin.removeGamePlayer(event.getPlayer());
    }
}
