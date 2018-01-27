package sole.memory.event;

import sole.memory.room.Room;

public class GameOverEvent extends GameEvent {
    public static final int ERROR_LEVEL_NOT_FOUND = 0;
    public static final int GAME_OVER = 1;




    private int error_type;
    private Room room;
    public GameOverEvent(Room room, Integer errpr_type){

    }
}
