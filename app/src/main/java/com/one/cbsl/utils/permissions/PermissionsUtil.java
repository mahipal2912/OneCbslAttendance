package com.one.cbsl.utils.permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by craterzone on 24/2/16.
 */
public class PermissionsUtil {

    private static final String TAG = PermissionsUtil.class.getSimpleName();

    public static boolean hasPermission(Context context, String... permissions) {
        for (String perm : permissions) {
            boolean hasPerm;
            try {
                hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            } catch (NullPointerException e) {
                throw new NullPointerException("Caller must does request permission with (YourActivityName.this or YourFragmentName.this) not only this");
            }
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }


    private static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public static void requestPermissions(final Object object, String rationale,
                                          final int requestCode, final String... perms) {
        requestPermissions(object, rationale,
                android.R.string.ok,
                android.R.string.cancel,
                requestCode, perms);
    }


    private static void requestPermissions(final Object object, String rationale,
                                           @StringRes int positiveButton,
                                           @StringRes int negativeButton,
                                           final int requestCode, final String... perms) {

        checkCallingObjectSuitability(object);

        if (rationale.equals("")) {
            executePermissionsRequest(object, perms, requestCode);
        } else {

            AlertDialog dialog = new AlertDialog.Builder(getActivity(object))
                    .setMessage(rationale)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executePermissionsRequest(object, perms, requestCode);
                        }
                    })
                    .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runAnnotatedMethodsWithDenied(object, requestCode);
                        }
                    }).create();
            dialog.show();
        }


   /*     boolean shouldShowRationale = false;
        for (String perm : perms) {
            shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
        }

        if (shouldShowRationale) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity(object))
                    .setMessage(rationale)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executePermissionsRequest(object, perms, requestCode);
                        }
                    })
                    .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runAnnotatedMethodsWithDenied(object, requestCode);
                        }
                    }).create();
            dialog.show();
        } else {
            executePermissionsRequest(object, perms, requestCode);
        }
*/
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }


    private static void executePermissionsRequest(@NonNull Object object, @NonNull String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);

        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof androidx.fragment.app.Fragment) {
            ((androidx.fragment.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof androidx.fragment.app.Fragment) {
            return ((androidx.fragment.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }


    private static void checkCallingObjectSuitability(Object object) {
        // Make sure Object is an Activity or Fragment
        if (!((object instanceof androidx.fragment.app.Fragment) || (object instanceof Activity))) {
            throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
        }

        // Make sure Object implements callbacks
        if (!(object instanceof BasePermissionsListener)) {
            throw new IllegalArgumentException("Caller must implement PermissionResult interface.");
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                  int[] grantResults, Object object) {

   /*     Log.d(TAG,"onRequestPermissionsResultPermissionUtil");
        PermissionRequest permissionRequestCallback = (PermissionRequest)object;
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();

       for (int i=0; i<permissions.length; i++){
           String perm = permissions[i];
           if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
               granted.add(perm);
           }else {
               denied.add(perm);
           }
       }*/

        boolean isGranted = verifyPermissions(grantResults);
        if (isGranted) {
            runAnnotatedMethods(object, requestCode, permissions);
        } else {
            runAnnotatedMethodsWithDenied(object, requestCode);
        }

      /*  if(!denied.isEmpty()){
            permissionRequestCallback.onPermissionDenied(requestCode,denied);
        }

        if(!granted.isEmpty() && denied.isEmpty()){
            runAnnotatedMethods(object,requestCode);
        }*/

    }

    private static void runAnnotatedMethods(Object object, int requestCode, String... perm) {
        Class objectClass = PermissionRequestHandler.class;
        for (Method method : objectClass.getMethods()) {
            if (method.isAnnotationPresent(AfterPermissionGranted.class)) {
                AfterPermissionGranted annotation = method.getAnnotation(AfterPermissionGranted.class);
                if (annotation.value() == requestCode) {
                    try {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        if (method.getParameterTypes().length < 2) {
                            throw new RuntimeException("Cannot execute non-void method " + method.getName());
                        } else if (method.getParameterTypes().length == 2) {
                            method.invoke(null, object, "Done");
                        } else {
                            method.invoke(null, object, "all permission granted", perm);
                        }

                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "runDefaultMethod : InvocationTargetException ", e);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "runDefaultMethod : IllegalAccessException ", e);
                    }
                }
            }
        }
    }

    private static void runAnnotatedMethodsWithDenied(Object object, int requestCode) {
        Class objectClass = PermissionRequestHandler.class;
        for (Method method : objectClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterPermissionDenied.class)) {
                AfterPermissionDenied annotation = method.getAnnotation(AfterPermissionDenied.class);
                if (annotation.value() == requestCode) {
                    if (method.getParameterTypes().length > 1) {
                        throw new RuntimeException("Cannot execute non-void method " + method.getName());
                    }
                    try {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(null, object);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "runDefaultMethod : InvocationTargetException ", e);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "runDefaultMethod : IllegalAccessException ", e);
                    }
                }
            }
        }
    }
}

