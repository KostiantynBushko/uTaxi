package com.onquantum.utaxi.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;

import com.onquantum.utaxi.R;

import java.util.List;

/**
 * Created by Admin on 10/11/14.
 */
public class SpeechRecognitionHelper {

    public static void run(Activity activity) {

        if (isSpeechRecognitionActivityPresented(activity)) {
            startRecognitionActivity(activity);
        } else {
            installGoogleVoiceSearch(activity);
        }

    }

    private static boolean isSpeechRecognitionActivityPresented(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            List activities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            if (activities.size() != 0)
                return true;
        } catch (Exception e) {}
        return false;
    }

    private static void startRecognitionActivity(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,activity.getResources().getString(R.string.speak_address));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        activity.startActivityForResult(intent,Constant.VOICE_RECOGNITION_REQUEST_CODE);
    }

    private static void installGoogleVoiceSearch(final Activity ownerActivity) {

    }

}
