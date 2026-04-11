package io.ejekta.bountiful

import io.ejekta.bountiful.content.BountyCreator
import kotlin.test.Test
import kotlin.test.assertTrue

class BountifulLogicTest {

    @Test
    fun discountGetsBetterWithMoreReputation() {
        val lowRep = BountyCreator.getDiscount(-30)
        val neutralRep = BountyCreator.getDiscount(0)
        val highRep = BountyCreator.getDiscount(30)
        val lateGameRep = BountyCreator.getDiscount(120)

        assertTrue(lowRep > neutralRep, "Negative reputation should make bounties less favorable")
        assertTrue(neutralRep > highRep, "Positive reputation should improve the discount")
        assertTrue(highRep > lateGameRep, "High-tier reputation should continue improving the discount")
    }

    @Test
    fun discountStaysWithinReasonableBounds() {
        val worstCase = BountyCreator.getDiscount(-30)
        val bestCase = BountyCreator.getDiscount(500)

        assertTrue(worstCase > 1.0, "Very low reputation should make objectives harder than baseline")
        assertTrue(bestCase > 0.0, "Discount should never invert or go negative")
        assertTrue(bestCase < 0.5, "Very high reputation should meaningfully reduce required filler worth")
    }
}
