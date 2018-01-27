package sole.memory.event;

import cn.nukkit.event.Cancellable;
import sole.memory.room.Room;
import sole.memory.seek.GamePlayer;

public class GamePlayerJoinGameEvent extends GameEvent implements Cancellable{


    private GamePlayer player;
    private Room room;
    public GamePlayerJoinGameEvent(GamePlayer player, Room room){
        this.player = player;
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public GamePlayer getPlayer() {
        return player;
    }
}
