package sepr.game.punishmentcards;

public class GoldenGoose extends Card {
    public GoldenGoose() {
        super(CardType.GOLDEN_GOOSE);
    }

    /**
     * Effect: Plays goose sounds for the duration of the next turn of the affected player
     */
    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
