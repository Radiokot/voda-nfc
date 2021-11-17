package ua.com.radiokot.voda.features.reader.logic

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ua.com.radiokot.voda.features.reader.model.VodaCard
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class DummyVodaCardReader: VodaCardReader {
    override val cards: Observable<VodaCard>
        get() = Observable.timer(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .map {
                VodaCard(
                    balance = BigDecimal("158.5")
                )
            }

    override val errors: Observable<Throwable> = Observable.empty()
}