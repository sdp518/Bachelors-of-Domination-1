package sepr.game;
import com.badlogic.gdx.graphics.Color;
import org.lwjgl.Sys;

import java.util.Random;


/**
 * base class for storing Neutral and Human player data
 */
public class PVC {

    private float spawnChance; //likelihood that the PVC will spawn , between 0 and 1
                                // 0 = 0% ; 1 = 100%
    private float PVCBonus; //the bonus the pvc gives
    private boolean PVCSpawned = false;
    private GameScreen gameScreen;

    /**
     * creates a player object with the specified properties
     *
     * @param spawnChance       player's unique identifier
     * @param PVCBonus  display name for this player
     */


    public PVC(float spawnChance, int PVCBonus, GameScreen gameScreen)
    {
        this.spawnChance = spawnChance;
        this.PVCBonus = PVCBonus;
        this.gameScreen = gameScreen;
    }

    public boolean PVCSpawn()
    {
        Random rand = new Random();
        Float randomValue = rand.nextFloat();
        if(randomValue <= spawnChance && (!PVCSpawned))
        {

            return true;
        }


        return false;
    }

    public void startMiniGame(){

        System.out.println("ive been called");
        gameScreen.openMiniGame();
    }


    public boolean isPVCSpawned() { return PVCSpawned; }

    public void setPVCSpawned(boolean value) {this.PVCSpawned = value ; }

    public GameScreen getGameScreen(){return this.gameScreen;}

    public float getSpawnChance() {
        return spawnChance;
    }

    public void setSpawnChance(float spawnChance) {
        this.spawnChance = spawnChance;
    }

    public float getPVCBonus() {
        return PVCBonus;
    }

    public void setPVCBonus(float PVCBonus) {
        this.PVCBonus = PVCBonus;
    }

}