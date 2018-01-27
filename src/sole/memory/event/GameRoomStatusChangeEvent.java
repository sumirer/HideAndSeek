package sole.memory.event;

import sole.memory.room.Room;

public class GameRoomStatusChangeEvent extends GameEvent{


    private Room room;
    private int chnageStatus;
    public GameRoomStatusChangeEvent(Room room,int changeStatus){
        this.room = room;
        this.chnageStatus = changeStatus;
    }

    public Room getRoom() {
        return room;
    }

    public int getChnageStatus() {
        return chnageStatus;
    }
}
