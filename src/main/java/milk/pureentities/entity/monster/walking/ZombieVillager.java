package milk.pureentities.entity.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.monster.WalkingMonster;
import milk.pureentities.util.Utils;

import java.util.HashMap;

public class ZombieVillager extends WalkingMonster{
    public static final int NETWORK_ID = 44;

    public ZombieVillager(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.72f;
    }

    @Override
    public float getHeight(){
        return 1.8f;
    }

    @Override
    public double getSpeed(){
        return 1.1;
    }

    @Override
    public void initEntity(){
        super.initEntity();

        this.setDamage(new int[]{0, 3, 4, 6});
    }

    @Override
    public String getName(){
        return "ZombieVillager";
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1){
            this.attackDelay = 0;
            HashMap<Integer, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.MODIFIER_BASE, (float) this.getDamage());

            if(player instanceof Player){
                HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>() {{
                    put(Item.LEATHER_CAP, 1f);
                    put(Item.LEATHER_TUNIC, 3f);
                    put(Item.LEATHER_PANTS, 2f);
                    put(Item.LEATHER_BOOTS, 1f);
                    put(Item.CHAIN_HELMET, 1f);
                    put(Item.CHAIN_CHESTPLATE, 5f);
                    put(Item.CHAIN_LEGGINGS, 4f);
                    put(Item.CHAIN_BOOTS, 1f);
                    put(Item.GOLD_HELMET, 1f);
                    put(Item.GOLD_CHESTPLATE, 5f);
                    put(Item.GOLD_LEGGINGS, 3f);
                    put(Item.GOLD_BOOTS, 1f);
                    put(Item.IRON_HELMET, 2f);
                    put(Item.IRON_CHESTPLATE, 6f);
                    put(Item.IRON_LEGGINGS, 5f);
                    put(Item.IRON_BOOTS, 2f);
                    put(Item.DIAMOND_HELMET, 3f);
                    put(Item.DIAMOND_CHESTPLATE, 8f);
                    put(Item.DIAMOND_LEGGINGS, 6f);
                    put(Item.DIAMOND_BOOTS, 3f);
                }};

                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }

                damage.put(EntityDamageEvent.MODIFIER_ARMOR, (float) (damage.getOrDefault(EntityDamageEvent.MODIFIER_ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.MODIFIER_BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, damage));
        }
    }

    @Override
    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0:
                    return new Item[]{Item.get(Item.FEATHER, 0, 1)};
                case 1:
                    return new Item[]{Item.get(Item.CARROT, 0, 1)};
                case 2:
                    return new Item[]{Item.get(Item.POTATO, 0, 1)};
            }
        }
        return new Item[0];
    }

}
