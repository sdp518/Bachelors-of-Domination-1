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

    /** MODIFIED ASSESSMENT 4 - ADDED SUPPORT FOR PGS, RENAMED VARIABLES FOR CLARITY
     * creates a dialog asking the player how many units they want to attack with
     *
     * @throws RuntimeException if the attacking sector or defending sector are set to null
     */
    private void getNumberToMove() throws RuntimeException {
        if (startingSector == null || destinationSector == null) {
            throw new RuntimeException("Cannot execute attack unless both a starting and destination sector have been selected");
        }
        troopsToMove = new int[2];
        troopsToMove[0] = -1;
        troopsToMove[1] = -1;
        DialogFactory.moveDialog(startingSector.getUndergraduatesInSector(), startingSector.getPostgraduatesInSector(), troopsToMove, this);
    }

    /** MODIFIED ASSESSMENT 4 - ADDED SUPPORT FOR PGS, RENAMED VARIABLES FOR CLARITY
     * carries out movement once number of troops has been set using the dialog
     */
    private void executeMoveTroops() {

        System.out.println("MOVED BOII");
        int startUndergraduates = troopsToMove[0];
        int endUndergraduates = troopsToMove[0];
        int startPostgraduates = troopsToMove[1];
        int endPostgraduates = troopsToMove[1];


        // apply the movement to the map
        if (gameScreen.getMap().moveTroops(startingSector.getId(), destinationSector.getId(), startUndergraduates, endUndergraduates, startPostgraduates, endPostgraduates)) {
            updateTroopReinforcementLabel();
        }
        else {
            DialogFactory.basicDialogBox("Postgraduates", "Cannot move Postgraduate to a sector that already contains one", this);
        }
    }

    /**
     * process a movement if one is being carried out
     */
    @Override
    public void phaseAct() {


        if (startingSector != null && destinationSector != null && troopsToMove[0] != -1 && troopsToMove[1] != -1) {

            if (troopsToMove[0] == 0 && troopsToMove[1] == 0) {

                // cancel attack
            } else {
                executeMoveTroops();
            }
            // reset attack
            startingSector = null;
            destinationSector = null;
            troopsToMove = null;
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


            if (this.startingSector != null && this.destinationSector == null) { // If its the second selection in the movement phase

                if (this.startingSector.isAdjacentTo(selected) && selected.getOwnerId() == this.currentPlayer.getId()) { // check the player does owns the defending sector and that it is adjacent
                    this.arrowHeadPosition.set(worldCoord.x, worldCoord.y); // Finalise the end position of the arrow
                    this.destinationSector = selected;

                    getNumberToMove(); // attacking and defending sector selected so find out how many units the player wants to move with
                } else { // cancel the movement as selected defending sector cannot be moved to: may not be adjacent or may be owned by the attacker
                    this.startingSector = null;
                }

            } else if (selected.getOwnerId() == this.currentPlayer.getId() && (selected.getUndergraduatesInSector() > 1 || selected.getPostgraduatesInSector() > 0)) { // First selection, is owned by the player and has enough troops
                this.startingSector = selected;
                this.arrowTailPosition.set(worldCoord.x, worldCoord.y); // set arrow tail position
            } else {
                this.startingSector = null;
                this.destinationSector = null;
            }
        } else { // mouse pressed and not hovered over a sector to attack therefore cancel any movement in progress
            this.startingSector = null;
            this.destinationSector = null;
        }

        return true;
    }
}

