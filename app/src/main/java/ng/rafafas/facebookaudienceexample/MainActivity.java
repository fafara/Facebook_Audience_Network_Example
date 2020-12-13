package ng.rafafas.facebookaudienceexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.*;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tv_facebook;
    AdView adView; // facebook banner ads
    InterstitialAd interstitialAd; //facebook interstitial ads
    NativeAd nativeAd; //facebook nativeAd
    NativeAdLayout nativeAdLayout;
    LinearLayout linearLayoutAdView;
    Button btn_int_ads;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialising Facebook Audience SDK
        AudienceNetworkInitializeHelper.initialize(this);

        //banner ads for facebook
        adView = new AdView(this,
                "IMG_16_9_APP_INSTALL#YOUR_FACEBOOK_PLACEHOLDER_ID", AdSize.BANNER_HEIGHT_90); //adview banner
        //interstital ads for facebook
        interstitialAd = new InterstitialAd(this, "IMG_16_9_APP_INSTALL#YOUR_FACEBOOK_PLACEHOLDER_ID");


        // finding the views in layout
        tv_facebook = findViewById(R.id.tv_facebook);
        btn_int_ads = findViewById(R.id.interstitial_ad);

        btn_int_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                } else {
                    showToast("Interstitial ads not loaded yet");
                    interstitialAd.loadAd();
                }
            }
        });


        //find the ad container
        LinearLayout adContainer = findViewById(R.id.banner_container);

        //Add the adview to the activity container
        adContainer.addView(adView);

        //request an ad Load
        adView.loadAd();

        tv_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //All Ads Listeners methods
        bannerAdListener();
        interstitialAdListener();
        nativeAdListener();
    }

    public void bannerAdListener() {
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Toast.makeText(
                        MainActivity.this,
                        "Error" + adError.getErrorMessage(),
                        Toast.LENGTH_LONG)
                        .show();

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    public void interstitialAdListener() {
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

                showToast("ad Dismissed");
                interstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                showToast("Ad Error " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

                showToast("Ad is loaded you can now click the button");
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                showToast("logging impression ");
            }
        };

        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build());
    }

    public void nativeAdListener() {

        //nativead for facebook
        nativeAd = new NativeAd(this, "IMG_16_9_APP_INSTALL#YOUR_FACEBOOK_PLACEHOLDER_ID");

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                showToast("media is loaded.");
                if(nativeAd == null || nativeAd != ad){
                    return;
                }
                inflateAd(nativeAd);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                showToast("NativeAd Error " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                showToast("Native ad is loaded and will display soon");
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }

               mHandler.post(new Runnable() {
                   @Override
                   public void run() {

                       inflateAd(nativeAd);

                       View view = findViewById(android.R.id.content);
                       Snackbar.make(
                               view,
                               "NativeAd is loaded"
                               ,Snackbar.LENGTH_LONG)
                               .show();
                   }
               });

                //showNativeAdWithDelay();

                nativeAd.downloadMedia();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        //request native ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build()
        );

        //method to delay native ads by 1 min or more
        showNativeAdWithDelay();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    public void showToast(String message) {
        Toast.makeText(MainActivity.this,
                message,
                Toast.LENGTH_LONG).show();
    }

    // Inflating the native ads
    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();

        //Adding the Ad views inside the native ad container
        nativeAdLayout = findViewById(R.id.native_ad_container);

        LayoutInflater inflater = LayoutInflater.from(this);
        //inflate the Ad View
        linearLayoutAdView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(linearLayoutAdView);

        //Add the Ad Options View
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        //crating native UI using the metadata
        MediaView nativeAdIcon = linearLayoutAdView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = linearLayoutAdView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = linearLayoutAdView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = linearLayoutAdView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = linearLayoutAdView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = linearLayoutAdView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = linearLayoutAdView.findViewById(R.id.native_ad_call_to_action);

        //Assigning all the text values from all facebook advertisers
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);

        //Create a list of clickable views and calling to action once users clicked
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        //Enabling and registering the title and CTA button to listen to clicks
        nativeAd.registerViewForInteraction(
                linearLayoutAdView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);


    }

    private void showNativeAdWithDelay(){
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(nativeAd == null || !nativeAd.isAdLoaded()){
                    return;
                }
                if(nativeAd.isAdInvalidated()){
                    return;
                }
                inflateAd(nativeAd);
            }
        }, 1000 * 60 * 1);
    }
}