package ua.com.radiokot.voda.util.format

import java.math.BigDecimal
import java.text.NumberFormat

open class NumberFormatAmountFormat(private val format: NumberFormat) : AmountFormat {
    override fun format(amount: BigDecimal): CharSequence = format.format(amount)
}