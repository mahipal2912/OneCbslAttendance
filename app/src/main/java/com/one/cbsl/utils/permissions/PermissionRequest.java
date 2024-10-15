package com.one.cbsl.utils.permissions;

/**
 * Created by craterzone on 2/4/16.
 */
public interface PermissionRequest {

    interface RequestCamera extends BasePermissionsListener {
        void onCameraPermissionGranted();

        void onCameraPermissionDenied();
    }

    interface RequestContact extends BasePermissionsListener {
        void onContactPermissionGranted();

        void onContactPermissionDenied();
    }

    interface RequestLocation extends BasePermissionsListener {
        void onLocationPermissionGranted();

        void onLocationPermissionDenied();
    }

    interface RequestPhone extends BasePermissionsListener {
        void onPhonePermissionGranted();

        void onPhonePermissionDenied();
    }

    interface RequestSms extends BasePermissionsListener {
        void onSmsPermissionGranted();

        void onSmsPermissionDenied();
    }

    interface RequestCalender extends BasePermissionsListener {
        void onCalenderPermissionGranted();

        void onCalenderPermissionDenied();
    }

    interface RequestSensor extends BasePermissionsListener {
        void onSensorPermissionGranted();

        void onSensorPermissionDenied();
    }

    interface RequestMicrophone extends BasePermissionsListener {
        void onMicrophonePermissionGranted();

        void onMicrophonePermissionDenied();
    }

    interface RequestStorage extends BasePermissionsListener {
        void onStoragePermissionGranted();

        void onStoragePermissionDenied();
    }

    interface RequestCustomPermissionGroup extends BasePermissionsListener {
        void onAllCustomPermissionGroupGranted();
        void onCustomPermissionGroupDenied();
    }

    interface RequestCameraStoragePermissionGroup extends BasePermissionsListener {
        void onAllCameraStoragePermissionGroupGranted();
        void onCameraStoragePermissionGroupDenied();
    }
    interface RequestReadPhoneState extends BasePermissionsListener {
        void onAllCameraStoragePermissionGroupGranted();
        void onCameraStoragePermissionGroupDenied();
    }

}
