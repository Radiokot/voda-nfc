package ua.com.radiokot.voda.util.format

import java.math.BigDecimal

interface AmountFormat {
    fun format(amount: BigDecimal): CharSequence
}