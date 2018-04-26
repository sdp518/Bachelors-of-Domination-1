package sepr.game.punishmentcards;

import sepr.game.Player;

public class ExceptionalCircumstances extends Card {
    public ExceptionalCircumstances() {
        super(CardType.EXCEPTIONAL_CIRCUMSTANCES);
    }

    /**
     * Effect: Swap sectors with chosen player
     */
    @Override
    public void act(Player player) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
    }
}
