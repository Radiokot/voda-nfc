package ua.com.radiokot.voda.features.nfc.logic

import android.nfc.Tag
import io.reactivex.Observable

interface NfcReader {
    fun start()

    fun stop()

    val isActive: Boolean

    val tags: Observable<Tag>
}