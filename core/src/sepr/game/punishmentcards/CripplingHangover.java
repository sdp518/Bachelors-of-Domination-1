package sepr.game.punishmentcards;

public class CripplingHangover extends Card {
    public CripplingHangover() {
        super(CardType.CRIPPLING_HANGOVER);
    }

    /**
     * Effect: Reduces turn timer for next turn of affected player
     */
    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
