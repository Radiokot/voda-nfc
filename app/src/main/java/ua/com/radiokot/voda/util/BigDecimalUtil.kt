package ua.com.radiokot.voda.util

import java.math.BigDecimal

object BigDecimalUtil {
    /**
     * Allows to safely parse BigDecimal from String with default value (0 by default)
     * @return parsed value or [defaultValue] if it can't be parsed
     */
    fun valueOf(stringValue: String?, defaultValue: BigDecimal = BigDecimal.ZERO): BigDecimal {
        return try {
            if (stringValue.isNullOrBlank())
                defaultValue
            else
                BigDecimal(stringValue)
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }
}