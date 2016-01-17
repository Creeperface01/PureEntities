package milk.entitymanager.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.entity.Creature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

import java.util.HashMap;

public abstract class WalkEntity extends BaseEntity{

    public WalkEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    void checkTarget(){
    	if(this.getViewers().isEmpty()){
            return;
        }
        Vector3 target = this.baseTarget;
        if(!(target instanceof Creature) || !this.targetOption((Creature) target, this.distanceSquared(target))){
            double near = Integer.MAX_VALUE;

            for(Entity ent : this.getLevel().getEntities()){
                if(!(ent instanceof Creature) || ent instanceof Animal || ent == this) continue;
                Creature creature = (Creature) ent;

                if(creature instanceof BaseEntity && ((BaseEntity) creature).isFriendly() == this.isFriendly()) continue;

                double distance;
                if((distance = this.distanceSquared(creature)) > near || !this.targetOption(creature, distance)) continue;
                near = distance;
                this.baseTarget = creature;
            }
        }
        if(this.baseTarget instanceof Creature)
        	if(((Creature) this.baseTarget).isAlive()){
                return;
            }
        if(this.stayTime > 0){
            if(Utils.rand(1, 125) > 4) return;
            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            this.baseTarget = this.add(Utils.rand(0, 1) == 0 ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand(0, 1) == 0 ? z : -z);
        }else if(Utils.rand(1, 420) == 1){
            this.stayTime = Utils.rand(95, 420);
            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            this.baseTarget = this.add(Utils.rand(0, 1) == 0 ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand(0, 1) == 0 ? z : -z);
        }else if(this.moveTime <= 0 || this.baseTarget == null){
            this.moveTime = Utils.rand(100, 1000);
            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            this.baseTarget = this.add(Utils.rand(0, 1) == 0 ? x : -x, 0, Utils.rand(0, 1) == 0 ? z : -z);
        }
    }

    public Vector3 updateMove(){
        if(!this.isMovement()) return null;
        
        if(this.attacker != null){
            if(this.atkTime == 16){
                Vector3 target = this.attacker;
                double x = target.x - this.x;
                double z = target.z - this.z;
                double diff = Math.abs(x) + Math.abs(z);
                this.motionX = -0.5 * (diff == 0 ? 0 : x / diff);
                this.motionZ = -0.5 * (diff == 0 ? 0 : z / diff);
                --this.atkTime;
            }
            
            HashMap<Integer, Double> y = new HashMap<Integer, Double>(){{
                put(11, 0.3d);
                put(12, 0.3d);
                put(13, 0.4d);
                put(14, 0.4d);
                put(15, 0.5d);
                put(16, 0.5d);
            }};
            this.move(this.motionX, y.containsKey(this.atkTime) ?  y.get(this.atkTime) : -0.2, this.motionZ);
            
            if(--this.atkTime <= 0){
            	this.attacker = null;
            	this.motionX = 0;
            	this.motionY = 0;
            	this.motionZ = 0;
            }
            return null;
        }
        
        Vector3 before = this.baseTarget;
        this.checkTarget();
        if(this.baseTarget instanceof Creature || before != this.baseTarget){
            x = this.baseTarget.x - this.x;
            y = this.baseTarget.y - this.y;
            z = this.baseTarget.z - this.z;
            if(this.stayTime > 0 || x * x + z * z < 0.5){
                this.motionX = 0;
                this.motionZ = 0;
            }else{
                double diff = Math.abs(x) + Math.abs(z);
                this.motionX = this.getSpeed() * 0.15 * (x / diff);
                this.motionZ = this.getSpeed() * 0.15 * (z / diff);
            }
            this.yaw = -Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }
        
        Vector3 target = this.mainTarget != null ? this.mainTarget : this.baseTarget;
        if(this.stayTime > 0){
            --this.stayTime;
        }else{
            boolean isJump = false;
            double dx = this.motionX;
            double dy = this.motionY;
            double dz = this.motionZ;

            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, dy, dz);
            Vector2 af = new Vector2(this.x, this.z);

            if(be.x != af.x || be.y != af.y){
                x = 0;
                z = 0;
                if(be.x - af.x != 0) x += be.x - af.x > 0 ? 1 : -1;
                if(be.y - af.y != 0) z += be.y - af.y > 0 ? 1 : -1;

                Block block = this.level.getBlock((new Vector3(NukkitMath.floorDouble(be.x) + x, this.y, NukkitMath.floorDouble(af.y) + z)).floor());
                Block block2 = this.level.getBlock((new Vector3(NukkitMath.floorDouble(be.x) + x, this.y + 1, NukkitMath.floorDouble(af.y) + z)).floor());
                if(!block.canPassThrough()){
                    AxisAlignedBB bb = block2.getBoundingBox();
                    if(block2.canPassThrough() || (bb == null || bb.maxY - this.y <= 1)){
                        isJump = true;
                        this.motionY = 0.2;
                    }else{
                        if(this.level.getBlock(block.add(-x, 0, -z)).getId() == Item.LADDER){
                            isJump = true;
                            this.motionY = 0.2;
                        }
                    }
                }
                if(!isJump){
                    this.moveTime = 0;
                }
            }

            if(this.onGround && !isJump){
                this.motionY = 0;
            }else if(!isJump){
                this.motionY -= this.getGravity();
            }
        }
        this.updateMovement();
        return target;
    }

}