package sole.memory.seek;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import sole.memory.seek.entitys.animal.Pig;

public class Camouflage {

    public static final int ENTITY_PIG = Pig.NETWORK_ID;

    private GamePlayer player;
    public Camouflage(GamePlayer player){
        this.player = player;
    }

    public void setCamouflage() {
        switch (player.getCamouflageType()) {
            case GamePlayer.PLAYER_CAMOUFLAGE_TYPE_ENTITY:
                player.player.setNameTagVisible(false);
                removePlayer();
                addEntityPlayer(player.getCamouflage());
                break;
            case GamePlayer.PLAYER_CAMOUFLAGE_TYPE_BLOCK:
                player.player.setNameTagVisible(false);
                break;

        }
    }

    private void removePlayer(){
        RemoveEntityPacket removeEntityPacket = new RemoveEntityPacket();
        removeEntityPacket.eid = player.getPlayer().getId();
        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            if (player.getName().equals(p.getName())) continue;
            p.dataPacket(removeEntityPacket);
        }
        player.getPlayer().setScale(0.01F);
    }

    private void addEntityPlayer(int id){
        AddEntityPacket addEntityPacket = new AddEntityPacket();
        addEntityPacket.type = id;
        addEntityPacket.entityRuntimeId = player.player.getId();
        addEntityPacket.x = (float) player.player.x;
        addEntityPacket.y = (float) player.player.y;
        addEntityPacket.z = (float) player.player.z;
        addEntityPacket.speedX = 0;
        addEntityPacket.speedY = 0;
        addEntityPacket.speedZ = 0;
        addEntityPacket.metadata = new EntityMetadata();
        for (Player p:Server.getInstance().getOnlinePlayers().values()) {
          //  if (player.getName().equals(p.getName())) continue;
            p.dataPacket(addEntityPacket);
        }
    }

    private void addPlayer(){
        player.player.setScale(1F);
        AddPlayerPacket addPlayerPacket = new AddPlayerPacket();
        addPlayerPacket.entityRuntimeId = player.player.getId();
        addPlayerPacket.item = player.player.getInventory().getItemInHand();
        addPlayerPacket.uuid = player.player.getUniqueId();
        addPlayerPacket.x = (float)player.player.x;
        addPlayerPacket.y = (float)player.player.y;
        addPlayerPacket.z = (float)player.player.z;
        addPlayerPacket.speedX = 0;
        addPlayerPacket.speedY = 0;
        addPlayerPacket.speedZ = 0;
        for (Player p:Server.getInstance().getOnlinePlayers().values()) {
            if (player.getName().equals(p.getName())) continue;
            p.dataPacket(addPlayerPacket);
        }
    }

    public void removeCamouflage(){
        removePlayer();
        addPlayer();
    }
}
