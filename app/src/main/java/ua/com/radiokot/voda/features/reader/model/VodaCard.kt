package ua.com.radiokot.voda.features.reader.model

import java.math.BigDecimal

data class VodaCard(
    val balance: Long,
) {
    val balanceUah: BigDecimal = BigDecimal(balance).movePointLeft(2)

    fun getLiters(pricePerLiter: Int): Long = balance / pricePerLiter
}