package ua.com.radiokot.voda.features.reader.logic

import android.nfc.tech.MifareClassic
import android.util.Log
import io.reactivex.Single
import ua.com.radiokot.voda.BuildConfig
import ua.com.radiokot.voda.features.reader.model.VodaCardRawData

class MifareVodaCardRawDataReader(
    private val cardKey: ByteArray
) {
    init {
        require(cardKey.size == 6) { "Card key must be 6 bytes long" }
    }

    fun read(mifare: MifareClassic): Single<VodaCardRawData> = Single.defer {
        mifare.use { mifare ->
            mifare.connect()

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "read: read_balance_block")
            }

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
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "readBlock: authenticate_and_read, sector = $sector" +
                        "\n block = $block")
            }
            authenticateSectorWithKeyB(sector, cardKey)
            readBlock(sectorToBlock(sector) + block)
        }
    }

    private companion object {
        private const val BALANCE_SECTOR = 9
        private const val BALANCE_BLOCK = 0
        private const val LOG_TAG = "MifareVCRawReader"
    }
}