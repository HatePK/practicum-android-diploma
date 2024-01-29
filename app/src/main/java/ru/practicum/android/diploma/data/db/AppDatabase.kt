package ru.practicum.android.diploma.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.android.diploma.data.db.dao.VacancyDao
import ru.practicum.android.diploma.data.db.entity.VacancyDescriptionEntity

@Database(entities = [VacancyDescriptionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vacancyDao(): VacancyDao
}
