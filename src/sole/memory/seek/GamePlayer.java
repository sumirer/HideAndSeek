package sole.memory.seek;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;
import sole.memory.room.Room;

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

    public void setCamouflage() {
        this.camouflage = camouflage;
    }

    public void setPlayerToCamoufalge(){
        if (camouflage==-1) return;
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

    public void sendMessage(String message,int type){

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
                }
    }
}
