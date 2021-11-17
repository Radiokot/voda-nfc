package ua.com.radiokot.voda.features.card.storage

import android.content.SharedPreferences
import ua.com.radiokot.voda.util.BigDecimalUtil
import java.math.BigDecimal

class CardPreferencesImpl(
    private val sharedPreferences: SharedPreferences
) : CardPreferences {
    override var literPrice: BigDecimal
        get() =
            sharedPreferences
                .getString(LITER_PRICE_KEY, "1.4")
                .let(BigDecimalUtil::valueOf)
        set(value) {
            sharedPreferences
                .edit()
                .putString(LITER_PRICE_KEY, value.toPlainString())
                .apply()
        }

    private companion object {
        private const val LITER_PRICE_KEY = "liter_price"
    }
}