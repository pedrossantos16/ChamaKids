package com.pedro.ChamaKids.data

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object DatabaseProvider {
    private var instance: ChamaKidsDatabase? = null

    fun initialize(database: ChamaKidsDatabase) {
        instance = database
    }

    fun getDatabase(): ChamaKidsDatabase {
        return instance ?: throw IllegalStateException("Database not initialized")
    }
}
