package sepr.game.punishmentcards;

public class FreshersFlu extends Card {
    public FreshersFlu() {
        super(CardType.FRESHERS_FLU);
    }

    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
