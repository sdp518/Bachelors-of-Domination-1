package sepr.game.punishmentcards;

import sepr.game.DialogFactory;
import sepr.game.GameScreen;
import sepr.game.Player;

public class CripplingHangover extends Card {
    public CripplingHangover() {
        super(CardType.CRIPPLING_HANGOVER, true, false);
    }

    /**
     * Effect: Reduces turn timer for next turn of affected player
     */
    @Override
    public boolean act(Player player, GameScreen gameScreen) {
        if (player.hasCripplingHangover()) {
            DialogFactory.basicDialogBox("Don't be cruel!", "This player already has a crippling hangover!", gameScreen.getCardStage());
            return false;
        }
        else {
            player.switchCripplingHangover();
            return true;
        }

    }
}
