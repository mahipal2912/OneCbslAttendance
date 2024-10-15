package com.one.cbsl.utils.permissions;

import android.Manifest;

/**
 * Created by craterzone on 24/2/16.
 */
public interface ConstantPermissions {

    /*permission-group-request-code*/
    int REQUEST_CAMERA_CODE = 100;
    int REQUEST_CONTACTS_CODE = 101;
    int REQUEST_PHONE_CODE = 102;

    int REQUEST_LOCATION_CODE = 103;
    int REQUEST_SMS_CODE = 104;
    int REQUEST_SENSORS_CODE = 105;
    int REQUEST_MICROPHONE_CODE = 106;
    int REQUEST_STORAGE_CODE = 107;
    int REQUEST_CALENDER_CODE = 108;
    int REQUEST_CUSTOM_PERMISSION_GROUP_CODE = 109;
    int REQUEST_CAMERA_STORAGE_GROUP_CODE = 110;
    int REQUEST_READ_PHONE_STATE = 111;


    /*permission-group-request-string*/
    String[] CONTACTS_PERMISSION_STRING = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS};
    String[] STORAGE_PERMISSION_STRING = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] CALENDER_PERMISSION_STRING = {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
    String[] SMS_PERMISSION_STRING = {Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_WAP_PUSH};
    String[] MICROPHONE_PERMISSION_STRING = {Manifest.permission.RECORD_AUDIO};
    String[] SENSOR_PERMISSION_STRING = {Manifest.permission.BODY_SENSORS, Manifest.permission.USE_FINGERPRINT};
    String[] PHONE_PERMISSION_STRING1 = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.CALL_PHONE, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS};
    String[] PHONE_PERMISSION_STRING = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
    String[] LOCATION_PERMISSION_STRING = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    String[] CAMERA_PERMISSION_STRING = {Manifest.permission.CAMERA};
    String[] READ_PHONE_STATE = {Manifest.permission.READ_PHONE_STATE};
    String[] CAMERA_PERMISSION_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
}
