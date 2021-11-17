package ua.com.radiokot.voda.features.card.view

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.di.InjectedCardReader
import ua.com.radiokot.voda.features.card.model.VodaCard
import ua.com.radiokot.voda.features.reader.logic.VodaCardReader
import ua.com.radiokot.voda.view.base.BaseActivity

class SingleCardReadActivity : BaseActivity(), VodaCardsSource {
    private val tagsSubject: PublishSubject<Tag> = PublishSubject.create()
    private val cardReader: VodaCardReader
            by inject(named(InjectedCardReader.REAL)) { parametersOf(tagsSubject) }

    private val cardsSubject: BehaviorSubject<VodaCard> = BehaviorSubject.create()
    override val cards: Observable<VodaCard> = cardsSubject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_card_read)

        initReader()

        if (savedInstanceState == null && NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val intentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag
            if (intentTag != null) {
                tagsSubject.onNext(intentTag)
            } else {
                finish()
            }
        } else {
            val savedCard = savedInstanceState?.getSerializable(CARD_DATA_STATE_KEY) as? VodaCard
            if (savedCard != null) {
                cardsSubject.onNext(savedCard)
            } else {
                finish()
            }
        }
    }

    private fun initReader() {
        cardReader
            .cards
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = cardsSubject::onNext,
                onError = { fatalError ->
                    fatalError.printStackTrace()
                    toastManager.short(
                        getString(
                            R.string.template_error_fatal_message,
                            fatalError.localizedMessage ?: fatalError.message
                        )
                    )
                    finish()
                }
            )
            .addTo(compositeDisposable)

        cardReader
            .errors
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { error ->
                error.printStackTrace()
                toastManager.short(R.string.error_failed_to_read_card_try_again)
                finish()
            }
            .addTo(compositeDisposable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(CARD_DATA_STATE_KEY, cardsSubject.value)
    }

    private companion object {
        private const val CARD_DATA_STATE_KEY = "card"
    }
}