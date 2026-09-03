package com.pedro.ChamaKids.data

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * Banco de dados local do ChamaKids.
 *
 * Neste momento possui apenas a tabela de membros.
 *
 * Futuramente adicionaremos também:
 * - chamadas;
 * - presenças;
 * - demais entidades necessárias.
 */
@Database(
    entities = [
        MemberEntity::class,
        AttendanceEntity::class,
        AttendanceRecordEntity::class,
        StarRecordEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ChamaKidsDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun starDao(): StarDao

    companion object {

        /**
         * INSTANCE mantém uma única instância
         * do banco durante a execução do app.
         */
        @Volatile
        private var INSTANCE: ChamaKidsDatabase? = null


        /**
         * Retorna o banco existente ou cria
         * uma nova instância caso ainda não exista.
         */
        fun getDatabase(
            context: Context
        ): ChamaKidsDatabase {

            return INSTANCE
                ?: synchronized(this) {

                    val instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            ChamaKidsDatabase::class.java,
                            "chamakids_database"
                        )
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance

                    instance
                }
        }
    }
}