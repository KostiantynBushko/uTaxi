package com.onquantum.utaxi;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onquantum.utaxi.common.Constant;
import com.onquantum.utaxi.services.TrackingService;
import com.onquantum.utaxi.wizard.AbstractFragmentWizard;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends Activity implements FragmentsCommonInterface,AbstractFragmentWizard.WizardCommonInterface, View.OnTouchListener{

    private TelephonyManager telephonyManager;
    private Context context;

    private BroadcastReceiver locationBroadcastReceiver;
    private FragmentTransaction fragmentTransaction;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private Fragment homeFragment;
    private TextView textLogo;
    private TextView textLogoInfo;

    private boolean isTouched;
    private float touchX, touchY;
    private float move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/philosopher_bold.ttf");
        int titleId = getResources().getIdentifier("action_bar_title", "id","android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(font);

        setContentView(R.layout.main);
        context = this;

        ((FrameLayout)findViewById(R.id.fragment)).setOnTouchListener(this);

        homeFragment = new FragmentHome();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, homeFragment);
        fragmentTransaction.addToBackStack(FragmentHome.class.getName());
        fragmentTransaction.commit();

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        textLogo = (TextView)findViewById(R.id.textView);
        textLogo.setTypeface(font);
        SpannableString text = new SpannableString(getResources().getString(R.string.text_logo));
        text.setSpan(new ForegroundColorSpan(Color.RED),0,1,0);
        textLogo.setText(text);
        textLogo.setAnimation(animation);

        textLogoInfo = (TextView)findViewById(R.id.textView1);
        textLogoInfo.setTypeface(font);
        textLogoInfo.setAnimation(animation);

        fragmentManager = getFragmentManager();
        //startService(new Intent(this, TrackingService.class));
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            this.finish();
        } else {
            if (textLogo.getVisibility() == View.VISIBLE) {
                Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
                textLogo.setAnimation(animation);
                textLogoInfo.setAnimation(animation);
            }
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onCloseFragment(Object object) {
        //Log.i("info","FragmentCommonInterface onCloseFragment");
        textLogo.setVisibility(View.INVISIBLE);
        textLogoInfo.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_out);
        textLogo.setAnimation(animation);
        textLogoInfo.setAnimation(animation);
    }

    @Override
    public void onRunFragment() {
        //Log.i("info","FragmentCommonInterface onRunFragment");
        textLogo.setVisibility(View.VISIBLE);
        textLogoInfo.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        textLogo.setAnimation(animation);
        textLogoInfo.setAnimation(animation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMask = event.getActionMasked();
        switch (actionMask) {
            case MotionEvent.ACTION_DOWN:
                isTouched = true;
                touchX = event.getX();
                touchY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                isTouched = false;
                touchX = 0.0f;
                touchY = 0.0f;
                break;
            case MotionEvent.ACTION_MOVE:
                touchX += event.getX();
                touchY += event.getY();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void OnStartExecuteWizard() {
        Log.i("info","OnStartExecuteWizard");
    }

    @Override
    public void OnSwitchStep(int step) {
    }

    @Override
    public void OnCloseWizard() {
        //Log.i("info","OnCloseWizard");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        currentFragment = fragment;
    }

    @Override
    public void onDestroy() {
        //stopService(new Intent(this, TrackingService.class));
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constant.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList matches = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0) {
                Toast.makeText(this, matches.get(0).toString(), Toast.LENGTH_LONG).show();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment);
                if (fragment != null && fragment instanceof AbstractFragmentWizard) {
                    ((AbstractFragmentWizard)fragment).onRecognizer(matches.get(0).toString());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
