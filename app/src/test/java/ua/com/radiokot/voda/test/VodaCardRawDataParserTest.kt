package ua.com.radiokot.voda.test

import org.junit.Assert
import org.junit.Test
import ua.com.radiokot.voda.extensions.decodeHex
import ua.com.radiokot.voda.features.reader.logic.VodaCardRawDataParser
import ua.com.radiokot.voda.features.reader.model.VodaCard
import ua.com.radiokot.voda.features.reader.model.VodaCardRawData

class VodaCardRawDataParserTest {
    @Test
    fun parse() {
        val parser = VodaCardRawDataParser()

        Assert.assertEquals(
            VodaCard(
                balance = 13891
            ),
            parser.parse(
                VodaCardRawData(
                    balanceBlock = "43360000BCC9FFFF4336000063000000".decodeHex()
                )
            ).blockingGet()
        )

        Assert.assertEquals(
            VodaCard(
                balance = 14391
            ),
            parser.parse(
                VodaCardRawData(
                    balanceBlock = "37380000C8C7FFFF3738000064000000".decodeHex()
                )
            ).blockingGet()
        )

        Assert.assertEquals(
            VodaCard(
                balance = 14358
            ),
            parser.parse(
                VodaCardRawData(
                    balanceBlock = "16380000E9C7FFFF1638000065000000".decodeHex()
                )
            ).blockingGet()
        )
    }

    @Test(expected = IllegalStateException::class)
    fun parseDuplicatesMismatch() {
        VodaCardRawDataParser().parse(
            VodaCardRawData(
                balanceBlock = "37380000C8C7FFFF3738200064000000".decodeHex()
            )
        ).blockingGet()
    }

    @Test(expected = IllegalStateException::class)
    fun parseUnusedMismatch() {
        VodaCardRawDataParser().parse(
            VodaCardRawData(
                balanceBlock = "16380000E9C5FFFF1638000065000000".decodeHex()
            )
        ).blockingGet()
    }
}