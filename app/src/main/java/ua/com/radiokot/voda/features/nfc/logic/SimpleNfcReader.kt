package ua.com.radiokot.voda.features.nfc.logic

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Handler
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ua.com.radiokot.voda.BuildConfig

class SimpleNfcReader(
    private val activity: Activity
) : NfcReader {
    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private val tagsSubject = PublishSubject.create<Tag>()
    private val timeoutHandler = Handler(activity.mainLooper)

    override var isActive: Boolean = false
        private set

    override val tags: Observable<Tag> = tagsSubject

    private var acceptTags: Boolean = true

    override fun start() {
        if (BuildConfig.DEBUG)
            Log.d(
                LOG_TAG, "start:" +
                        "\nactivity = $activity" +
                        "\ndefault_nfcAdapter = $nfcAdapter" +
                        "\nBuild.VERSION.SDK_INT = ${Build.VERSION.SDK_INT}"
            )

        if (nfcAdapter == null || !nfcAdapter.isEnabled) {
            return
        }
        if (BuildConfig.DEBUG)
            Log.d(
                LOG_TAG,
                "start: call_enableReaderMode(), nfcAdapter_isEnabled = ${nfcAdapter.isEnabled}"
            )

        isActive = true
        nfcAdapter.disableForegroundDispatch(activity)
        nfcAdapter.enableReaderMode(
            activity,
            this::onTagDiscovered,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                    or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
            null
        )
    }

    // Not call on some devices, as result not work nfc
    private fun onTagDiscovered(tag: Tag) {
        if (BuildConfig.DEBUG)
            Log.d(
                LOG_TAG, "onTagDiscovered: callback_start" +
                        "\ntag = $tag" +
                        "\ntechList = ${tag.techList.joinToString()}"
            )

        if (acceptTags) {
            timeoutHandler.postDelayed({ acceptTags = true }, TIMEOUT_MS)
            acceptTags = false

            tagsSubject.onNext(tag)
        }
    }

    override fun stop() {
        if (BuildConfig.DEBUG)
            Log.d(LOG_TAG, "stop")

        if (nfcAdapter == null || !nfcAdapter.isEnabled) {
            return
        }

        if (BuildConfig.DEBUG)
            Log.d(LOG_TAG, "stop: call_disableReaderMode()")

        isActive = false
        nfcAdapter.disableReaderMode(activity)
    }

    companion object {
        private const val LOG_TAG = "SimpleNfcReader"
        private const val TIMEOUT_MS = 1000L
    }
}