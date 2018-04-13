package sepr.game.gangmembers;

public class GangMembers {
    private String name;
    private int movement;
    private double strengthMultiplier;
    private int pizzaCost;

    public GangMembers(String name, int movement, double strengthMultiplier, int pizzaCost) {
        this.name = name;
        this.movement = movement;
        this.strengthMultiplier = strengthMultiplier;
        this.pizzaCost = pizzaCost;
    }

    public String getName() {
        return this.name;
    }

    public int getMovement() {
        return this.movement;
    }

    public double getStrengthMultiplier() {
        return this.strengthMultiplier;
    }

    public int getPizzaCost() {
        return this.pizzaCost;
    }
}
