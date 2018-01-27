package sole.memory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import sole.memory.listener.ListenerEvent;
import sole.memory.listener.ListenerGameEvent;
import sole.memory.seek.GamePlayer;
import sole.memory.tasks.UpdateTask;

import java.util.HashMap;

public class HideAndSeek extends PluginBase {


    private static HashMap<String,GamePlayer> GAME_PLAYER = new HashMap<>();
    @Override
    public void onEnable() {
        super.onEnable();
        this.getDataFolder().mkdirs();
        initTask();

    }

    private void initTask(){
        Server.getInstance().getScheduler().scheduleRepeatingTask(new UpdateTask(),10);
    }
    private void initListener(){
        Server.getInstance().getPluginManager().registerEvents(new ListenerEvent(this),this);
        Server.getInstance().getPluginManager().registerEvents(new ListenerGameEvent(this),this);
    }

    public boolean isGamePlayer(Player player){
        return GAME_PLAYER.containsKey(player.getName());
    }

    public GamePlayer getGamePlayer(Player player){
        return GAME_PLAYER.get(player.getName());
    }

    public void addGamePlayer(GamePlayer player){
        GAME_PLAYER.put(player.getName(),player);
    }

    public void removeGamePlayer(Player player){
        if (GAME_PLAYER.containsKey(player.getName())) {
            GAME_PLAYER.get(player.getName()).room.removeGamePlayer(player);
            GAME_PLAYER.remove(player.getName());
        }
    }
    public void removeGamePlayer(GamePlayer player){
        if (GAME_PLAYER.containsValue(player)) {
            GAME_PLAYER.get(player.getName()).room.removeGamePlayer(player);
            GAME_PLAYER.remove(player.getName());
        }
    }

    public void updateGamePlayer(GamePlayer player){
        GAME_PLAYER.replace(player.getName(),player);
    }
}
