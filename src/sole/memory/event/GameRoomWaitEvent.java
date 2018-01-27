package sole.memory.event;

import sole.memory.room.Room;

public class GameRoomWaitEvent extends GameEvent{

    private Room room;
    public GameRoomWaitEvent(Room room){
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }
}
