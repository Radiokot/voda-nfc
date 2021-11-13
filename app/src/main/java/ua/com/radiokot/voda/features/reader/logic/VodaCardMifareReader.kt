package ua.com.radiokot.voda.features.reader.logic

import android.nfc.tech.MifareClassic
import io.reactivex.Single
import ua.com.radiokot.voda.features.reader.model.VodaCardRawData

class VodaCardMifareReader(
    private val cardKey: ByteArray
) {
    init {
        require(cardKey.size == 6) { "Card key must be 6 bytes long" }
    }

    fun read(mifare: MifareClassic): Single<VodaCardRawData> = Single.defer {
        mifare.use { mifare ->
            mifare.connect()

            val balanceBlock = readBlock(mifare, BALANCE_SECTOR, BALANCE_BLOCK)

            Single.just(
                VodaCardRawData(
                    balanceBlock = balanceBlock
                )
            )
        }
    }

    /**
     * Authenticates given [sector] and reads relative [block]
     *
     * @return block content
     */
    private fun readBlock(mifare: MifareClassic, sector: Int, block: Int): ByteArray {
        return mifare.run {
            authenticateSectorWithKeyB(sector, cardKey)
            readBlock(sectorToBlock(sector) + block)
        }
    }

    private companion object {
        private const val BALANCE_SECTOR = 9
        private const val BALANCE_BLOCK = 0
    }
}