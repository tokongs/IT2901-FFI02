package test.app.functions

import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class Calculator {

    fun avgDelay (timestamps: List<Pair<LocalDateTime, LocalDateTime>>): Double =
        timestamps.fold(Duration.ZERO) {acc, (a,b) -> acc + Duration.between(a,b)}
            .toNanos() / (timestamps.size.toDouble())

    fun maxDelay (timestamps: List<Pair<LocalDateTime, LocalDateTime>>): Long =
        timestamps.map { (m1,m2) -> Duration.between(m1,m2) }.maxOf { it }.toNanos()


    fun minDelay (timestamps: List<Pair<LocalDateTime, LocalDateTime>>): Long =
        timestamps.map { (m1,m2) -> Duration.between(m1,m2) }.minOf { it }.toNanos()

    // Finds average delay as compared to a global minimum for the testsuite(estimated to be undelayed)
    fun normalizedDelay (timestamps: List<Pair<LocalDateTime, LocalDateTime>>, optimal: Long): Double {
        val standardizedDelays = timestamps.map {(a,b) -> Duration.between(a,b).toNanos() - optimal}
        // WTF?
        return standardizedDelays.fold(0L) { acc, x -> acc + x} / standardizedDelays.size.toDouble()
    }
    fun delayStddev (delays: List<Pair<LocalDateTime, LocalDateTime>>): Double {
        return 0.0
    }

    // Find global minima of testsuite, to be considered undelayed. Better yet might be just to
    // Run a few max priority messages at low priority to approximate it.
    fun findOptimal (topics: ConcurrentHashMap<String, List<Pair<LocalDateTime, LocalDateTime>>>): Long =
        topics.values.map{ this.minDelay(it) }.minOf { it }
}