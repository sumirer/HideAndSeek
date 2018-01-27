package sole.memory.seek.entitys.animal;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import sole.memory.seek.entitys.EntityUpdate;

import java.util.Random;

public class Pig extends EntityUpdate{
    public static final int NETWORK_ID = 12;

    public Pig(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return 12;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        Random random = new Random();
        if (random.nextInt(11)>5){
            setBaby();
        }

    }

    public void setBaby() {
        this.setDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY,true);
    }
}
