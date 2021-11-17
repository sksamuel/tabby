package com.sksamuel.tabby.core

import java.time.Instant

fun Long.fromEpochMillis(): Instant = Instant.ofEpochMilli(this)
