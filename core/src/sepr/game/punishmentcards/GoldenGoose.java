package sepr.game.punishmentcards;

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
    public void act(Player player, GameScreen gameScreen) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
        player.switchGoldenGoose();
    }

    // TODO See Trello comment
}
