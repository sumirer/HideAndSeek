package sole.memory.event;

import sole.memory.room.Room;

public class GameStartEvent extends GameEvent{

    private Room room;
    public GameStartEvent(Room room){
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }
}
