package sepr.game.gangmembers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GangMemebersTest {
    private GangMembers gangMembers;
    private Undergraduates undergraduates;
    private Postgraduates postgraduates;

    @Before
    public void setUp() {
        this.gangMembers = new GangMembers("test");
        this.undergraduates = new Undergraduates();
        this.postgraduates = new Postgraduates();
    }

    @Test
    public void testGangMemebers() {
        assertEquals("test", this.gangMembers.getName());
    }

    @Test
    public void testUndergraduates() {
        assertEquals("Undergraduate", this.undergraduates.getName());
    }

    @Test
    public void testPostgraduates() {
        assertEquals("Postgraduate", this.postgraduates.getName());
        assertEquals(false, this.postgraduates.getAttacked());
        this.postgraduates.setAttacked(true);
        assertEquals(true, this.postgraduates.getAttacked());
    }
}
