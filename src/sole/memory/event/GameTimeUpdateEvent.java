package sole.memory.event;

import sole.memory.room.Room;

public class GameTimeUpdateEvent extends GameEvent{

    private Room room;
    private int time;
    public GameTimeUpdateEvent(Room room,Integer time){
        this.room = room;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public Room getRoom() {
        return room;
    }
}
