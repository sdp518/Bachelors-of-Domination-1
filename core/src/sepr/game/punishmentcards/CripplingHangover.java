package sepr.game.punishmentcards;

public class CripplingHangover extends Card {
    public CripplingHangover() {
        super(CardType.CRIPPLING_HANGOVER);
    }

    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
