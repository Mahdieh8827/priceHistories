import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class Util {
    companion object {
        @JvmStatic
        fun getCurrentEpochSecond(): Long {
            return Instant.now().epochSecond
        }

        fun getEpochSecondByLastMinutes(minutes: Long): Long {
            val cal = Calendar.getInstance()
            cal[Calendar.SECOND] = 0
            return cal.time.toInstant().minus(minutes - 1, ChronoUnit.MINUTES).epochSecond
        }

        private fun getZonedDateTime(instant: Instant): ZonedDateTime {
            return instant.atZone(ZoneId.of("Europe/Berlin"))
        }

        fun getZoneTime(instant: Instant): String {
            return getZonedDateTime(instant).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        }

        fun getZoneDate(instant: Instant): String {
            return getZonedDateTime(instant).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

        fun getEpochSecondByLastMinutesForTest(minutes: Long): Long {
            return getCalenderTime(2022, 5, 15, 13, 45, 0).minus(minutes, ChronoUnit.MINUTES).epochSecond
        }

        fun getCalenderTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Instant {
            val date = Calendar.getInstance()
            date[Calendar.YEAR] = year
            date[Calendar.MONTH] = month - 1
            date[Calendar.DAY_OF_MONTH] = day
            date[Calendar.HOUR_OF_DAY] = hour
            date[Calendar.MINUTE] = minute
            date[Calendar.SECOND] = second

            return date.time.toInstant()
        }
    }
}