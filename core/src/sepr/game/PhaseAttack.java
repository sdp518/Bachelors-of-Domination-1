package sepr.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import org.lwjgl.Sys;
import sepr.game.utils.TurnPhaseType;

import java.util.Random;

/**
 * handles input, updating and rendering for the attack phase
 */
public class PhaseAttack extends PhaseAttackMove{

    public AudioManager Audio = AudioManager.getInstance();



    public PhaseAttack(GameScreen gameScreen) {
        super(gameScreen, TurnPhaseType.ATTACK);

    }


    /**
     * creates a dialog asking the player how many units they want to attack with
     *
     * @throws RuntimeException if the attacking sector or defending sector are set to null
     */
    private void getNumberOfAttackers() throws RuntimeException {
        if (startingSector == null || destinationSector == null) {
            throw new RuntimeException("Cannot execute attack unless both an attacking and defending sector have been selected");
        }
        troopsToMove = new int[1];
        troopsToMove[0] = -1;
        DialogFactory.selectUnitTypeDialog(this, this);
    }

    public void showUndergraduateSelection() {
        DialogFactory.attackDialog(startingSector.getUndergraduatesInSector(), destinationSector.getUndergraduatesInSector(), troopsToMove, this);
    }

    public void postgraduateAttack() {
        if (startingSector.getPostgraduateStatus() == 1) {
            DialogFactory.basicDialogBox("Postgraduate", "Postgraduates may only cast once per turn", this);
        }
        else if (startingSector.getPostgraduateStatus() == 0) {
            Random random = new Random();
            int damage = random.nextInt(4) + 1; // random amount of damage between 1 and 5
            if (damage > destinationSector.getUndergraduatesInSector()) { // prevents damage from being greater than the number of units in the sector
                damage = destinationSector.getUndergraduatesInSector();
            }
            startingSector.setPostgraduateStatus(true);
            if (gameScreen.getMap().attackSector(startingSector.getId(), destinationSector.getId(), 0, damage, gameScreen.getPlayerById(startingSector.getOwnerId()), gameScreen.getPlayerById(destinationSector.getOwnerId()), gameScreen.getPlayerById(gameScreen.NEUTRAL_PLAYER_ID), this)) {
                updateTroopReinforcementLabel();
            }
        }
    }

    /**
     * carries out attack once number of attackers has been set using the dialog
     */
    private void executeAttack() {
        int attackers = troopsToMove[0];
        int defenders = destinationSector.getUndergraduatesInSector();

        float propAttack = (float)attackers / (float)(attackers + defenders); // proportion of troops that are attackers
        float propDefend = (float)defenders / (float)(attackers + defenders); // proportion of troops that are defenders

        // calculate the proportion of attackers and defenders lost
        float propAttackersLost = (float)Math.max(0, Math.min(1, 0.02 * Math.exp(5 * propDefend) + 0.1 + (-0.125 + random.nextFloat()/4)));
        float propDefendersLost = (float)Math.max(0, Math.min(1, 0.02 * Math.exp(5 * propAttack) + 0.15 + (-0.125 + random.nextFloat()/4)));

        if (propAttack == 1) { // if attacking an empty sector then no attackers will be lost
            propAttackersLost = 0;
            propDefendersLost = 1;
        }

        int attackersLost = (int)(attackers * propAttackersLost);
        int defendersLost = (int)(defenders * propDefendersLost);

        if(attackersLost > defendersLost){
            // Poor Move
            int voice = random.nextInt(3);

            switch (voice){
                case 0:
                    Audio.get("sound/Invalid Move/Colin_Your_actions_are_questionable.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 1:
                    Audio.get("sound/Battle Phrases/Colin_Seems_Risky_To_Me.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 2:
                    break;
            }
        } else {
            // Good move
            int voice = random.nextInt(5);

            switch (voice){
                case 0:
                    Audio.get("sound/Battle Phrases/Colin_An_Unlikely_Victory.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 1:
                    Audio.get("sound/Battle Phrases/Colin_Far_better_than_I_expected.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 2:
                    Audio.get("sound/Battle Phrases/Colin_I_couldnt_have_done_it_better_myself.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 3:
                    Audio.get("sound/Battle Phrases/Colin_Multiplying_by_the_identity_matrix_is_more_fasinating_than_your_last_move.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 4:
                    Audio.get("sound/Battle Phrases/Colin_Well_Done.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 5:
                    break;
            }
        }

        // apply the attack to the map
        if (gameScreen.getMap().attackSector(startingSector.getId(), destinationSector.getId(), attackersLost, defendersLost, gameScreen.getPlayerById(startingSector.getOwnerId()), gameScreen.getPlayerById(destinationSector.getOwnerId()), gameScreen.getPlayerById(gameScreen.NEUTRAL_PLAYER_ID), this)) {


            updateTroopReinforcementLabel();
        }
    }



    /**
     * process an attack if one is being carried out
     */
    @Override
    public void phaseAct() {
        if (startingSector != null && destinationSector != null && troopsToMove[0] != -1) {

            if (troopsToMove[0] == 0) {
                // cancel attack
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
                        break;
                }
            }

            else {
                    executeAttack();
                }
            // reset attack
            startingSector = null;
            destinationSector = null;
            troopsToMove = null;
        }
    }


    /**
     *
     * @param screenX mouse x position on screen when clicked
     * @param screenY mouse y position on screen when clicked
     * @param pointer pointer to the event
     * @param button which button was pressed
     * @return if the event has been handled
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (super.touchUp(screenX, screenY, pointer, button)) {
            return true;
        }

        Vector2 worldCoord = gameScreen.screenToWorldCoords(screenX, screenY);

        int sectorId = gameScreen.getMap().detectSectorContainsPoint((int)worldCoord.x, (int)worldCoord.y);
        if (sectorId != -1) { // If selected a sector

            Sector selected = gameScreen.getMap().getSectorById(sectorId); // Current sector
            boolean notAlreadySelected = this.startingSector == null && this.destinationSector == null; // T/F if the attack sequence is complete

            if (this.startingSector != null && this.destinationSector == null) { // If its the second selection in the attack phase

                if (this.startingSector.isAdjacentTo(selected) && selected.getOwnerId() != this.currentPlayer.getId()) { // check the player does not own the defending sector and that it is adjacent
                    this.arrowHeadPosition.set(worldCoord.x, worldCoord.y); // Finalise the end position of the arrow
                    this.destinationSector = selected;


                    getNumberOfAttackers();
                } else { // cancel attack as selected defending sector cannot be attack: may not be adjacent or may be owned by the attacker
                    this.startingSector = null;
                }

            } else if (selected.getOwnerId() == this.currentPlayer.getId() && selected.getUndergraduatesInSector() > 1 && notAlreadySelected) { // First selection, is owned by the player and has enough troops
                this.startingSector = selected;
                this.arrowTailPosition.set(worldCoord.x, worldCoord.y); // set arrow tail position
            } else {
                this.startingSector = null;
                this.destinationSector = null;
            }
        } else { // mouse pressed and not hovered over a sector to attack therefore cancel any attack in progress
            this.startingSector = null;
            this.destinationSector = null;
        }

        return true;
    }
}
