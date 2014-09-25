package com.onquantum.utaxi;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.onquantum.utaxi.wizard.AbstractFragmentWizard;


public class MainActivity extends Activity implements FragmentsCommonInterface, View.OnTouchListener{

    private TelephonyManager telephonyManager;
    private Context context;

    private FragmentTransaction fragmentTransaction;
    private Fragment homeFragment;
    private TextView textLogo;

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


    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            this.finish();
        } else {
            Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
            textLogo.setAnimation(animation);
            /*if (AbstractFragmentWizard.currentWizardStep > 1) {
                while (AbstractFragmentWizard.currentWizardStep > 1){
                    getFragmentManager().popBackStack();
                    AbstractFragmentWizard.currentWizardStep -= 1;
                }
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.move_in_down, R.anim.move_out_down);
            }*/
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onCloseFragment() {
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        textLogo.setAnimation(animation);
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
                Log.i("info"," MainActivity ACTION_UP");
                isTouched = false;
                touchX = 0.0f;
                touchY = 0.0f;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("info"," MainActivity ACTION_MOVE");
                touchX += event.getX();
                touchY += event.getY();
                Log.i("info"," MainActivity Y = " + touchY);
                break;
            default:break;
        }
        return true;
    }

    private void slideUp() {

    }
    private void slideDown() {

    }
}
