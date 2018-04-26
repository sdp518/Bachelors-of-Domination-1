package sepr.game.punishmentcards;


public class PlagueOfGeese extends Card {
    public PlagueOfGeese() {
        super(CardType.PLAGUE_OF_GEESE);
    }

    /**
     * Effect: Remove 3 units from all sectors of affected player
     */
    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
