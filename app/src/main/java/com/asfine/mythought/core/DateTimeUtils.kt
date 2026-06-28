package com.asfine.mythought.core

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

private val jakartaZoneId = ZoneId.of("Asia/Jakarta")

fun String.toJakartaDateTimeOrNull(): ZonedDateTime? {
    return runCatching {
        OffsetDateTime.parse(this).atZoneSameInstant(jakartaZoneId)
    }.getOrNull()
}
