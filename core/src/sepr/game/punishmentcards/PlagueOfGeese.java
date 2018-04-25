package sepr.game.punishmentcards;


public class PlagueOfGeese extends Card {
    public PlagueOfGeese() {
        super(CardType.PLAGUE_OF_GEESE);
    }

    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
