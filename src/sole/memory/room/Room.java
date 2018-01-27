package sole.memory.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import sole.memory.HideAndSeek;
import sole.memory.event.GameOverEvent;
import sole.memory.event.GameRoomStatusChangeEvent;
import sole.memory.event.GameStartEvent;
import sole.memory.event.GameTimeUpdateEvent;
import sole.memory.seek.GamePlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class Room {

    public static final int ROOM_STATUS_WAIT = 0;
    public static final int ROOM_STATUS_START = 1;

    public static final int ROOM_STATUS_CAN_JOIN = 2;
    public static final int ROOM_STATUS_NOT_JOIN = 3;



    public String name = "ROOM";

    public HideAndSeek plugin = null;

    public static HashMap<String,Room> room_list = new HashMap<>();

    public int GAME_TIME = 600;
    public int GAME_NOW_TIME = 600;

    public int CHOOSE_ENTITY_TIME = 30;

    public Vector3 Wait_Pos = null;

    public Vector3 Game_Pos = null;

    public Position quite_pos = null;

    public String GAME_WORLD_NAME = null;

    public Level GAME_WORLD = null;

    public int WAIT_TIME = 30;

    public int GAME_MIN_PLAYER = 5;

    public HashMap<String,GamePlayer> player_list = new HashMap<>();

    public HashMap<String,Long> boss_bar_id = new HashMap<>();

    public int game_status = ROOM_STATUS_CAN_JOIN;

    public HashMap<String,GamePlayer> hide = new HashMap<>();

    public HashMap<String,GamePlayer> seek_list = new HashMap<>();

    public void initRoom(){
        seek_list.clear();
        GAME_NOW_TIME = GAME_TIME;
        WAIT_TIME = 30;
        CHOOSE_ENTITY_TIME = 30;
        hide.clear();
        boss_bar_id.clear();
        player_list.clear();
    }

    public void updateRoomTime(){
        this.GAME_NOW_TIME--;
        GameTimeUpdateEvent event = new GameTimeUpdateEvent(this,GAME_TIME);
        callEvent(event);
        if (GAME_NOW_TIME<1){
            shutDownRoom();
        }
    }

    public int getRoomPlayerCount(){
        return player_list.size();
    }

    public void addPlayerToList(GamePlayer player){
        player_list.put(player.getName(),player);
    }
    public GamePlayer getGamePlayer(Player player){
        return player_list.get(player.getName());
    }

    public void removeBossBar(GamePlayer player){
        if (boss_bar_id.containsKey(player.getName())){
            player.getPlayer().removeBossBar(boss_bar_id.get(player.getName()));
        }
    }

    public boolean playerInRoom(Player player){
        return player_list.containsKey(player.getName());
    }

    public void addGamePlayer(GamePlayer player){
        player_list.put(player.getName(),player);
    }

    public void removeGamePlayer(Player player){
        if (player_list.containsKey(player.getName())) player_list.remove(player.getName());
    }
    public void removeGamePlayer(GamePlayer player){
        if (player_list.containsKey(player.getName())) player_list.remove(player.getName());
    }

    private void callEvent(Event event){
        Server.getInstance().getPluginManager().callEvent(event);
    }

    //when player join room
    public void createBossBarToPlayer(GamePlayer player){
        DummyBossBar.Builder builder = new DummyBossBar.Builder(player.player);
        builder.length(100);
        builder.text(TextFormat.AQUA+"房间: "+name+TextFormat.GOLD+"      人数: "+getRoomPlayerCount()+ TextFormat.RED+"   状态: 准备中");
        builder.color(255,174,185);
        boss_bar_id.put(player.getName(),player.getPlayer().createBossBar(builder.build()));
    }

    public void chooseCamouflage(){

    }

    public void updatePlayerBossBar(GamePlayer player,boolean remove) {
        DummyBossBar dummyBossBar = player.getPlayer().getDummyBossBar(boss_bar_id.get(player.getName()));
        if (remove) {
            dummyBossBar.destroy();
            return;
        }
        if (game_status == ROOM_STATUS_START) {
            dummyBossBar.setLength(GAME_NOW_TIME / GAME_TIME * 100);
            dummyBossBar.setText(TextFormat.AQUA + "房间: " + name + TextFormat.GOLD + "  伪装者: " + seek_list.size() + TextFormat.RED + "   猎人: ");
            return;
        }
        if (game_status == ROOM_STATUS_WAIT) {
            dummyBossBar.setText(TextFormat.AQUA + "房间: " + name + TextFormat.GOLD + "      人数: " + getRoomPlayerCount() + TextFormat.RED + "   状态: 准备中...");
        }
        dummyBossBar.reshow();
    }

    public void updatePlayerBossBar(){
        for (GamePlayer player:player_list.values()) {
            updatePlayerBossBar(player,false);
        }
    }

    public void sendMessageToRoomPlayer(String message,int type){
        for (GamePlayer player:player_list.values()) {
            player.sendMessage(message,type);
        }
    }

    public void shutDownRoom(){
        game_status = ROOM_STATUS_CAN_JOIN;
        GameRoomStatusChangeEvent gameRoomStatusChangeEvent = new GameRoomStatusChangeEvent(this,ROOM_STATUS_CAN_JOIN);
        callEvent(gameRoomStatusChangeEvent);
        GameOverEvent event = new GameOverEvent(this,GameOverEvent.GAME_OVER);
        callEvent(event);
        for(GamePlayer player:player_list.values()){
            player.getPlayer().teleport(quite_pos);
            plugin.removeGamePlayer(player);
        }
        initRoom();
    }

    public void updateRoom(){
        if (GAME_WORLD==null){
            GAME_WORLD = Server.getInstance().getLevelByName(name);
            if (GAME_WORLD != null){
                game_status = ROOM_STATUS_NOT_JOIN;
                GameRoomStatusChangeEvent gameRoomStatusChangeEvent = new GameRoomStatusChangeEvent(this,ROOM_STATUS_NOT_JOIN);
                callEvent(gameRoomStatusChangeEvent);
                GameOverEvent gameErrorEvent = new GameOverEvent(this, GameOverEvent.ERROR_LEVEL_NOT_FOUND);
                callEvent(gameErrorEvent);
                //shutDown Room
                return;
            }
        }
        updatePlayerBossBar();
        if (game_status!=ROOM_STATUS_START && getRoomPlayerCount()<GAME_MIN_PLAYER && WAIT_TIME != 0){
            if (game_status!=ROOM_STATUS_WAIT) {
                game_status = ROOM_STATUS_WAIT;
                GameRoomStatusChangeEvent gameRoomStatusChangeEvent = new GameRoomStatusChangeEvent(this, ROOM_STATUS_WAIT);
                this.callEvent(gameRoomStatusChangeEvent);
            }
            sendMessageToRoomPlayer("游戏还有: "+WAIT_TIME+"秒开始",GamePlayer.MESSAGE_TIP);
            WAIT_TIME--;
            return;
        }
        updateRoomTime();
        if (game_status!=ROOM_STATUS_START) {
            game_status = ROOM_STATUS_START;
            GameRoomStatusChangeEvent gameRoomStatusChangeEvent = new GameRoomStatusChangeEvent(this, ROOM_STATUS_WAIT);
            callEvent(gameRoomStatusChangeEvent);
            GameStartEvent event = new GameStartEvent(this);
            callEvent(event);
        }
    }

}
