package sole.memory.tasks;


import cn.nukkit.scheduler.Task;
import sole.memory.room.Room;

public class UpdateTask extends Task {
    @Override
    public void onRun(int i) {
        for (Room room:Room.room_list.values()) {
            room.updateRoom();
        }
    }

}
