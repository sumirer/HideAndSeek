package sole.memory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import sole.memory.command.AddRoomCommand;
import sole.memory.listener.ListenerEvent;
import sole.memory.listener.ListenerGameEvent;
import sole.memory.room.Room;
import sole.memory.seek.GamePlayer;
import sole.memory.seek.entitys.animal.Pig;
import sole.memory.tasks.UpdateTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HideAndSeek extends PluginBase {


    public static int[] ENTITY_ID_LIST = {Pig.NETWORK_ID};
    //设置方块栏物品选择入口 打开玩家背包
    public static int[] BLOCK_ID_LIST = { Block.SNOW_BLOCK,Block.CHEST,Block.BED_BLOCK};
    private static HideAndSeek instance;
    private static HashMap<String,GamePlayer> GAME_PLAYER = new HashMap<>();
    public static HashMap<String,Room> ROOM_LIST = new HashMap<>();

    public static final String MESSAGE = TextFormat.GOLD+"[HideAndSeek] "+TextFormat.AQUA;
    public static final String MESSAGE_ERROR = TextFormat.GOLD+"[HideAndSeek] "+TextFormat.RED;
    public static HideAndSeek getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        Server.getInstance().getLogger().notice(MESSAGE+"HideAndSeek is start....");
        this.getDataFolder().mkdirs();
        File file = new File(this.getDataFolder()+"/room");
        file.mkdirs();
        Server.getInstance().getLogger().notice(MESSAGE+"Start init plugin....");
        initListener();
        initCommand();
        Server.getInstance().getLogger().notice(MESSAGE+"Start init Room....");
        initRoomData();
        Server.getInstance().getLogger().notice(MESSAGE+"HideAndSeek Start Successful....");
    }

    private void initTask(Room room){
        Server.getInstance().getScheduler().scheduleRepeatingTask(new UpdateTask(room),10);
    }
    private void initListener(){
        Server.getInstance().getPluginManager().registerEvents(new ListenerEvent(this),this);
        Server.getInstance().getPluginManager().registerEvents(new ListenerGameEvent(this),this);
    }


    private void initCommand(){
        //每个房间用一个定时任务刷新(1s)
        Server.getInstance().getCommandMap().register("HideAndSeek",new AddRoomCommand("has"));
    }
    private void initRoomData(){
        Map<String,Object> room_data = (new Config(this.getDataFolder()+"/room.yml",Config.YAML)).getAll();
        room_data.forEach((name,room_name)->{
            File file = new File(this.getDataFolder()+"/room/"+room_name+".yml");
            if (file.isFile()) {
                try {
                    ConfigSection config = (new Config(this.getDataFolder() + "/room/" + room_name + ".yml", Config.YAML)).getSections();
                    Room room = new Room();
                    room.GAME_TIME = config.getInt("GameTime");
                    room.name = config.getString("Name");
                    room.GAME_WORLD_NAME = config.getString("Level");
                    room.plugin = this;
                    room.Wait_Pos = new Vector3(config.getDouble("Wait_Pos_X"), config.getDouble("Wait_Pos_Y"), config.getDouble("Wait_Pos_Z"));
                    room.Game_Pos = new Vector3(config.getDouble("Game_Pos_X"), config.getDouble("Game_Pos_Y"), config.getDouble("Game_Pos_Z"));
                    room.quite_pos = new Position(config.getDouble("Quit_Pos_X"), config.getDouble("Quit_Pos_Y"),
                            config.getDouble("Quit_Pos_Z"),
                            Server.getInstance().getLevelByName(config.getString("Quit_Level")));
                    ROOM_LIST.put(room.name,room);
                    initTask(room);
                }catch (Exception e){
                    Server.getInstance().getLogger().warning("[HideAndSeek] 房间: "+room_name+" 初始化失败");
                }
            }
        });
    }

    public boolean hasGamePlayer(Player player){
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
