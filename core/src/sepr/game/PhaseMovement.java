package sepr.game;

import com.badlogic.gdx.math.Vector2;
import sepr.game.utils.TurnPhaseType;


/**
 * handles input, updating and rendering for the movement phase
 * not implemented
 */
public class PhaseMovement extends PhaseAttackMove {


    public PhaseMovement(GameScreen gameScreen) {
        super(gameScreen, TurnPhaseType.MOVEMENT);

    }

    /**
     * creates a dialog asking the player how many units they want to attack with
     *
     * @throws RuntimeException if the attacking sector or defending sector are set to null
     */
    private void getNumberOfAttackers() throws RuntimeException {
        if (attackingSector == null || defendingSector == null) {
            throw new RuntimeException("Cannot execute attack unless both an attacking and defending sector have been selected");
        }
        numOfAttackers = new int[1];
        numOfAttackers[0] = -1;
        DialogFactory.moveDialog(attackingSector.getUnitsInSector().size(), numOfAttackers, this);
    }

    /**
     * carries out movement once number of troops has been set using the dialog
     */
    private void executeMoveTroops() {

        int attackersLost = numOfAttackers[0];
        int defendersLost = numOfAttackers[0];


        // apply the movement to the map
        if (gameScreen.getMap().moveTroops(attackingSector.getId(), defendingSector.getId(), attackersLost, defendersLost)) {


            updateTroopReinforcementLabel();
        }
    }

    /**
     * process a movement if one is being carried out
     */
    @Override
    public void phaseAct() {


        if (attackingSector != null && defendingSector != null && numOfAttackers[0] != -1) {

            if (numOfAttackers[0] == 0) {

                // cancel attack
            } else {
                executeMoveTroops();
            }
            // reset attack
            attackingSector = null;
            defendingSector = null;
            numOfAttackers = null;
        }
    }


    /**
     * @param screenX mouse x position on screen when clicked
     * @param screenY mouse y position on screen when clicked
     * @param pointer pointer to the event
     * @param button  which button was pressed
     * @return if the event has been handled
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (super.touchUp(screenX, screenY, pointer, button)) {
            return true;
        }

        Vector2 worldCoord = gameScreen.screenToWorldCoords(screenX, screenY);

        int sectorId = gameScreen.getMap().detectSectorContainsPoint((int) worldCoord.x, (int) worldCoord.y);
        if (sectorId != -1) { // If selected a sector

            Sector selected = gameScreen.getMap().getSectorById(sectorId); // Current sector


            if (this.attackingSector != null && this.defendingSector == null) { // If its the second selection in the movement phase

                if (this.attackingSector.isAdjacentTo(selected) && selected.getOwnerId() == this.currentPlayer.getId()) { // check the player does owns the defending sector and that it is adjacent
                    this.arrowHeadPosition.set(worldCoord.x, worldCoord.y); // Finalise the end position of the arrow
                    this.defendingSector = selected;

                    getNumberOfAttackers(); // attacking and defending sector selected so find out how many units the player wants to move with
                } else { // cancel the movement as selected defending sector cannot be moved to: may not be adjacent or may be owned by the attacker
                    this.attackingSector = null;
                }

            } else if (selected.getOwnerId() == this.currentPlayer.getId() && selected.getUnitsInSector().size() > 1) { // First selection, is owned by the player and has enough troops
                this.attackingSector = selected;
                this.arrowTailPosition.set(worldCoord.x, worldCoord.y); // set arrow tail position
            } else {
                this.attackingSector = null;
                this.defendingSector = null;
            }
        } else { // mouse pressed and not hovered over a sector to attack therefore cancel any movement in progress
            this.attackingSector = null;
            this.defendingSector = null;
        }

        return true;
    }
}

