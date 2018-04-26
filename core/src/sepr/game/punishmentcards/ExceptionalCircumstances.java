package sepr.game.punishmentcards;

public class ExceptionalCircumstances extends Card {
    public ExceptionalCircumstances() {
        super(CardType.EXCEPTIONAL_CIRCUMSTANCES);
    }

    /**
     * Effect: Swap sectors with chosen player
     */
    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
