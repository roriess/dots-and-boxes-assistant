import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import rules.ClassicRule
import rules.NoBonusRule
import rules.PenaltyRule

class RuleTest {

    @Test
    fun `calculateScore correct work`() {
        val rule = ClassicRule()
        assertEquals(0, rule.calculateScore(0))
        assertEquals(10, rule.calculateScore(10))
        assertEquals(1, rule.calculateScore(1))
    }
    @Test
    fun `classic rule isBonusTurn correct work`() {
        val rule = ClassicRule()
        assertTrue(rule.isBonusTurn())
    }
    @Test
    fun `penalty rule isBonusTurn correct work`() {
        val rule = PenaltyRule()
        assertFalse(rule.isBonusTurn())
    }

    @Test
    fun `no bonus rule isBonusTurn correct work`() {
        val rule = NoBonusRule()
        assertFalse(rule.isBonusTurn())
    }

}

