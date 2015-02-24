package com.onquantum.utaxi.wizard;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.onquantum.utaxi.AbstractFragment;
import com.onquantum.utaxi.R;
import com.onquantum.utaxi.common.URL;
import com.onquantum.utaxi.services.GetStreetsService;
import com.onquantum.utaxi.services.TrackingService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 9/24/14.
 */
public class FragmentWizardStepTwo extends AbstractFragmentWizard {

    public static String STREET = "street";

    private View root;
    private GoogleMap googleMap;
    private boolean trackingServiceIsBind = false;
    private LatLng currentLocation;
    private LatLng cameraLocation;
    private boolean isMapTouches = false;
    private TouchableWrapper touchableWrapper;
    private boolean isTouchEnable = false;
    private TextView tvAddress;

    private Intent intent;
    private boolean isBound;
    private ServiceConnection serviceConnection;

    private ArrayList<HashMap<String, Object>> listObjects = null;
    private ListView listView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        intent = new Intent(GetStreetsService.GET_STREET_SERVICE_INTENT_NAME);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("info"," FragmentWizardStepTwo onServiceConnection");
                isBound = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("info"," FragmentWizardStepTwo onServiceDisconnected");
                isBound = false;
            }
        };

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        currentWizardStep = 3;
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.destination_address));

        if (root != null) {
            ViewGroup parent = (ViewGroup)root.getParent();
            if (parent != null)
                parent.removeView(root);
        }

        try {
            root = layoutInflater.inflate(R.layout.wizard_step_two, container,false);
            touchableWrapper = new TouchableWrapper(getActivity());
            touchableWrapper.addView(root);
        }catch (InflateException e){}

       ((Button) root.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.move_left_in, R.anim.move_left_out,
                        R.anim.move_right_in, R.anim.move_right_out
                );
                transaction.replace(R.id.fragment, new FragmentWizardStepTwo());
                transaction.addToBackStack(FragmentWizardStepTwo.class.getName());
                transaction.commit();
            }
        });

        listView = (ListView)root.findViewById(R.id.listView);

        new GetStreets().execute();
        isTouchEnable = true;
        return touchableWrapper;
    }

    @Override
    public View getView() {
        return root;
    }

    @Override
    public void onDestroyView() {
        if (isBound) {
            getActivity().unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroyView();
    }

    private class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }
        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (!isTouchEnable)
                return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("info","ACTION_DOWN");
                    isMapTouches = true;
                    break;
                case MotionEvent.ACTION_UP:
                    //Log.i("info","ACTION_UP");
                    isMapTouches = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    private void updateList() {
        listView.setAdapter(null);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), listObjects ,R.layout.item_street,
                new String[]{STREET},
                new int[]{R.id.textView5});
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
    }

    class GetStreets extends AsyncTask<Void, Void, Boolean> {
        private String resultString = null;
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost(URL.streets);

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(1);
            nameValuePairList.add(new BasicNameValuePair("streets", ""));

            try {

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                resultString = EntityUtils.toString(httpEntity);
                return true;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.i("info","RESULT = " + resultString);
            } else {
            }
        }
    }

}
