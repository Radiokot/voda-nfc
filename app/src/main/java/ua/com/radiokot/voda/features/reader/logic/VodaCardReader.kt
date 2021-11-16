package ua.com.radiokot.voda.features.reader.logic

import io.reactivex.Observable
import ua.com.radiokot.voda.features.reader.model.VodaCard

interface VodaCardReader {
    val cards: Observable<VodaCard>
}