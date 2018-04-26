package sepr.game.punishmentcards;

public class FreshersFlu extends Card {
    public FreshersFlu() {
        super(CardType.FRESHERS_FLU);
    }

    /**
     * Effect: Debuffs affected player (half strength)
     */
    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
