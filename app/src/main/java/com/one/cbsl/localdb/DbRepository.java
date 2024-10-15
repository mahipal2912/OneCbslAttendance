package com.one.cbsl.localdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Room;


import com.one.cbsl.utils.Constants;
import com.one.cbsl.utils.SessionManager;
import com.one.cbsl.utils.Utils;

import org.jetbrains.annotations.NotNull;


@SuppressLint("StaticFieldLeak")
public class DbRepository {

    private static DbRepository mInstance;
    Context context;
    private final String DBNAME = "crc_attendance.db";
    private final OneCbslDatabase crcDataBase;


    public DbRepository(Context context) {
        this.context = context;
        crcDataBase = Room.databaseBuilder(context, OneCbslDatabase.class, DBNAME).build();
    }

    public static synchronized DbRepository getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DbRepository(mCtx);
        }
        return mInstance;
    }

    public OneCbslDatabase getAppDatabase() {
        return crcDataBase;
    }


    //Validation

    public void punchAttendance(final String typeid, final String todayDate, final String currentTime, final String location, final String lat, final String lng, final int sync) {
        new AsyncTaskLoader<Integer>(context) {
            @Override
            public Integer loadInBackground() {
                int isExist = getCount(todayDate);
                if (isExist == 0) {

                    AttendanceMaster data = new AttendanceMaster();
                    data.AttendanceTypeId = typeid;
                    data.Userid = SessionManager.getInstance().getString(Constants.UserId);
                    data.PunchDate = todayDate;
                    data.PunchIn = currentTime;
                    data.LocationAddress = location;
                    data.Latitude = lat;
                    data.CreatedOn = Utils.getCurrentDateTime();
                    data.isSync = sync;
                    data.Longitude = lng;
                    data.PunchOut = "";
                    data.status = "Pending";
                    data.PunchOutLocationAddress = "";
                    crcDataBase.crcDao().punchUserAttendance(data);
                    return 1;
                } else {
                    return 0;
                }

            }

            @Override
            public void deliverResult(@Nullable Integer data) {
                super.deliverResult(data);
                if (data == 0) {
                    showToast("Already PunchIn !!");
                } else {
                    showToast("Punch In Successfully");
                }
            }

        }.forceLoad();
    }

    public void punchOut(final String punchDate, final String outTime, final String outLocation) {
        new AsyncTaskLoader<Integer>(context) {
            @NotNull
            @Override
            public Integer loadInBackground() {
                int isExist = getCount(punchDate);
                if (isExist > 0) {
                    crcDataBase.crcDao().punchOutAttendance(outTime, punchDate, outLocation);
                    return 1;
                } else {
                    return 0;
                }
            }

            @Override
            public void deliverResult(@Nullable Integer data) {
                super.deliverResult(data);
                if (data == 0) {
                    showToast("Not PunchIn Today");
                } else {
                    showToast("Punch Out Successfully");
                }
            }
        }.forceLoad();
    }


    private int getCount(String punchDate) {
        return crcDataBase.crcDao().getCount(punchDate);
    }

    private void showToast(String s) {
        final Toast toast = Toast.makeText(
                context,
                "" + s,
                Toast.LENGTH_SHORT
        );
        toast.show();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);
    }

}



