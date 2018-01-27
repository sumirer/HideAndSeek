package sole.memory.event;

import sole.memory.room.Room;
import sole.memory.seek.GamePlayer;

public class GamePlayerQuitGameEvent extends GameEvent{

    private Room room;
    private GamePlayer player;
    public GamePlayerQuitGameEvent(Room room, GamePlayer player){
        this.room = room;
        this.player = player;
    }

    public Room getRoom() {
        return room;
    }

    public GamePlayer getPlayer() {
        return player;
    }
}
