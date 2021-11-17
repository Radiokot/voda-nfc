package ua.com.radiokot.voda.features.reader.logic

import android.content.res.XmlResourceParser

/**
 * Parses tech-list resource XML according to the ACTION_TECH_DISCOVERED specification.
 *
 * @see <a href="https://developer.android.com/guide/topics/connectivity/nfc/nfc#tech-disc">ACTION_TECH_DISCOVERED</a>
 */
class NfcTechListsXmlParser {
    fun parse(resParser: XmlResourceParser): List<List<String>> {
        val techSets = mutableListOf<List<String>>()
        var currentTechSet = mutableListOf<String>()

        resParser.next()
        check(resParser.eventType == XmlResourceParser.START_DOCUMENT)
        resParser.next()
        check(resParser.name == "resources")
        resParser.next()

        while (resParser.eventType != XmlResourceParser.END_DOCUMENT) {
            when (resParser.eventType) {
                XmlResourceParser.START_TAG ->
                    when (resParser.name) {
                        "tech-list" -> {
                            currentTechSet = mutableListOf()
                        }
                        "tech" -> {
                            resParser.next()
                            check(resParser.eventType == XmlResourceParser.TEXT)
                            currentTechSet.add(resParser.text)
                        }
                        else -> throw IllegalStateException(resParser.name)
                    }
                XmlResourceParser.END_TAG ->
                    when (resParser.name) {
                        "tech-list" -> {
                            techSets.add(currentTechSet)
                        }
                    }
            }

            resParser.next()
        }

        return techSets
    }
}