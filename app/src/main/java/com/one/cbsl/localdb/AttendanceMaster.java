package com.one.cbsl.localdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AttendanceMaster {
    @PrimaryKey(autoGenerate = true)
     public int id ;
     public String Userid;
     public String AttendanceTypeId;
     public String PunchIn;
     public String PunchDate;
     public String PunchOut="";
     public String LocationAddress;
     public String CreatedOn;
     public String PunchOutLocationAddress="";
     public String Latitude;
     public String Longitude;
     public String status;
     public int isSync;
     public String syncDate;

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getUserid() {
        return Userid;
    }

    public String getAttendanceTypeId() {
        return AttendanceTypeId;
    }

    public String getPunchIn() {
        return PunchIn;
    }

    public String getPunchDate() {
        return PunchDate;
    }

    public String getPunchOut() {
        return PunchOut;
    }

    public String getLocationAddress() {
        return LocationAddress;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public String getPunchOutLocationAddress() {
        return PunchOutLocationAddress;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public int getIsSync() {
        return isSync;
    }

    public String getSyncDate() {
        return syncDate;
    }
}
