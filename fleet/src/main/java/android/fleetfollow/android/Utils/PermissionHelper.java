package android.fleetfollow.android.Utils;

import android.app.Activity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PermissionHelper {

    public interface OnRequestResponseListner {
        void OnRequestError(String error);
        void OnRequestSuccess(String response);
    }


    private PermissionHelper.OnRequestResponseListner onRequestResponseListner;

    public void setCustomClickListener(PermissionHelper.OnRequestResponseListner onRequestResponseListner)
    {
        this.onRequestResponseListner = onRequestResponseListner;
    }

    public void AskPermission(Activity context, String permission, final PermissionHelper.OnRequestResponseListner onRequestResponseListner){
        setCustomClickListener(onRequestResponseListner);

        Dexter.withActivity(context)
                .withPermission(permission)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted, open the camera
                        if(onRequestResponseListner != null){
                            onRequestResponseListner.OnRequestSuccess(String.valueOf(response));
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(onRequestResponseListner != null){
                            onRequestResponseListner.OnRequestError(String.valueOf(response));
                        }
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }
}
