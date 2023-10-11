import Repository.Companion.add
import Repository.Companion.delete
import Repository.Companion.update
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun main() {
    println("starting up")

    val server = Server()
    val instrumentStream = InstrumentStream()
    val quoteStream = QuoteStream()

    instrumentStream.connect { event ->
        if (event.type == InstrumentEvent.Type.ADD) {
            add(event.data.isin)
        } else if (event.type == InstrumentEvent.Type.DELETE) {
            delete(event.data.isin)
        }
        println(event)
    }

    quoteStream.connect { event ->
        update(event.data.isin, event.data)
        println(event)
    }
    server.start()
}

val jackson: ObjectMapper =
    jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)