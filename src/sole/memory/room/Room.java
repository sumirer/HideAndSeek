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
import sole.memory.event.*;
import sole.memory.seek.GamePlayer;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {

    public static final int ROOM_STATUS_WAIT = 0;
    public static final int ROOM_STATUS_START = 1;
    public static final int ROOM_STATUS_CHOOSE = 2;
    public static final int ROOM_STATUS_HINT = 4;
    public static final int ROOM_STATUS_READ_SATRT = 3;

    public static final int ROOM_STATUS_CAN_JOIN = 2;
    public static final int ROOM_STATUS_NOT_JOIN = 3;



    public String name = "ROOM";

    public HideAndSeek plugin = null;

    public static HashMap<String,Room> room_list = new HashMap<>();

    public int GAME_TIME = 600;
    public int GAME_NOW_TIME = 600;

    public int CHOOSE_ENTITY_TIME = 30;

    // 等待点和游戏点在同一Level
    // seek 直接传送到 Game_pos
    //
    public Vector3 Wait_Pos = null;

    public Vector3 Game_Pos = null;

    //初始猎人等待点
    //玩家变成猎人后传送到 Game_pos
    public Vector3 hide_wait_pos = null;

    public int HINT_TIME = 30;

    public Position quite_pos = null;

    public String GAME_WORLD_NAME = null;

    public Level GAME_WORLD = null;

    public int _CHOOSE_ENTITY_TIME = 30;

    public int WAIT_TIME = 30;

    public int GAME_MIN_PLAYER = 5;

    public HashMap<String,GamePlayer> player_list = new HashMap<>();

    private HashMap<String,Long> boss_bar_id = new HashMap<>();

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

    private void updateRoomTime(){
        this.GAME_NOW_TIME--;
        GameTimeUpdateEvent event = new GameTimeUpdateEvent(this,GAME_TIME);
        callEvent(event);
        if (GAME_NOW_TIME<1){
            shutDownRoom();
        }
    }

    private int getRoomPlayerCount(){
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


    private void updatePlayerBossBar(GamePlayer player, boolean remove) {
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

    private void updatePlayerBossBar(){
        for (GamePlayer player:player_list.values()) {
            updatePlayerBossBar(player,false);
        }
    }

    private void handlerWaitEvent(){

            GameRoomWaitEvent roomWaitEvent = new GameRoomWaitEvent(this);
            callEvent(roomWaitEvent);
    }
    public void sendMessageToRoomPlayer(String message,int type){
        for (GamePlayer player:player_list.values()) {
            player.sendMessage(message,type);
        }
    }

    private void shutDownRoom(){
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
            if (GAME_WORLD == null){
                game_status = ROOM_STATUS_NOT_JOIN;
                GameRoomStatusChangeEvent gameRoomStatusChangeEvent = new GameRoomStatusChangeEvent(this,ROOM_STATUS_NOT_JOIN);
                callEvent(gameRoomStatusChangeEvent);
                GameOverEvent gameErrorEvent = new GameOverEvent(this, GameOverEvent.ERROR_LEVEL_NOT_FOUND);
                callEvent(gameErrorEvent);
                return;
            }
        }
        updatePlayerBossBar();
        if (game_status!=ROOM_STATUS_WAIT && getRoomPlayerCount()<GAME_MIN_PLAYER && WAIT_TIME != 0){
            if (game_status!=ROOM_STATUS_WAIT) {
                game_status = ROOM_STATUS_WAIT;
                GameRoomStatusChangeEvent gameRoomStatusChangeEvent = new GameRoomStatusChangeEvent(this, ROOM_STATUS_WAIT);
                callEvent(gameRoomStatusChangeEvent);
            }
            handlerWaitEvent();
        }
        if (game_status==ROOM_STATUS_WAIT && WAIT_TIME>0){
            sendMessageToRoomPlayer("游戏还有: "+WAIT_TIME+"秒开始",GamePlayer.MESSAGE_TIP);
            WAIT_TIME--;
            return;
        }
        //game start
        if (CHOOSE_ENTITY_TIME >= 0) {
            if (CHOOSE_ENTITY_TIME == _CHOOSE_ENTITY_TIME) {
                game_status = ROOM_STATUS_CHOOSE;
                GameRoomStatusChangeEvent event = new GameRoomStatusChangeEvent(this, ROOM_STATUS_CHOOSE);
                callEvent(event);
            }
            //TODO　手持栏？箱子？GUI?个人物品栏？选择伪装实体，未选择要随机选择，方块和实体数量差距大要随机设置部分玩家的选项
            sendMessageToRoomPlayer("请选择你要伪装的生物或方块", GamePlayer.MESSAGE_TIP);
            if (CHOOSE_ENTITY_TIME == 0) {
                sendMessageToRoomPlayer("游戏开始", GamePlayer.MESSAGE_TITLE);
                // 在player_list里面随机挑选一名hide
                chooseHide(true);
                hide.forEach((name, player) -> {
                    if (hide_wait_pos == null) {
                        GameOverEvent event = new GameOverEvent(this, GameOverEvent.ERROR_HIDE_POS_NOT_FOUND);
                        callEvent(event);
                        shutDownRoom();
                        return;
                    }
                    player.teleport(hide_wait_pos);
                });
                seek_list.forEach((name, player) -> {
                    if (Game_Pos == null) {
                        GameOverEvent event = new GameOverEvent(this, GameOverEvent.ERROR_GAME_POS_NOT_FOUND);
                        callEvent(event);
                        shutDownRoom();
                        return;
                    }
                    player.teleport(Game_Pos);
                    player.setPlayerToCamouflage();
                });
            }
            CHOOSE_ENTITY_TIME--;
            return;
        }
        if (HINT_TIME>=0){
            hide.forEach((name,player)-> player.sendMessage("请等待其他玩家藏好，剩余时间"+HINT_TIME,GamePlayer.MESSAGE_TIP));
            seek_list.forEach((name,player)-> player.sendMessage("请尽快藏好,剩余时间"+HINT_TIME,GamePlayer.MESSAGE_TIP));
            // 猎人不能移动,给定一个封闭房间，时间到了传送到指定地点
            if (HINT_TIME == 0){
                seek_list.forEach((name,player)-> player.sendMessage("猎人开始了寻找，请躲好",GamePlayer.MESSAGE_TIP));
            }
            HINT_TIME--;
            return;
        }
        if (GAME_TIME==1){
            //游戏结束了
            if (seek_list.size()>hide.size()){
                player_list.forEach((name,player)-> player.sendMessage("猎人获得了胜利",GamePlayer.MESSAGE_TITLE));
            }else {
                player_list.forEach((name,player)-> player.sendMessage("躲藏者获得了胜利",GamePlayer.MESSAGE_TITLE));
            }
        }
        // 当时间结束 还存在seek seek 获胜，不存在 seek hide 获胜；seek数量为1；hide 数量为0，hide 获胜；防止人数不平衡或退出游戏
        if (hide.size()<1 && seek_list.size()<1){
            GameOverEvent event = new GameOverEvent(this,GameOverEvent.GAME_OVER);
            callEvent(event);
            shutDownRoom();
            return;
        }
        if (hide.size()<1){
            if (seek_list.size()>=2){
                chooseHide(false);
                // 如果猎人数量为0；随机挑选一名seek作为猎人
            }else {
                //如果 seek_list人数小于2，房间结束游戏
                GameOverEvent event = new GameOverEvent(this, GameOverEvent.GAME_OVER);
                callEvent(event);
                shutDownRoom();
                return;
            }
        }
        if (seek_list.size()<1){
            player_list.forEach((name,player)-> player.sendMessage("猎人获得了胜利",GamePlayer.MESSAGE_TITLE));
        }
        if (game_status!=ROOM_STATUS_START) {
            game_status = ROOM_STATUS_START;
            GameRoomStatusChangeEvent gameRoomStatusChangeEvent = new GameRoomStatusChangeEvent(this, ROOM_STATUS_WAIT);
            callEvent(gameRoomStatusChangeEvent);
            GameStartEvent event = new GameStartEvent(this);
            callEvent(event);
        }
        if (GAME_TIME<=9 && GAME_TIME>2){
            sendMessageToRoomPlayer("游戏将在"+GAME_TIME+"秒后结束",GamePlayer.MESSAGE_TITLE);
        }
        updateRoomTime();
    }

    private void chooseHide(boolean start){
        int index = new Random().nextInt(seek_list.size());
        final int[] i = {0};
        if (start){
            player_list.forEach((name,player)->{
                if (i[0]==index){
                    hide.put(name,player);
                    seek_list.putAll(player_list);
                    seek_list.remove(name);
                    player.setRole(GamePlayer.PLAYER_GAME_ROLE_FOUND);
                    player.sendMessage("你被选为了猎人，祝你好运",GamePlayer.MESSAGE_TITLE);
                }
                i[0]++;
            });
            //设置剩余玩家角色为 SEEK
            seek_list.forEach((name,player)-> player.setRole(GamePlayer.PLAYER_GAME_ROLE_SEEK));
            return;
        }
        seek_list.forEach((name,player)->{
            if (i[0]==index){
                hide.put(name,player);
                seek_list.remove(name);
                player.setRole(GamePlayer.PLAYER_GAME_ROLE_FOUND);
                player.unsetPlayerCamouflage();
                player.sendMessage("你被随机挑选为猎人",GamePlayer.MESSAGE_TITLE);
                //需要将此玩家传送到游戏点
                player.teleport(Game_Pos);
                return;
            }
            i[0]++;
        });
    }
}
