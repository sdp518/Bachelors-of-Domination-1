package sepr.game.punishmentcards;

import sepr.game.Player;

public class FreshersFlu extends Card {
    public FreshersFlu() {
        super(CardType.FRESHERS_FLU);
    }

    /**
     * Effect: Debuffs affected player (half strength)
     */
    @Override
    public void act(Player player) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
    }
}
