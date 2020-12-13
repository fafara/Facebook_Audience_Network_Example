package ng.rafafas.facebookaudienceexample;

import android.content.Context;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.Toast;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class AudienceNetworkInitializeHelper implements AudienceNetworkAds.InitListener {

    static void initialize(Context context){
        if(!AudienceNetworkAds.isInitialized(context)){
            if(AdSettings.DEBUG){
                AdSettings.turnOnSDKDebugger(context);
            }
           AudienceNetworkAds
                   .buildInitSettings(context)
                   .withInitListener(new AudienceNetworkInitializeHelper())
                   .initialize();
        }
    }
    @Override
    public void onInitialized(AudienceNetworkAds.InitResult initResult) {
        Log.d("Initialising fb","Initialising Facebook Audience");
    }
}
