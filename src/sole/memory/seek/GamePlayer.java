package sole.memory.seek;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import sole.memory.HideAndSeek;
import sole.memory.room.Room;

import java.util.ArrayList;
import java.util.Random;


public class GamePlayer {
    public static final int PLAYER_STATUS_WAIT = 0;
    public static final int PLAYER_STATUS_GAME = 1;
    public static final int PLAYER_STATUS_VISIT = 2;
    public static final int PLAYER_GAME_ROLE_FOUND = 0;
    public static final int PLAYER_GAME_ROLE_SEEK = 1;
    public static final int PLAYER_CAMOUFLAGE_TYPE_ENTITY = 0;
    public static final int PLAYER_CAMOUFLAGE_TYPE_BLOCK = 1;
    public static final int MESSAGE_POPUP = 0;
    public static final int MESSAGE_TIP = 1;
    public static final int MESSAGE_MESSAGE = 2;
    public static final int MESSAGE_GUI = 3;
    public static final int MESSAGE_TITLE = 4;



    public Player player;
    public int role = -1;
    public int camouflage = -1;
    public int camouflageType = -1;
    public int status = PLAYER_STATUS_WAIT;
    public Room room = null;


    public GamePlayer(Player player){
        this.player = player;
    }

    public void setCamouflage(int camouflage) {
        this.camouflage = camouflage;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getName(){
        return player.getName();
    }

    public void teleport(Position position){
        player.teleport(position);
    }

    public void teleport(Vector3 vector3){
        player.teleport(vector3);
    }

    public int getCamouflageType() {
        return camouflageType;
    }

    public void setCamouflageType(int camouflageType) {
        this.camouflageType = camouflageType;
    }

    public int getStatus() {
        return status;
    }

    public Player getPlayer() {
        return player;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRole() {
        return role;
    }

    public boolean isCamouflage(){
        return camouflage>=0;
    }

    public int getCamouflage() {
        return camouflage;
    }


    public void setPlayerToCamouflage(){
        if (camouflage==-1){
            //设置随机ID       
        }
        Camouflage camouflage = new Camouflage(this);
        camouflage.setCamouflage();
    }

    public void unsetPlayerCamouflage(){
        Camouflage camouflage = new Camouflage(this);
        camouflage.removeCamouflage();
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void sendMessage(String message,int type) {

        switch (type) {
            case MESSAGE_POPUP:
                player.sendPopup(message);
                break;
            case MESSAGE_TIP:
                player.sendTip(message);
                break;
            case MESSAGE_MESSAGE:
                player.sendMessage(message);
            case MESSAGE_TITLE:
                player.sendTitle(message);
                break;
            case MESSAGE_GUI:
                player.showFormWindow(new FormWindowModal("HideAndSeek", message, "确定", "确定"));
                break;
            default:
                try {
                    throw new Exception("Message Type Not Found");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setPlayerInvetory(int id){
        switch (id){
            case Item.REDSTONE_BLOCK:
                sendBlockList();
                break;
            case Item.NETHER_STAR:
                sendEntityList();
                break;
                default:
                    getRandom();
                    //选择花或者未选择，随机选择
                    break;
        }
    }

    private void sendEntityList(){
        for (Integer id:HideAndSeek.ENTITY_ID_LIST) {

        }
    }
    private void sendBlockList(){
        player.getInventory().clearAll();
        for (Integer id:HideAndSeek.BLOCK_ID_LIST) {
            player.getInventory().addItem(Item.get(id));
        }
    }

    private void getRandom(){
        if (new Random().nextInt(2)>0){
            setCamouflageType(PLAYER_CAMOUFLAGE_TYPE_BLOCK);
            int i = new Random().nextInt(HideAndSeek.BLOCK_ID_LIST.length);
            setCamouflage(HideAndSeek.BLOCK_ID_LIST[i]);
            sendMessage("随机挑选你的伪装ID为: BLOCK_ID="+getCamouflage(),GamePlayer.MESSAGE_TIP);
            return;
        }
        setCamouflageType(PLAYER_CAMOUFLAGE_TYPE_ENTITY);
        int i = new Random().nextInt(HideAndSeek.ENTITY_ID_LIST.length);
        setCamouflage(HideAndSeek.ENTITY_ID_LIST[i]);
        sendMessage("随机挑选你的伪装ID为: ENTITY_ID="+getCamouflage(),GamePlayer.MESSAGE_TIP);
    }
}
