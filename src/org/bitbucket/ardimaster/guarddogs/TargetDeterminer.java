package org.bitbucket.ardimaster.guarddogs;

import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ArdiMaster on 21.01.15.
 */
public class TargetDeterminer extends BukkitRunnable {
    private GuardDogs plugin;

    public TargetDeterminer(GuardDogs plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.targetDetermination = true;

        Random rand = new Random();
        double radiusSquare = 15 * 15;

        for (Wolf wolf : plugin.guards) {
            if (!wolf.isSitting() || plugin.guardWaits.containsValue(wolf)) {
                continue;
            }

            List<LivingEntity> all = wolf.getLocation().getWorld().getLivingEntities();
            ArrayList<LivingEntity> near = new ArrayList<>();
            ArrayList<Player> nearPlayers = new ArrayList<>();

            for (LivingEntity e : all) {
                if (e.getLocation().distanceSquared(wolf.getLocation()) <= radiusSquare) {
                    if (wolf.getOwner().equals(e)) {
                        continue;
                    }

                    if (e instanceof Wolf) {
                        if (plugin.guards.contains(e)) {
                            continue;
                        }
                        if (wolf.getOwner().equals(((Wolf) e).getOwner())) {
                            continue;
                        }
                    }

                    int yWolf = wolf.getLocation().getBlockY();
                    int yE = e.getLocation().getBlockY();
                    int yDelta = yE - yWolf;
                    if (yDelta > -6 && yDelta < 6) {
                        if (e instanceof Player) {
                            nearPlayers.add((Player) e);
                        } else {
                            if (!(e instanceof Sheep) && !(e instanceof Chicken) && !(e instanceof Cow) &&
                                    !(e instanceof Pig) && !(e instanceof Horse)) {
                                near.add(e);
                            }
                        }
                    }
                }
            }

            LivingEntity target;
            if (!nearPlayers.isEmpty()) {
                target = nearPlayers.get(rand.nextInt(nearPlayers.size()));
            } else {
                target = near.get(rand.nextInt(near.size()));
            }
            plugin.guardTargets.put(wolf, target);
            wolf.setSitting(false);
            wolf.damage(0, target);
        }

        plugin.targetDetermination = false;
    }
}
