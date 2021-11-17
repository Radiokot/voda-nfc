package ua.com.radiokot.voda.features.reader.logic

import ua.com.radiokot.voda.extensions.encodeHex
import ua.com.radiokot.voda.features.card.model.VodaCard
import ua.com.radiokot.voda.features.reader.model.VodaCardRawData
import java.math.BigInteger

class VodaCardRawDataParser {
    fun parse(rawData: VodaCardRawData): VodaCard {
        val balanceCop = parseBalance(rawData.balanceBlock)


        return VodaCard(
            balanceCop = balanceCop
        )
    }

    private fun parseBalance(balanceBlock: ByteArray): Long {
        /*
        [0..3] uint32 balance
        [4..7] uint32 unused balance (4294967295 - balance)
        [8..11] uint32 balance
         */

        // Two duplicates.
        val balanceABytes = balanceBlock.sliceArray(0..3)
        val balanceBBytes = balanceBlock.sliceArray(8..11)

        check(balanceABytes.contentEquals(balanceBBytes)) { "Balance duplicates doesn't match ${balanceABytes.encodeHex()} ${balanceBBytes.encodeHex()}" }

        // UInt32 max - balance.
        val balanceUnusedBytes = balanceBlock.sliceArray(4..7)

        val actualBalance = BigInteger(1, balanceABytes.reversedArray())
            .toLong()

        val unusedBalance = BigInteger(1, balanceUnusedBytes.reversedArray())
            .toLong()

        check(actualBalance == 4294967295 - unusedBalance) { "Balance is not related to the unused one $actualBalance $unusedBalance" }

        return actualBalance
    }
}