package sepr.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import sepr.game.utils.TurnPhaseType;

import java.util.Random;

/**
 * handles input, updating and rendering for the reinforcement phase
 */
public class PhaseReinforce extends Phase {
    public AudioManager Audio = AudioManager.getInstance();

    private int[] allocateUnits; // 2 index array storing : [0] number of troops to allocate ; [1] id of sector to allocate to

    private Random random;

    public PhaseReinforce(GameScreen gameScreen) {
        super(gameScreen, TurnPhaseType.REINFORCEMENT);
        random = new Random();
    }

    @Override
    public void enterPhase(Player player) {
        super.enterPhase(player);

        currentPlayer.addTroopsToAllocate(5); // players get a basic reinforcement of 5 troops every turn
        if (player.getOwnsPVC())  // assigns a bonus of two troops if they own the PVC tile
        {
            currentPlayer.addTroopsToAllocate(2);

        }

        updateTroopReinforcementLabel();
        DialogFactory.nextTurnDialogBox(currentPlayer.getPlayerName(), currentPlayer.getTroopsToAllocate(), this);
    }

    /**
     * MODIFIED 23/4/18 - Commented out line to prevent unallocated units from being lost
     */
    @Override
    public void endPhase() {
        //currentPlayer.setTroopsToAllocate(0); // any unallocated units are removed
        super.endPhase();
    }

    /** MODIFIED 23/4/18 - Added auto end phase
     * checks if the user has completed the unit allocation dialog
     */
    private void detectUnitAllocation() {
        if (allocateUnits != null) { // check that an allocation has been initiated
            if (allocateUnits[1] == -1 || allocateUnits[0] == 0) { // cancel allocation if sector id set to -1 or 0 units are allocated
                allocateUnits = null;
            } else if (allocateUnits[0] != -1) { // dialog complete : perform the allocation
                gameScreen.getMap().addUnitsToSectorAnimated(allocateUnits[1], allocateUnits[0]);
                currentPlayer.addTroopsToAllocate(-allocateUnits[0]);
                if (currentPlayer.getTroopsToAllocate() == 0) { // auto end the phase if all troops have been allocated
                    allocateUnits = null;
                    updateTroopReinforcementLabel();
                    gameScreen.nextPhase();
                } else {
                    allocateUnits = null;
                    updateTroopReinforcementLabel();
                }
            }
        }
    }

    @Override
    public void phaseAct() {
        detectUnitAllocation();
    }

    @Override
    public void visualisePhase(SpriteBatch batch) {

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (super.touchUp(screenX, screenY, pointer, button)) {
            return true;
        }

        Vector2 worldCoord = gameScreen.screenToWorldCoords(screenX, screenY);

        int sectorId = gameScreen.getMap().detectSectorContainsPoint((int)worldCoord.x, (int)worldCoord.y);
        if (sectorId != -1) { // If selected a sector
            if (currentPlayer.getTroopsToAllocate() <= 0) { // check the player still has units to allocate
                int voice = random.nextInt(2);

                if(voice == 0){
                    Audio.get("sound/Allocation/Colin_Insuffiecient_Gangmembers.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                }else{
                    InvalidMove();
                }

                DialogFactory.basicDialogBox("Allocation Problem", "You have no more troops to allocate", this);
            } else if (gameScreen.getMap().getSectorById(sectorId).getOwnerId() != currentPlayer.getId()) { // check the player has chosen to add units to their own sector
                InvalidMove();
                DialogFactory.basicDialogBox("Allocation Problem", "Cannot allocate units to a sector you do not own", this);
            } else {
                // setup allocation form
                allocateUnits = new int[2];
                allocateUnits[0] = -1;
                allocateUnits[1] = sectorId;
                DialogFactory.allocateUnitsDialog(currentPlayer.getTroopsToAllocate(), allocateUnits, gameScreen.getMap().getSectorById(sectorId).getDisplayName(), this);
            }
        }
        return false;
    }

    private void InvalidMove(){
        int voice = random.nextInt(3);

        switch (voice){
            case 0:
                Audio.get("sound/Invalid Move/Colin_Your_request_does_not_pass_easily_through_my_mind.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                break;
            case 1:
                Audio.get("sound/Invalid Move/Colin_You_would_find_more_success_trying_to_invert_a_singular_matrix.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                break;
            case 2:
                Audio.get("sound/Invalid Move/Colin_Your_actions_are_questionable.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                break;
            case 3:
                Audio.get("sound/Allocation/Colin_EmptySet.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                break;
            case 4:
                break;
        }
    }
}
