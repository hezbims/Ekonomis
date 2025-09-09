package com.hezapp.ekonomis.test_utils.rule

import com.hezapp.ekonomis.test_utils.TestTimeService
import org.junit.rules.ExternalResource
import java.util.Locale
import java.util.TimeZone

class GlobalTimeConfigRule(
    private val timeZoneId: String = "GMT+8",
    private val languageTag: String = "id-ID",
) : ExternalResource() {
    override fun before() {
        val locale = Locale.forLanguageTag(languageTag) // Indonesia
        Locale.setDefault(locale)

        val timeZone = TimeZone.getTimeZone(timeZoneId)
        TimeZone.setDefault(timeZone)
        TestTimeService.setTestTimeZone(timeZone)

        super.before()
    }
}