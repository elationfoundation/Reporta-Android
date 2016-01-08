package com.iwmf.fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iwmf.R;
import com.iwmf.adapters.LocationListAdapter;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.LocationData;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.AESCrypt;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * <p> User can pick location from the given list.
 * Also displays a map to select location. </p>
 */
public class PickLocationFragment extends BaseFragment implements OnClickListener {

    private final Comparator<LocationData> distComparator = new Comparator<LocationData>() {

        @Override
        public int compare(LocationData lhs, LocationData rhs) {

            return Double.compare(lhs.getDistance(), rhs.getDistance());
        }
    };

    private ListView listView;
    private EditText edtAddress;
    private SupportMapFragment mapFragment = null;
    private GoogleMap googleMap = null;
    private ArrayList<LocationData> list = null;
    private double latitude = 0, longitude = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_pick_location, container, false);

        mappingWidgets(mView);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        edtAddress.setText(getArguments().getString("address"));
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");

        initGoogleMap();
    }

    @Override
    public void onResume() {

        super.onResume();

        if (googleMap == null) {
            googleMap = mapFragment.getMap();
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.setMyLocationEnabled(false);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
        }

        if (latitude != 0 && longitude != 0) {
            putOverlay(latitude, longitude);

            if (Utils.isEmpty(edtAddress.getText().toString().trim())) {
                getAddressFromLatLong();
            }

        } else {

            if (ConstantData.LATITUDE != 0 && ConstantData.LONGITUDE != 0) {
                latitude = ConstantData.LATITUDE;
                longitude = ConstantData.LONGITUDE;
                putOverlay(ConstantData.LATITUDE, ConstantData.LONGITUDE);
                getAddressFromLatLong();
            } else {

                displayProgressDialog();

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            Looper.prepare();

                            while (ConstantData.LATITUDE == 0 && ConstantData.LONGITUDE == 0) {
                                Thread.sleep(1000);
                            }

                            dismissProgressDialogOnUIthread();

                            if (ConstantData.LATITUDE != 0 && ConstantData.LONGITUDE != 0) {
                                latitude = ConstantData.LATITUDE;
                                longitude = ConstantData.LONGITUDE;
                                putOverlay(ConstantData.LATITUDE, ConstantData.LONGITUDE);
                                getAddressFromLatLong();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            dismissProgressDialogOnUIthread();
                        }
                    }

                }).start();
            }
        }
    }

    private void mappingWidgets(View v) {

        edtAddress = (EditText) v.findViewById(R.id.edtAddress);
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setEmptyView(v.findViewById(R.id.txtEmptyView));

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if (position < list.size()) edtAddress.setText(list.get(position).getAddress());
            }
        });
    }

    private void initGoogleMap() {

        try {

            FragmentManager fm = getChildFragmentManager();
            mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.frmMapContainer);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.frmMapContainer, mapFragment).commit();
            }

            googleMap = mapFragment.getMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putOverlay(final double lat, final double lng) {

        try {

            if (googleMap != null) {

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        googleMap.clear();
                        LatLng mLatLngPickup = new LatLng(lat, lng);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLngPickup, 16));
                        Marker marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).anchor(0.5f, 1.0f).position(mLatLngPickup));
                        marker.setDraggable(true);

                        googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

                            @Override
                            public void onMarkerDragStart(Marker arg0) {

                            }

                            @Override
                            public void onMarkerDragEnd(Marker arg0) {

                                latitude = arg0.getPosition().latitude;
                                longitude = arg0.getPosition().longitude;

                                getAddressFromLatLong();
                                getNearByLocations();
                            }

                            @Override
                            public void onMarkerDrag(Marker arg0) {

                            }
                        });
                    }
                });

                getNearByLocations();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dismissProgressDialog();

        }
    }

    private void getAddressFromLatLong() {

        displayProgressDialog();

        try {
            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s", latitude, longitude, AESCrypt.decrypt(ConstantData.GOOGLE_PLACES_KEY));

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(url, RequestMethod.GET, "");

            mAsyncWebServiceLoader.setCallback(new Callbacks() {
                @Override
                public void onResponse(String result) {

                    try {

                        dismissProgressDialog();

                        JSONObject jObjResult = new JSONObject(result);

                        if (jObjResult.getString("status").equalsIgnoreCase("OK")) {

                            JSONArray jArrayPlaces = jObjResult.getJSONArray("results");

                            if (jArrayPlaces.length() > 0) {
                                final String address = jArrayPlaces.getJSONObject(0).getString("formatted_address");

                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        edtAddress.setText(address);
                                    }
                                });
                            }
                        } else {
                            Toast.displayError(getActivity(), getString(R.string.try_later));
                            dismissProgressDialog();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                    dismissProgressDialog();
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.displayError(getActivity(), getString(R.string.try_later));
            dismissProgressDialog();
        }
    }

    private void getNearByLocations() {

        displayProgressDialog();

        try {

            String url = String.format("https://maps.googleapis.com/maps/api/place/search/json?location=%s,%s&key=%s&radius=5000", latitude, longitude, AESCrypt.decrypt(ConstantData.GOOGLE_PLACES_KEY));

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(url, RequestMethod.GET, "");

            mAsyncWebServiceLoader.setCallback(new Callbacks() {
                @Override
                public void onResponse(String result) {

                    try {

                        dismissProgressDialog();

                        JSONObject jObjResult = new JSONObject(result);

                        if (jObjResult.getString("status").equalsIgnoreCase("OK")) {

                            list = new ArrayList<>();

                            JSONArray jArrayPlaces = jObjResult.getJSONArray("results");

                            for (int i = 0; i < jArrayPlaces.length(); i++) {

                                try {

                                    JSONObject jObj = jArrayPlaces.getJSONObject(i);
                                    LocationData data = new LocationData();

                                    String name = jObj.getString("name");
                                    String vicinity = jObj.getString("vicinity");

                                    data.setName(name);
                                    if (vicinity.startsWith(name) || vicinity.contains(name)) {
                                        data.setAddress(vicinity);
                                    } else {
                                        data.setAddress(name + ", " + vicinity);
                                    }
                                    data.setLatitude(jObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                                    data.setLongitude(jObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));

                                    Location loc1 = new Location("");
                                    loc1.setLatitude(latitude);
                                    loc1.setLongitude(longitude);

                                    Location loc2 = new Location("");
                                    loc2.setLatitude(data.getLatitude());
                                    loc2.setLongitude(data.getLongitude());

                                    float disInMeters = loc1.distanceTo(loc2);
                                    data.setDistance(disInMeters);

                                    if (disInMeters * 0.000621371 > 1) {
                                        try {
                                            data.setDistanceString(String.format(getString(R.string.dist_mi), (disInMeters * 0.000621371)));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            data.setDistanceString(getString(R.string.dist_mi));
                                        }

                                    } else {
                                        try {
                                            data.setDistanceString(String.format(getString(R.string.dist_ft), (int) (disInMeters * 3.28084)));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            data.setDistanceString(getString(R.string.dist_ft));
                                        }
                                    }

                                    list.add(data);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            Collections.sort(list, distComparator);

                        } else {
                            Toast.displayError(getActivity(), getString(R.string.try_later));
                            dismissProgressDialog();
                        }

                        listView.setAdapter(new LocationListAdapter(getActivity(), list));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                    dismissProgressDialog();
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.displayError(getActivity(), getString(R.string.try_later));
            dismissProgressDialog();
        }
    }

    public void getLatLongFromLocation() {

        displayProgressDialog();

        try {

            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", URLEncoder.encode(edtAddress.getText().toString().trim(), "UTF-8"), AESCrypt.decrypt(ConstantData.GOOGLE_PLACES_KEY));

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(url, RequestMethod.GET, "");
            mAsyncWebServiceLoader.setCallback(new Callbacks() {
                @Override
                public void onResponse(String result) {

                    try {

                        dismissProgressDialog();

                        JSONObject jObjResult = new JSONObject(result);

                        if (jObjResult.getString("status").equalsIgnoreCase("OK")) {

                            JSONArray jArrayPlaces = jObjResult.getJSONArray("results");

                            if (jArrayPlaces.length() > 0) {

                                JSONObject jObj = jArrayPlaces.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                                latitude = jObj.getDouble("lat");
                                longitude = jObj.getDouble("lng");

                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        Intent intent = new Intent();
                                        intent.putExtra("address", edtAddress.getText().toString().trim());
                                        intent.putExtra("latitude", latitude);
                                        intent.putExtra("longitude", longitude);
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
                                    }
                                });
                            } else {
                                Toast.displayMessage(getActivity(), getString(R.string.loc_not_found));
                            }
                        } else {
                            Toast.displayMessage(getActivity(), getString(R.string.loc_not_found));
                        }
                    } catch (Exception e) {
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                    dismissProgressDialog();
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.displayError(getActivity(), getString(R.string.try_later));
            dismissProgressDialog();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtBack:
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
                break;

            case R.id.txtDone:
                getLatLongFromLocation();
                break;
        }
    }

}
