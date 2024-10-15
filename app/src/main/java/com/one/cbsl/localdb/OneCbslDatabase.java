package com.one.cbsl.localdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AttendanceMaster.class}, version = 1)
public abstract class OneCbslDatabase extends RoomDatabase {
    public abstract AttendanceMasterDao crcDao();
}
