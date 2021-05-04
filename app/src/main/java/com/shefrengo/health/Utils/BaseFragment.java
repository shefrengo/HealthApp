package com.shefrengo.health.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;


    public class BaseFragment extends Fragment {


        protected static String currentTab;
        protected FragmentInteractionCallback fragmentInteractionCallback;
        private View mRootView;

        private boolean mIsInitData;

        public static void setCurrentTab(String currentTab) {
            BaseFragment.currentTab = currentTab;
        }

        @Override
        public void onAttach(@NotNull Context context) {
            super.onAttach(context);
            try {
                fragmentInteractionCallback = (FragmentInteractionCallback) context;
            } catch (ClassCastException e) {
                throw new RuntimeException(context.toString() + " must implement " + FragmentInteractionCallback.class.getName());
            }
        }

        @Override
        public void onDetach() {
            fragmentInteractionCallback = null;
            super.onDetach();
        }

        public interface FragmentInteractionCallback {

            void onFragmentInteractionCallback(Bundle bundle);
        }
    }

