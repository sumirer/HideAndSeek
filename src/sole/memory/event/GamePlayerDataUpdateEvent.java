package sole.memory.event;

import sole.memory.room.Room;
import sole.memory.seek.GamePlayer;

public class GamePlayerDataUpdateEvent extends GameEvent {

    private Room room;
    private GamePlayer player;
    public GamePlayerDataUpdateEvent(Room room, GamePlayer player){
        this.room = room;
        this.player = player;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public Room getRoom() {
        return room;
    }
}
