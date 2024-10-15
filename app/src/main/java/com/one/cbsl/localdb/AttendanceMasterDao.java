package com.one.cbsl.localdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AttendanceMasterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void punchUserAttendance(AttendanceMaster data);

    @Query("SELECT * FROM ATTENDANCEMASTER WHERE PunchDate=:date")
    LiveData<List<AttendanceMaster>> getTodayPunchTime(String date);

    @Query("SELECT * FROM ATTENDANCEMASTER  where isSync=:type")
    LiveData<List<AttendanceMaster>> getAttendance(int type);

    @Query("update attendancemaster set isSync=1,status=:status,syncDate=:date where id=:id")
    void updateStatus(int id,String date,String status);

    @Query("update attendancemaster set PunchOut=:time,PunchOutLocationAddress=:outLocation where punchDate=:punchDate")
    void punchOutAttendance(String time,String punchDate,String outLocation);

    @Query("SELECT COUNT(*) FROM ATTENDANCEMASTER where punchDate=:punchDate")
    int getCount(String punchDate);
}
