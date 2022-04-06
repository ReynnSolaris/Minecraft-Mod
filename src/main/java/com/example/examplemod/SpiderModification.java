package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SpiderModification {
    private static final Logger LOGGER = LogUtils.getLogger();
    List<Monster> SpiderParty = new ArrayList<>();
    @SubscribeEvent
    public void onWorldEvent(AttackEntityEvent event) {
        Player p = event.getPlayer();
        Level l = p.getLevel();

        if(!l.isClientSide()) {
            Entity e = event.getTarget();
            if(e.getType() == EntityType.SPIDER) {
                final CompoundTag data = e.getPersistentData();
                if(data.getString("SPIDERATTACK").equalsIgnoreCase("")) {
                    data.putString("SPIDERATTACK", java.util.UUID.randomUUID().toString());
                    for (int i = 0; i < 6; i++) {
                        CaveSpider cv = new CaveSpider(EntityType.CAVE_SPIDER, l);
                        cv.setHealth(Float.MAX_VALUE);
                        cv.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1.2D);
                        cv.setPos(e.position());
                        cv.setCustomName(new TextComponent(ChatFormatting.GREEN+"Lil Dumpy"));
                        cv.getPersistentData().putString("SPIDERATTACK", data.getString("SPIDERATTACK"));
                        l.addFreshEntity(cv);
                        SpiderParty.add(cv);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMobDied(LivingDeathEvent event) {
        Entity e = event.getEntity();
        Level l = e.getLevel();
        if (!l.isClientSide()) {
            if(e.getType() == EntityType.SPIDER) {
                final CompoundTag ct = e.getPersistentData();
                if (!ct.getString("SPIDERATTACK").equalsIgnoreCase("")) {
                    String UUIDSpider = ct.getString("SPIDERATTACK");
                    for (Monster monster : SpiderParty) {
                        if(monster.getHealth() >= 1) {
                            if (monster.getPersistentData().getString("SPIDERATTACK").equalsIgnoreCase(UUIDSpider)) {
                                monster.setHealth(-1);
                                monster.kill();
                                //SpiderParty.remove(monster);
                            }
                        }
                    }
                }
            }
        }
    }


}
