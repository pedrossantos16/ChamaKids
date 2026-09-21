package com.pedro.ChamaKids

import java.util.UUID

actual object IdGenerator {
    actual fun generate(): String = UUID.randomUUID().toString()
}
