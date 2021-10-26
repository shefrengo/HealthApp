package com.shefrengo.health.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SimpleLocation {

    private static final String PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER;
    private static final String PROVIDER_FINE = LocationManager.GPS_PROVIDER;
    private static final String PROVIDER_FINE_PASSIVE = LocationManager.PASSIVE_PROVIDER;
    private static final long INTERVAL_DEFAULT = 10 * 1000;
    private static final float KILOMETER_TO_METER = 1000.0f;
    private static final float LATITUDE_TO_KILOMETER = 111.133f;
    private static final float LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE = 111.320f;
    private static final Random mRandom = new Random();
    private static final double SQUARE_ROOT_TWO = Math.sqrt(2);
    private static Location mCachedPosition;
    private final LocationManager mLocationManager;
    private final boolean mRequireFine;
    private final boolean mPassive;
    private final long mInterval;
    private final boolean mRequireNewLocation;
    private Geocoder geocoder;
    private int mBlurRadius;
    private LocationListener mLocationListener;
    private Location mPosition;
    private Listener mListener;

    public SimpleLocation(final Context context) {
        this(context, false);

    }

    public SimpleLocation(final Context context, final boolean requireFine) {
        this(context, requireFine, false);
    }

    public SimpleLocation(final Context context, final boolean requireFine, final boolean passive) {
        this(context, requireFine, passive, INTERVAL_DEFAULT);
    }

    public SimpleLocation(final Context context, final boolean requireFine, final boolean passive, final long interval) {
        this(context, requireFine, passive, interval, false);
    }

    public SimpleLocation(final Context context, final boolean requireFine, final boolean passive, final long interval, final boolean requireNewLocation) {
        mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mRequireFine = requireFine;
        mPassive = passive;
        mInterval = interval;
        mRequireNewLocation = requireNewLocation;

        if (!mRequireNewLocation) {
            mPosition = getCachedPosition();
            cachePosition();
        }
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    /**
     * For any radius `n`, calculate a random offset in the range `[-n, n]`
     *
     * @param radius the radius
     * @return the random offset
     */
    private static int calculateRandomOffset(final int radius) {
        return mRandom.nextInt((radius + 1) * 2) - radius;
    }

    public static void openSettings(final Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public static double latitudeToKilometer(double latitude) {
        return latitude * LATITUDE_TO_KILOMETER;
    }

    public static double kilometerToLatitude(double kilometer) {
        return kilometer / latitudeToKilometer(1.0f);
    }

    public static double latitudeToMeter(double latitude) {
        return latitudeToKilometer(latitude) * KILOMETER_TO_METER;
    }

    public static double meterToLatitude(double meter) {
        return meter / latitudeToMeter(1.0f);
    }

    public static double longitudeToKilometer(double longitude, double latitude) {
        return longitude * LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE * Math.cos(Math.toRadians(latitude));
    }

    public static double kilometerToLongitude(double kilometer, double latitude) {
        return kilometer / longitudeToKilometer(1.0f, latitude);
    }

    public static double longitudeToMeter(double longitude, double latitude) {
        return longitudeToKilometer(longitude, latitude) * KILOMETER_TO_METER;
    }

    public static double meterToLongitude(double meter, double latitude) {
        return meter / longitudeToMeter(1.0f, latitude);
    }

    public static double calculateDistance(Point start, Point end) {
        return calculateDistance(start.latitude, start.longitude, end.latitude, end.longitude);
    }

    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public boolean hasLocationEnabled() {
        return hasLocationEnabled(getProviderName());
    }

    private boolean hasLocationEnabled(final String providerName) {
        try {
            return mLocationManager.isProviderEnabled(providerName);
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    public void beginUpdates() {
        if (mLocationListener != null) {
            endUpdates();
        }

        if (!mRequireNewLocation) {
            mPosition = getCachedPosition();
        }

        mLocationListener = createLocationListener();
        mLocationManager.requestLocationUpdates(getProviderName(), mInterval, 0, mLocationListener);
    }

    public void endUpdates() {

        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationListener = null;
        }
    }

    /**
     * Blurs the specified location with the defined blur radius or returns an unchanged location if no blur radius is set
     *
     * @param originalLocation the original location received from the device
     * @return the blurred location
     */
    private Location blurWithRadius(final Location originalLocation) {
        if (mBlurRadius <= 0) {
            return originalLocation;
        } else {
            Location newLocation = new Location(originalLocation);

            double blurMeterLong = calculateRandomOffset(mBlurRadius) / SQUARE_ROOT_TWO;
            double blurMeterLat = calculateRandomOffset(mBlurRadius) / SQUARE_ROOT_TWO;

            newLocation.setLongitude(newLocation.getLongitude() + meterToLongitude(blurMeterLong, newLocation.getLatitude()));
            newLocation.setLatitude(newLocation.getLatitude() + meterToLatitude(blurMeterLat));

            return newLocation;
        }
    }

    public Point getPosition() {
        if (mPosition == null) {
            return null;
        } else {
            Location position = blurWithRadius(mPosition);
            return new Point(position.getLatitude(), position.getLongitude());
        }
    }

    public double getLatitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(mPosition);
            return position.getLatitude();
        }
    }

    public double getLongitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(mPosition);
            return position.getLongitude();
        }
    }

    public float getSpeed() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            return mPosition.getSpeed();
        }
    }

    public double getAltitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            return mPosition.getAltitude();
        }
    }

    public Location getLocation() {
        return mPosition;
    }

    public void setBlurRadius(final int blurRadius) {
        mBlurRadius = blurRadius;
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                mPosition = location;
                cachePosition();

                if (mListener != null) {
                    mListener.onPositionChanged();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                if (mListener != null) {
                    mListener.onStatusChanged(provider, status, extras);
                }
            }

            @Override
            public void onProviderEnabled(String provider) {

                if (mListener != null) {
                    mListener.onProviderEnabled(provider);
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                if (mListener != null) {
                    mListener.onProviderDisabled(provider);
                }
            }

        };
    }

    private String getProviderName() {
        return getProviderName(mRequireFine);
    }

    private String getProviderName(final boolean requireFine) {
        if (requireFine) {
            if (mPassive) {
                return PROVIDER_FINE_PASSIVE;
            } else {
                return PROVIDER_FINE;
            }
        } else {
            if (hasLocationEnabled(PROVIDER_COARSE)) {
                if (mPassive) {
                    throw new RuntimeException("There is no passive provider for the coarse location");
                } else {
                    return PROVIDER_COARSE;
                }
            } else {
                if (hasLocationEnabled(PROVIDER_FINE) || hasLocationEnabled(PROVIDER_FINE_PASSIVE)) {
                    return getProviderName(true);
                } else {
                    return PROVIDER_COARSE;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private Location getCachedPosition() {
        if (mCachedPosition != null) {
            return mCachedPosition;
        } else {
            try {
                return mLocationManager.getLastKnownLocation(getProviderName());
            } catch (Exception e) {
                return null;
            }
        }
    }

    private void cachePosition() {
        if (mPosition != null) {
            mCachedPosition = mPosition;
        }
    }

    public Address getAddress() {
        try {
            List<Address> addresses = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                //if (returnedAddress.getAddressLine(0) != null) {
                return returnedAddress;
                //}
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static interface Listener {

        public void onPositionChanged();

        public void onStatusChanged(String provider, int status, Bundle extras);

        public void onProviderEnabled(String provider);

        public void onProviderDisabled(String provider);

    }

    public static class Point implements Parcelable {

        public static final Creator<Point> CREATOR = new Creator<Point>() {

            @Override
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            @Override
            public Point[] newArray(int size) {
                return new Point[size];
            }

        };
        public final double latitude;
        public final double longitude;

        public Point(double lat, double lon) {
            latitude = lat;
            longitude = lon;
        }

        private Point(Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }

        @Override
        public String toString() {
            return "(" + latitude + ", " + longitude + ")";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}