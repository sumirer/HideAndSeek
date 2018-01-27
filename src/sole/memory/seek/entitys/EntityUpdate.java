package sole.memory.seek.entitys;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

import java.util.Random;

public class EntityUpdate extends EntityCreature
{
    public EntityUpdate(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private int updateTime = 50;

    private Vector3 pos = null;

    public float getSpeed(){
        return 1;
    }

    private void setUpdateTime(){
        if (pos==null) getNextPos();
        if (updateTime<1){
            getNextPos();
            updateTime = 50;
            return;
        }
        updateTime--;
    }

    public boolean isMove = false;

    @Override
    public int getNetworkId() {
        return 0;
    }

    private void setMove(){
        Random random = new Random();
        if (random.nextInt(2)>0){
            this.isMove = true;
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            AddEntityPacket pk = new AddEntityPacket();
            pk.entityRuntimeId = this.getId();
            pk.entityUniqueId = this.getId();
            pk.type = this.getNetworkId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = pk.speedY = pk.speedZ = 0;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.hasSpawned.put(player.getLoaderId(), player);
        }
    }


    private void getNextPos(){
        if (!isMove) pos = getPosition();
        pos =  this.add(NukkitMath.randomRange(new NukkitRandom(),0,10),0,NukkitMath.randomRange(new NukkitRandom(),0,10));
    }



    private String getBlock(Block block) {
        switch (block.getId()) {
            case 0:
            case 6:
            case 27:
            case 30:
            case 31:
            case 37:
            case 38:
            case 39:
            case 40:
            case 50:
            case 51:
            case 63:
            case 64:
            case 66:
            case 68:
            case 78:
            case 111:
            case 141:
            case 142:
            case 171:
            case 175:
            case 244:
            case 323:
                return "pass";
            case 10:
            case 11:
                return "lava";
            case 44:
            case 158:
                //半砖
                if (block.getDamage() >= 8) {
                    return "block";
                } else {
                    return "half";
                }
            case 85:
            case 107:
            case 139:
                return "no";
            case 65:
            case 106:
                //可攀爬物
                return "climb";
            default:
                return "block";
        }
    }

    @Override
    public boolean onUpdate(int tickDiff) {
        setUpdateTime();
        if (!this.isMove){
            this.motionZ = 0;
            this.motionX = 0;
            updateMovement();
        }
        double x = pos.x - this.x;
        double y = pos.y - this.y;
        double z = pos.z - this.z;
        double diff = Math.abs(x) + Math.abs(z);
        this.motionX = this.getSpeed() * 0.15 * (x / diff);
        this.motionZ = this.getSpeed() * 0.15 * (z / diff);
        double dz = this.motionZ * tickDiff;
        double dx = this.motionX * tickDiff;
        this.move(dx, this.motionY * tickDiff, dz);
        Block block = this.getLevel().getBlock(new Vector3(NukkitMath.floorDouble(this.x + dx), (int) this.y, NukkitMath.floorDouble(this.z + dz))).getSide(this.getHorizontalFacing());
        this.yaw = Math.toDegrees(-Math.atan2(x/diff, z/diff));
        this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        Block down_block = level.getBlock(new Vector3(block.x,block.y-1,block.z));
        Block up_1_block = level.getBlock(new Vector3(block.x,block.y+1,block.z));
        Block up_2_block = level.getBlock(new Vector3(block.x,block.y+2,block.z));
            if (getBlock(down_block).equals("lava")||getBlock(up_1_block).equals("lava") || (getBlock(up_1_block).equals("block"))){
                this.motionX = -this.motionX*tickDiff;
                this.motionZ = -this.motionY*tickDiff;
                getNextPos();
                updateMovement();
            }
            switch (getBlock(block)) {
                case "lava":
                    this.motionX = -this.motionX*tickDiff;
                    this.motionZ = -this.motionY*tickDiff;
                    getNextPos();
                    updateMovement();
                case "pass":
                    //keep move
                    this.motionY = this.getGravity();
                    this.motionX = -this.motionX*tickDiff;
                    this.motionZ = -this.motionY*tickDiff;
                    updateMovement();
                    break;
                case "block":
                    this.motionY = this.getGravity() * 8;
                    this.motionX = this.motionX*tickDiff;
                    this.motionZ = this.motionY*tickDiff;
                    updateMovement();
                    break;
                case "half":
                case "no":
                    this.motionX = -this.motionX*tickDiff;
                    this.motionZ = -this.motionY*tickDiff;
                    getNextPos();
                    updateMovement();
                    break;
                default:
                    this.motionY += this.getGravity() * 0.25;
                    this.motionX = this.motionX*tickDiff;
                    this.motionZ = this.motionY*tickDiff;
                    break;
            }
        updateMovement();
        return super.onUpdate(tickDiff);
    }

}
