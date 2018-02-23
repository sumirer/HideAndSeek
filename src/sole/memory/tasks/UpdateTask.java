package sole.memory.tasks;


import cn.nukkit.scheduler.Task;
import sole.memory.room.Room;

public class UpdateTask extends Task {

    private Room room;
    public UpdateTask(Room room){

    }
    @Override
    public void onRun(int i) {
        room.updateRoom();
    }

}
