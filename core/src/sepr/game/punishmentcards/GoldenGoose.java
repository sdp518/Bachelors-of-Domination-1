package sepr.game.punishmentcards;

import sepr.game.DialogFactory;
import sepr.game.GameScreen;
import sepr.game.Player;

public class GoldenGoose extends Card {
    public GoldenGoose() {
        super(CardType.GOLDEN_GOOSE, true, false);
    }

    /**
     * Effect: Plays goose sounds for the duration of the next turn of the affected player
     */
    @Override
    public boolean act(Player player, GameScreen gameScreen) {
        if (player.hasGoldenGoose()) {
            DialogFactory.basicDialogBox("One goose is enough!", "This player already has a golden goose!", gameScreen.getCardStage());
            return false;
        }
        else {
            player.switchGoldenGoose();
            return true;
        }
    }
}
