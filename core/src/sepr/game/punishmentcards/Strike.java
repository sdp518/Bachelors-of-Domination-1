package sepr.game.punishmentcards;

public class Strike extends Card {
    public Strike() {
        super(CardType.STRIKE);
    }

    /**
     * Effect: Steal PVC sector from player that currently holds it
     */
    public void act() {
        System.out.println(type.getCardType());
    }
}
