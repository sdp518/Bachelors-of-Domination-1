package sepr.game.punishmentcards;

import sepr.game.Player;

public class GoldenGoose extends Card {
    public GoldenGoose() {
        super(CardType.GOLDEN_GOOSE);
    }

    /**
     * Effect: Plays goose sounds for the duration of the next turn of the affected player
     */
    @Override
    public void act(Player player) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
    }
}
