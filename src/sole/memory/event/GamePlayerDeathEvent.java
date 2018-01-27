package sole.memory.event;

import sole.memory.seek.GamePlayer;

public class GamePlayerDeathEvent extends GameEvent {

    private GamePlayer player;
    public GamePlayerDeathEvent(GamePlayer player){
        this.player = player;
    }

    public GamePlayer getPlayer() {
        return player;
    }
}
