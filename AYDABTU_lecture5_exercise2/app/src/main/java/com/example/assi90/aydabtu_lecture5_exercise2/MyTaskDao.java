package com.example.assi90.aydabtu_lecture5_exercise2;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.assi90.aydabtu_lecture5_exercise2.models.Task;

import java.util.List;

@Dao
public interface MyTaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("Select * from task where uid in (:ids)")
    List<Task> loadAllByIds(int[] ids);

    @Query("Select * from task where name like :name and "
            + "place like :place limit 1")
    Task findByNameAndPlace(String name, String place);

    // the tripple dot annotation seems to be convention,
    // for methods expecting list or arrays as arguments
    @Insert
    void insertAll(Task... task);

    @Delete
    void delete(Task task);
}
