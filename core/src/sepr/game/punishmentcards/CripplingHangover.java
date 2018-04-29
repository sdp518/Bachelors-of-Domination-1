package sepr.game.punishmentcards;

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
    public void act(Player player, GameScreen gameScreen) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
        player.switchCripplingHangover();
    }
}
