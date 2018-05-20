package ua.compservice.util

import org.slf4j.LoggerFactory
import kotlin.math.roundToInt

inline fun <reified T: Any> loggerFor() = LoggerFactory.getLogger(T::class.java)

//fix the conversion numeric cell's value(for example: 11 converts to 11.0)
inline fun Double.toNormalizedString() = if ((Math.floor(this) - this) == 0.0) this.roundToInt().toString() else this.toString()

