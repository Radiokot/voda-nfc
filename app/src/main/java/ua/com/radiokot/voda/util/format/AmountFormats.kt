package ua.com.radiokot.voda.util.format

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@SuppressLint("ConstantLocale")
object AmountFormats {
    /**
     * Adds UAH sign, makes decimal part and sign smaller.
     *
     * @see SMALLER_RELATIVE_SIZE
     */
    val uah: AmountFormat by lazy {
        val numberFormat = DecimalFormat().apply {
            decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
            roundingMode = RoundingMode.DOWN
            maximumFractionDigits = 2
            minimumFractionDigits = 0
            positiveSuffix = " ₴"
            isGroupingUsed = true
        }

        DecoratedAmountFormat(
            NumberFormatAmountFormat(numberFormat)
        ) { source ->
            val decimalPart =
                getDecimalPart(source, numberFormat.decimalFormatSymbols.decimalSeparator)

            if (decimalPart != null) {
                getSuffixSizeDecorator(decimalPart)
                    .invoke(source)
            } else {
                getSuffixSizeDecorator(numberFormat.positiveSuffix)
                    .invoke(source)
            }
        }
    }

    /**
     * Makes decimal part and sign smaller.
     *
     * @see SMALLER_RELATIVE_SIZE
     */
    val default: AmountFormat by lazy {
        val numberFormat = DecimalFormat().apply {
            decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
            isGroupingUsed = true
        }

        DecoratedAmountFormat(
            NumberFormatAmountFormat(numberFormat),
        ) { source ->
            getDecimalPart(source, numberFormat.decimalFormatSymbols.decimalSeparator)
                ?.let(::getSuffixSizeDecorator)
                ?.invoke(source)
                ?: source
        }
    }

    private fun getDecimalPart(
        source: CharSequence,
        decimalSeparator: Char
    ): String? {
        val separatorPosition = source.lastIndexOf(decimalSeparator)
        return if (separatorPosition != -1) {
            source.substring(separatorPosition until source.length)
        } else {
            null
        }
    }

    private fun getSuffixSizeDecorator(suffix: String) = { source: CharSequence ->
        SpannableString(source).apply {
            val suffixPosition = source.lastIndexOf(suffix)
            if (suffixPosition >= 0) {
                setSpan(RelativeSizeSpan(SMALLER_RELATIVE_SIZE), suffixPosition, source.length, 0)
            }
        }
    }

    private const val SMALLER_RELATIVE_SIZE = 0.8f
}