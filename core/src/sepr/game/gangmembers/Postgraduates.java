package sepr.game.gangmembers;

public class Postgraduates extends GangMembers {

    private Boolean attacked;

    public Postgraduates() {
        super("Postgraduate");

        this.attacked = false;
    }

    public Boolean getAttacked() {
        return this.attacked;
    }

    public void setAttacked(Boolean status) {
        this.attacked = status;
    }
}
