package com.ramawidi.ghoresepmakanan.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.APP_DATASTORE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DATASTORE_PREFS_BACK_ONLINE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DATASTORE_PREFS_DIET_TYPE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DATASTORE_PREFS_DIET_TYPE_ID
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DATASTORE_PREFS_MEAL_TYPE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DATASTORE_PREFS_MEAL_TYPE_ID
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// Ini didefinisikan di luar kelas untuk menjalankan satu instansi saja (menghindari Exception).
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val selectedMealType = stringPreferencesKey(DATASTORE_PREFS_MEAL_TYPE)
    private val selectedMealTypeId = intPreferencesKey(DATASTORE_PREFS_MEAL_TYPE_ID)
    private val selectedDietType = stringPreferencesKey(DATASTORE_PREFS_DIET_TYPE)
    private val selectedDietTypeId = intPreferencesKey(DATASTORE_PREFS_DIET_TYPE_ID)
    private val backOnlineState = booleanPreferencesKey(DATASTORE_PREFS_BACK_ONLINE)

    suspend fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) {
        context.dataStore.edit {
                preferences ->
            preferences[selectedMealType] = mealType
            preferences[selectedMealTypeId] = mealTypeId
            preferences[selectedDietType] = dietType
            preferences[selectedDietTypeId] = dietTypeId
        }
    }

    val readMealAndDietType: Flow<MealAndDietType> = context.dataStore.data
        .catch {
                exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
        }.map {
                preferences ->
            // Jika tidak ada nilai yang disimpan untuk Key yang dipanggil, maka akan mengembalikan nilai default: "hidangan utama".
            val mealType = preferences[selectedMealType] ?: DEFAULT_MEAL_TYPE
            val mealTypeId = preferences[selectedMealTypeId] ?: 0
            val dietType = preferences[selectedDietType] ?: DEFAULT_DIET_TYPE
            val dietTypeId = preferences[selectedDietTypeId] ?: 0
            MealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }

    suspend fun saveOnlineState(backOnline: Boolean) {
        context.dataStore.edit {
                preferences ->
            preferences[backOnlineState] = backOnline
        }
    }

    val readBackOnlineState: Flow<Boolean> = context.dataStore.data
        .catch {
                exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
        }.map {
                preferences ->
            val backOnline = preferences[backOnlineState] ?: false
            backOnline
        }

}

data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)