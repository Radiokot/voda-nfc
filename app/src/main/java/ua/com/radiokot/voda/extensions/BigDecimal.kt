package ua.com.radiokot.voda.extensions

import java.math.BigDecimal

val BigDecimal.isInteger: Boolean
    get() = signum() == 0 || scale() <= 0 || stripTrailingZeros().scale() <= 0