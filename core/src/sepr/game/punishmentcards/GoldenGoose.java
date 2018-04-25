package sepr.game.punishmentcards;

public class GoldenGoose extends Card {
    public GoldenGoose() {
        super(CardType.GOLDEN_GOOSE);
    }

    @Override
    public void act() {
        System.out.println(type.getCardType());
    }
}
