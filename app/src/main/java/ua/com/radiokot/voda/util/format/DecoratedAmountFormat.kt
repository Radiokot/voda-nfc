package ua.com.radiokot.voda.util.format

import java.math.BigDecimal

class DecoratedAmountFormat(
    private val source: NumberFormatAmountFormat,
    private val decorator: (CharSequence) -> CharSequence
) : AmountFormat {
    override fun format(amount: BigDecimal): CharSequence =
        decorator.invoke(source.format(amount))
}