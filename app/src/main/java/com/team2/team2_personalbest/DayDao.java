package com.team2.team2_personalbest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Observable;

import java.util.List;
/**
 * This class is used for database
 */
@Dao
public interface DayDao {

    @Insert
    void insertSingleDay(Day day);

    @Insert
    void insertMultipleDays(List<Day> dayList);

    @Query("SELECT * FROM Day WHERE dayId = (:dayId)")
    Day getDayById(String dayId);

    @Query("SELECT * FROM Day WHERE dayId=(:dayId)")
    LiveData<Day> getLiveDay(String dayId);

    @Query("DELETE FROM Day")
    void deleteDay();

    @Query("SELECT * FROM Day")
    List<Day> getAllDays();

    @Update
    void updateDay(Day day);

    /*@Query("SELECT * from day_step_table ORDER BY day ASC")
    List<Day> getAllWords(); */


}
