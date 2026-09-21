package com.pedro.ChamaKids

import platform.Foundation.NSUUID

actual object IdGenerator {
    actual fun generate(): String = NSUUID().UUIDString()
}
