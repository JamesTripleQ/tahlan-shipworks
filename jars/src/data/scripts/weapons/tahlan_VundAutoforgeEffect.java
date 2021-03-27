//Based on Tartiflette's missileFab script. Thanks, buddy
package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class tahlan_VundAutoforgeEffect implements EveryFrameWeaponEffectPlugin {

    private boolean runOnce = false;
    private ShipSystemAPI theSystem;
    private final Map<WeaponAPI, Float> LAUNCHERS = new HashMap<>();

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (!runOnce){
            LAUNCHERS.clear();
            runOnce=true;
            theSystem = weapon.getShip().getSystem();
            //List all reloadable weapons
            for (WeaponAPI w : weapon.getShip().getAllWeapons()){
                if (w.usesAmmo() && w.getType() == WeaponAPI.WeaponType.MISSILE){
                    LAUNCHERS.put(w, 0f);
                }
            }
        }

        if (engine.isPaused() || LAUNCHERS.isEmpty()) {
            return;
        }

        for (Iterator<Map.Entry< WeaponAPI , Float >> iter = LAUNCHERS.entrySet().iterator(); iter.hasNext();) {
            //dig through the list of weapons
            Map.Entry<WeaponAPI, Float> entry = iter.next();

            if (entry.getKey().getAmmo() == entry.getKey().getMaxAmmo()) {
                //reset the loader if the weapon is already fully loaded
                LAUNCHERS.put(entry.getKey(), 0f);
            } else {
                //build 10% of the total ammo per minute
                float build = entry.getValue() + (amount * entry.getKey().getSpec().getMaxAmmo() * 0.1f / 60f);
                if (build >= 1) {
                    //add one ammo if it's built
                    LAUNCHERS.put(entry.getKey(), build - 1);
                    entry.getKey().setAmmo(entry.getKey().getAmmo() + 1);
                } else {
                    //store the progression otherwise
                    LAUNCHERS.put(entry.getKey(), build);
                }
            }
        }
    }
}