package milk.entitymanager.entity.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.Player;
import milk.entitymanager.EntityManager;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class SnowGolem extends WalkingMonster{
    public static final int NETWORK_ID = 21;

    public SnowGolem(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.6f;
    }

    @Override
    public float getHeight(){
        return 1.8f;
    }

    @Override
    public void initEntity(){
        super.initEntity();

        this.setFriendly(true);
    }

    @Override
    public String getName(){
        return "SnowGolem";
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance){
        return !(creature instanceof Player) && creature.isAlive() && distance <= 60;
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 23  && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 55){
            this.attackDelay = 0;

            double f = 1.2;
            double yaw = this.yaw + Utils.rand(-220, 220) / 10;
            double pitch = this.pitch + Utils.rand(-120, 120) / 10;
            Location location = new Location(
                this.x + (-Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5),
                this.getEyeHeight(),
                this.z +(Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5),
                yaw,
                pitch,
                this.level
            );
            Entity k = EntityManager.create("EntitySnowball", location, this);
            if(k == null){
                return;
            }

            EntitySnowball snowball = (EntitySnowball) k;
            snowball.setMotion(new Vector3(
                -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f,
                -Math.sin(pitch / 180 * Math.PI) * f * f,
                Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f
            ));

            Vector3 motion = new Vector3(
                -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f,
                -Math.sin(pitch / 180 * Math.PI) * f * f,
                Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f
            ).multiply(f);
            snowball.setMotion(motion);

            EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), snowball, f);
            this.server.getPluginManager().callEvent(ev);

            EntityProjectile projectile = ev.getProjectile();
            if(ev.isCancelled()){
                projectile.kill();
            }else if(projectile != null){
                ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile);
                this.server.getPluginManager().callEvent(launch);
                if(launch.isCancelled()){
                    projectile.kill();
                }else{
                    projectile.spawnToAll();
                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                }
            }
        }
    }

    @Override
    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return new Item[]{Item.get(Item.SNOWBALL, 0, 15)};
        }
        return new Item[0];
    }

}
