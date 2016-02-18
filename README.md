# PureEntities
  
Author(제작자): **[SW-Team](https://github.com/SW-Team)**  
  
자매품(PMMP): [PureEntities-PMMP](https://github.com/milk0417/PureEntities)
  
**[NOTICE] This plug-in is not perfect, the entity may move abnormally (It was written in Java8)**
  
PureEntities is a plugin for managing entities, literally.  
Most entities are moving around, and jumps if needed.  
  
PureEntities also has simple API for developers,  
such as **clear()** or **create()**.  
  
See documentation page for details.  
  
**[알림] 이 플러그인은 완벽하지 않으며 엔티티가 비정상적으로 움직일 수 있습니다 (Java8로 작성되었습니다)**  
  
엔티티매니저는 말 그대로 엔티티를 관리하는 플러그인을 의미합니다.  
많은 엔티티들은 주위를 돌아다니거나 뛰어다닙니다.  

엔티티매니저는 또한 개발자 여러분을 위해  
**clear()** 또는 **create()** 와 같은 간단한 API가 제공됩니다.  
  
자세한 사항은 아래를 보시기 바랍니다  
  
### Method(메소드)
  * BaseEntity
    * public boolean isMovement()
    * public boolean isFriendly()
    * public boolean isWallCheck()
    * 
    * public void setMovement(boolean value)
    * public void setFriendly(boolean value)
    * public void setWallCheck(boolean value)
  * Monster, FlyMonster
    * public double getDamage();
    * public double getDamage(int difficulty)
    * 
    * public double getMinDamage()
    * public double getMinDamage(int difficulty)
    * 
    * public double getMaxDamage()
    * public double getMaxDamage(int difficulty)
    * 
    * public void setDamage(double damage)
    * public void setDamage(double[] damage)
    * public void setDamage(double damage, int difficulty)
  * PigZombie, Wolf, Ocelot
    * public boolean isAngry()
    * public void setAngry(int angry)

### Method Examples(메소드 예시)
``` java
//Entity Method
this.getServer().getDefaultLevel().getEntities().forEach((id, baseEntity) -> {
    if(!baseEntity.isMovement()){
        baseEntity.setMovement(true);
    }
    if(baseEntity instanceof Monster){
        Monster mob = (Monster) baseEntity;

        mob.setDamage(10);

        mob.setMaxDamage(10);
        mob.setMinDamage(10);
    }
});
```