package sepr.game.punishmentcards;

import sepr.game.DialogFactory;
import sepr.game.GameScreen;

public class PlagueOfGeese extends Card {
    public PlagueOfGeese(CardType type) {
        super(type);
    }

    @Override
    public void act() {
        System.out.println("YES");
    }
}
