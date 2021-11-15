package ua.com.radiokot.voda.features.reader.model

import java.io.Serializable
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class VodaCard(
    val balance: BigDecimal
): Serializable {
    constructor(balanceCop: Long) : this(
        balance = BigDecimal(balanceCop).movePointLeft(2)
    )

    fun getLiters(pricePerLiter: BigDecimal) =
        balance.divide(pricePerLiter, MathContext.DECIMAL32)
            .setScale(2, RoundingMode.DOWN)
            .stripTrailingZeros()
}