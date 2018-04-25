package sepr.game.punishmentcards;

public class Strike extends Card {
    public Strike() {
        super(CardType.STRIKE);
    }

    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
