package ua.compservice.util

import org.slf4j.LoggerFactory

inline fun <reified T: Any> loggerFor() = LoggerFactory.getLogger(T::class.java)