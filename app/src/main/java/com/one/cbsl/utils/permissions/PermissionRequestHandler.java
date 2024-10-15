package com.one.cbsl.utils.permissions;

/**
 * Created by craterzone on 4/4/16.
 */
public class PermissionRequestHandler {

    /*
    * Camera request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_CAMERA_CODE)
    public static void requestCamera(Object object, String rationalString) {
        String[] perms = ConstantPermissions.CAMERA_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestCamera callback = (PermissionRequest.RequestCamera) object;
            callback.onCameraPermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_CAMERA_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_CAMERA_CODE)
    private static void requestCameraDenied(Object object) {
        PermissionRequest.RequestCamera callback = (PermissionRequest.RequestCamera) object;
        callback.onCameraPermissionDenied();
    }


    /*
    * Contact request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_CONTACTS_CODE)
    public static void requestContact(Object object, String rationalString) {
        String[] perms = ConstantPermissions.CONTACTS_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestContact callback = (PermissionRequest.RequestContact) object;
            callback.onContactPermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_CONTACTS_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_CONTACTS_CODE)
    private static void requestContactDenied(Object object) {
        PermissionRequest.RequestContact callback = (PermissionRequest.RequestContact) object;
        callback.onContactPermissionDenied();
    }

    /*
 * Storage request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_STORAGE_CODE)
    public static void requestStorage(Object object, String rationalString) {
        String[] perms = ConstantPermissions.STORAGE_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestStorage callback = (PermissionRequest.RequestStorage) object;
            callback.onStoragePermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_STORAGE_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_STORAGE_CODE)
    private static void requestStorageDenied(Object object) {
        PermissionRequest.RequestStorage callback = (PermissionRequest.RequestStorage) object;
        callback.onStoragePermissionDenied();
    }

    /*
* Phone request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_PHONE_CODE)
    public static void requestPhone(Object object, String rationalString) {
        String[] perms = ConstantPermissions.PHONE_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestPhone callback = (PermissionRequest.RequestPhone) object;
            callback.onPhonePermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_PHONE_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_PHONE_CODE)
    private static void requestPhoneDenied(Object object) {
        PermissionRequest.RequestPhone callback = (PermissionRequest.RequestPhone) object;
        callback.onPhonePermissionDenied();
    }

    /*
* Sensor request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_SENSORS_CODE)
    public static void requestSensor(Object object, String rationalString) {
        String[] perms = ConstantPermissions.SENSOR_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestSensor callback = (PermissionRequest.RequestSensor) object;
            callback.onSensorPermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_SENSORS_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_SENSORS_CODE)
    private static void requestSensorDenied(Object object) {
        PermissionRequest.RequestSensor callback = (PermissionRequest.RequestSensor) object;
        callback.onSensorPermissionDenied();
    }


    /*
* Location request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_LOCATION_CODE)
    public static void requestLocation(Object object, String rationalString) {
        String[] perms = ConstantPermissions.LOCATION_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestLocation callback = (PermissionRequest.RequestLocation) object;
            callback.onLocationPermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_LOCATION_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_LOCATION_CODE)
    private static void requestLocationDenied(Object object) {
        PermissionRequest.RequestLocation callback = (PermissionRequest.RequestLocation) object;
        callback.onLocationPermissionDenied();
    }


    /*
* SMS request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_SMS_CODE)
    public static void requestSMS(Object object, String rationalString) {
        String[] perms = ConstantPermissions.SMS_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestSms callback = (PermissionRequest.RequestSms) object;
            callback.onSmsPermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_SMS_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_SMS_CODE)
    private static void requestSMSDenied(Object object) {
        PermissionRequest.RequestSms callback = (PermissionRequest.RequestSms) object;
        callback.onSmsPermissionDenied();
    }


    /*
* Calender request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_CALENDER_CODE)
    public static void requestCalender(Object object, String rationalString) {
        String[] perms = ConstantPermissions.CALENDER_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestCalender callback = (PermissionRequest.RequestCalender) object;
            callback.onCalenderPermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_CALENDER_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_CALENDER_CODE)
    private static void requestCalenderDenied(Object object) {
        PermissionRequest.RequestCalender callback = (PermissionRequest.RequestCalender) object;
        callback.onCalenderPermissionDenied();
    }


    /*
* Microphone request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_MICROPHONE_CODE)
    public static void requestMicrophone(Object object, String rationalString) {
        String[] perms = ConstantPermissions.MICROPHONE_PERMISSION_STRING;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestMicrophone callback = (PermissionRequest.RequestMicrophone) object;
            callback.onMicrophonePermissionGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_MICROPHONE_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_MICROPHONE_CODE)
    private static void requestMicrophoneDenied(Object object) {
        PermissionRequest.RequestMicrophone callback = (PermissionRequest.RequestMicrophone) object;
        callback.onMicrophonePermissionDenied();
    }

    /*
* Custom permission group request*/
    @AfterPermissionGranted(ConstantPermissions.REQUEST_CUSTOM_PERMISSION_GROUP_CODE)
    public static void requestCustomPermissionGroup(Object object, String rationalString, String... perms) {
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestCustomPermissionGroup callback = (PermissionRequest.RequestCustomPermissionGroup) object;
            callback.onAllCustomPermissionGroupGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_CUSTOM_PERMISSION_GROUP_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_CUSTOM_PERMISSION_GROUP_CODE)
    private static void requestCustomPermissionGroupDenied(Object object) {
        PermissionRequest.RequestCustomPermissionGroup callback = (PermissionRequest.RequestCustomPermissionGroup) object;
        callback.onCustomPermissionGroupDenied();
    }
    @AfterPermissionGranted(ConstantPermissions.REQUEST_READ_PHONE_STATE)
    public static void requestReadPhoneState(Object object, String rationalString) {
        String[] perms = ConstantPermissions.READ_PHONE_STATE;
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestCustomPermissionGroup callback = (PermissionRequest.RequestCustomPermissionGroup) object;
            callback.onAllCustomPermissionGroupGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_READ_PHONE_STATE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_READ_PHONE_STATE)
    private static void requestReadPhoneStateDenied(Object object) {
        PermissionRequest.RequestCustomPermissionGroup callback = (PermissionRequest.RequestCustomPermissionGroup) object;
        callback.onCustomPermissionGroupDenied();
    }
    /*
    * Custom permission group request
    *
    * */
    @AfterPermissionGranted(ConstantPermissions.REQUEST_CAMERA_STORAGE_GROUP_CODE)
    public static void requestCameraStoragePermissionGroup(Object object, String rationalString, String... perms) {
        if (PermissionsUtil.hasPermission(PermissionsUtil.getActivity(object), perms)) {
            PermissionRequest.RequestCameraStoragePermissionGroup callback = (PermissionRequest.RequestCameraStoragePermissionGroup) object;
            callback.onAllCameraStoragePermissionGroupGranted();
        } else {
            PermissionsUtil.requestPermissions(object, rationalString,
                    ConstantPermissions.REQUEST_CAMERA_STORAGE_GROUP_CODE, perms);
        }
    }

    @AfterPermissionDenied(ConstantPermissions.REQUEST_CAMERA_STORAGE_GROUP_CODE)
    private static void requestCameraStoragePermissionGroupDenied(Object object) {
        PermissionRequest.RequestCameraStoragePermissionGroup callback = (PermissionRequest.RequestCameraStoragePermissionGroup) object;
        callback.onCameraStoragePermissionGroupDenied();
    }

}
