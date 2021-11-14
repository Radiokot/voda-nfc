package ua.com.radiokot.voda.features.reader.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_reader_card_data.*
import ua.com.radiokot.voda.BaseFragment
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.features.reader.model.VodaCardsSource
import java.math.BigDecimal

class ReaderCardDataFragment: BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reader_card_data, container, false)
    }

    override fun onInitAllowed() {
        ((parentFragment ?: activity) as VodaCardsSource)
            .cards
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { card ->
                liters_text_view.text = BigDecimal(card.getLiters(140)).toPlainString()
                liters_label_text_view.text = resources.getQuantityString(R.plurals.liters, (card.balance / 100).toInt())
            }
            .addTo(compositeDisposable)
    }
}