package com.shefrengo.health;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.shefrengo.health.Fragments.CommunitiesFragment;
import com.shefrengo.health.Fragments.HomeFragment;
import com.shefrengo.health.Fragments.NotificationFragment;
import com.shefrengo.health.Fragments.ProfileFragment;
import com.shefrengo.health.Utils.BaseFragment;
import com.shefrengo.health.Utils.FragmentStrings;
import com.shefrengo.health.Utils.SharedViewModel;

import net.skoumal.fragmentback.BackFragmentAppCompatActivity;
import net.skoumal.fragmentback.BackFragmentHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import static android.provider.ContactsContract.Intents.Insert.ACTION;
import static com.shefrengo.health.Utils.Constants.DATA_KEY_1;
import static com.shefrengo.health.Utils.Constants.DATA_KEY_2;
import static com.shefrengo.health.Utils.Constants.DATA_KEY_3;
import static com.shefrengo.health.Utils.Constants.EXTRA_IS_ROOT_FRAGMENT;

import static com.shefrengo.health.Utils.Constants.TAB_CHALLENGES;
import static com.shefrengo.health.Utils.Constants.TAB_HOME_VIDEOS;
import static com.shefrengo.health.Utils.Constants.TAB_NOTIFICATIONS;
import static com.shefrengo.health.Utils.Constants.TAB_PROFILE;
import static com.shefrengo.health.Utils.FragmentStrings.HOME;
import static com.shefrengo.health.Utils.FragmentUtils.addAdditionalTabFragment;
import static com.shefrengo.health.Utils.FragmentUtils.addInitialTabFragment;
import static com.shefrengo.health.Utils.FragmentUtils.addShowHideFragment;
import static com.shefrengo.health.Utils.FragmentUtils.removeFragment;
import static com.shefrengo.health.Utils.FragmentUtils.showHideTabFragment;
import static com.shefrengo.health.Utils.StackListManager.updateStackIndex;
import static com.shefrengo.health.Utils.StackListManager.updateStackToIndexFirst;
import static com.shefrengo.health.Utils.StackListManager.updateTabStackIndex;

public class MainActivity extends BackFragmentAppCompatActivity implements BaseFragment.FragmentInteractionCallback {

    public static final String TAG = "MainActivity";

    private BottomNavigationViewEx bottomNavigationView;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SharedViewModel viewModel;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Stack<String>> tagStacks;
    private String currentTab;
    private List<String> stackList;
    private List<String> menuStacks;
    private Fragment currentFragment;
    private Fragment homeVideos;
    private Fragment notificationFragment;
    private Fragment profileFragment;
    private Fragment communities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView = findViewById(R.id.bnve);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        setupFirebaseAuth();
        createStacks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            overridePendingTransition(0, 0);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            //check if the user is logged in
            checkCurrentUser(user);

            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

    private final BottomNavigationViewEx.OnNavigationItemReselectedListener reselectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.action_home:
                popStackExceptFirst();
                break;
            case R.id.action_search:
                popStackExceptFirst();
                break;
            case R.id.action_heart:
                popStackExceptFirst();
                break;
            case R.id.action_profile:
                popStackExceptFirst();
                break;
        }
    };

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationViewEx.OnNavigationItemSelectedListener listener = item -> {
        switch (item.getItemId()) {

            case R.id.action_home:
                selectedTab(TAB_HOME_VIDEOS);
                return true;
            case R.id.action_heart:
                selectedTab(TAB_NOTIFICATIONS);
                return true;
            case R.id.action_search:
                selectedTab(TAB_CHALLENGES);
                return true;

            case R.id.action_profile:
                selectedTab(TAB_PROFILE);
              return true;
        }
        return false;
    };

    @Override
    public void onFragmentInteractionCallback(Bundle bundle) {
        String action = bundle.getString(ACTION);


        if (action != null) {
            switch (action) {

                case FragmentStrings.SEARRCH_FRAGMENT:
                    showFragment(bundle, CommunitiesFragment.newInstance(false));
                    break;
                case FragmentStrings.PROFILE:
                    showFragment(bundle, ProfileFragment.newInstance(false));
                    break;
                case HOME:
                    showFragment(bundle, HomeFragment.newInstance(false));
                    break;
                case FragmentStrings.NOTIFICATIONS:
                    showFragment(bundle, NotificationFragment.newInstance(false));
                    break;

            }
        }
    }

    private void createStacks() {
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(true);

        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.setTextVisibility(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        homeVideos = HomeFragment.newInstance(true);
        notificationFragment = NotificationFragment.newInstance(true);
        profileFragment = ProfileFragment.newInstance(true);
        communities = CommunitiesFragment.newInstance(true);
        tagStacks = new LinkedHashMap<>();

        tagStacks.put(TAB_HOME_VIDEOS, new Stack<String>());
        tagStacks.put(TAB_NOTIFICATIONS, new Stack<String>());
        tagStacks.put(TAB_CHALLENGES, new Stack<String>());
        tagStacks.put(TAB_PROFILE, new Stack<String>());

        menuStacks = new ArrayList<>();
        menuStacks.add(TAB_HOME_VIDEOS);
        stackList = new ArrayList<>();
        stackList.add(TAB_HOME_VIDEOS);
        stackList.add(TAB_NOTIFICATIONS);
        stackList.add(TAB_CHALLENGES);
        stackList.add(TAB_PROFILE);

        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemReselectedListener(reselectedListener);
    }


    private void selectedTab(String tabId) {
        currentTab = tabId;
        BaseFragment.setCurrentTab(currentTab);

        if (Objects.requireNonNull(tagStacks.get(tabId)).size() == 0) {
            /*
              First time this tab is selected. So add first fragment of that tab.
              We are adding a new fragment which is not present in stack. So add to stack is true.
             */
            switch (tabId) {
                case TAB_HOME_VIDEOS:
                    addInitialTabFragment(getSupportFragmentManager(), tagStacks, TAB_HOME_VIDEOS, homeVideos, R.id.center_frame_layout, true);
                    resolveStackLists(tabId);
                    assignCurrentFragment(homeVideos);

                    break;

                case TAB_NOTIFICATIONS:
                    addAdditionalTabFragment(getSupportFragmentManager(), tagStacks, TAB_NOTIFICATIONS, notificationFragment, currentFragment, R.id.center_frame_layout, true);
                    resolveStackLists(tabId);
                    assignCurrentFragment(notificationFragment);
                    break;
                case TAB_CHALLENGES:
                    addAdditionalTabFragment(getSupportFragmentManager(), tagStacks, TAB_CHALLENGES, communities, currentFragment, R.id.center_frame_layout, true);
                    resolveStackLists(tabId);
                    assignCurrentFragment(communities);
                    break;
                case TAB_PROFILE:
                    addAdditionalTabFragment(getSupportFragmentManager(), tagStacks, TAB_PROFILE, profileFragment, currentFragment, R.id.center_frame_layout, true);
                    resolveStackLists(tabId);
                    assignCurrentFragment(profileFragment);
                    break;

            }
        } else {
            /*
             * We are switching tabs, and target tab already has at least one fragment.
             *
             * Show the target fragment
             */
            Fragment targetFragment = getSupportFragmentManager().findFragmentByTag(Objects.requireNonNull(tagStacks.get(tabId)).lastElement());
            showHideTabFragment(getSupportFragmentManager(), targetFragment, currentFragment);
            resolveStackLists(tabId);
            assignCurrentFragment(targetFragment);
        }


    }


    private void popFragment() {
        /*
         * Select the second last fragment in current tab's stack,
         * which will be shown after the fragment transaction given below
         */
        String fragmentTag = Objects.requireNonNull(tagStacks.get(currentTab))
                .elementAt(Objects.requireNonNull(tagStacks.get(currentTab)).size() - 2);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        /*pop current fragment from stack */

        Objects.requireNonNull(tagStacks.get(currentTab)).pop();

        removeFragment(getSupportFragmentManager(), fragment, currentFragment);

        assignCurrentFragment(fragment);
    }

    private void resolveBackPressed() {
        int stackValue = 0;
        if (Objects.requireNonNull(tagStacks.get(currentTab)).size() == 1) {
            Stack<String> value = tagStacks.get(stackList.get(1));
            assert value != null;
            if (value.size() > 1) {
                stackValue = value.size();
                popAndNavigateToPreviousMenu();
            }
            if (stackValue <= 1) {
                if (menuStacks.size() > 1) {
                    navigateToPreviousMenu();
                } else {
                    finish();
                }
            }
        } else {
            popFragment();
        }
    }

    /*Pops the last fragment inside particular tab and goes to the second tab in the stack*/
    private void popAndNavigateToPreviousMenu() {
        String tempCurrent = stackList.get(0);
        currentTab = stackList.get(1);
        BaseFragment.setCurrentTab(currentTab);
        bottomNavigationView.setSelectedItemId(resolveTabPositions(currentTab));

        Fragment targetFragment = getSupportFragmentManager().findFragmentByTag(Objects.requireNonNull(tagStacks.get(currentTab)).lastElement());
        showHideTabFragment(getSupportFragmentManager(), targetFragment, currentFragment);
        assignCurrentFragment(targetFragment);

        updateStackToIndexFirst(stackList, tempCurrent);


        menuStacks.remove(0);

    }

    private void navigateToPreviousMenu() {
        menuStacks.remove(0);
        currentTab = menuStacks.get(0);
        BaseFragment.setCurrentTab(currentTab);
        bottomNavigationView.setSelectedItemId(resolveTabPositions(currentTab));
        Fragment targetFragment = getSupportFragmentManager().findFragmentByTag(Objects.requireNonNull(tagStacks.get(currentTab)).lastElement());
        showHideTabFragment(getSupportFragmentManager(), targetFragment, currentFragment);
        assignCurrentFragment(targetFragment);
    }

    private void popStackExceptFirst() {
        if (Objects.requireNonNull(tagStacks.get(currentTab)).size() == 1) {
            return;
        }
        while (!Objects.requireNonNull(tagStacks.get(currentTab)).empty()
                && !Objects.requireNonNull(Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentByTag(Objects.requireNonNull(tagStacks.get(currentTab)).peek()))
                .getArguments()).getBoolean(EXTRA_IS_ROOT_FRAGMENT)) {

            getSupportFragmentManager().beginTransaction()
                    .remove(Objects.requireNonNull(Objects.requireNonNull(getSupportFragmentManager()
                            .findFragmentByTag(Objects.requireNonNull(tagStacks.get(currentTab)).peek()))));
            tagStacks.get(currentTab).pop();
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Objects.requireNonNull(tagStacks.get(currentTab)).elementAt(0));
        removeFragment(getSupportFragmentManager(), fragment, currentFragment);
        assignCurrentFragment(fragment);
    }

    /*
     * Add a fragment to the stack of a particular tab
     */
    private void showFragment(Bundle bundle, Fragment fragmentToAdd) {
        String tab = bundle.getString(DATA_KEY_1);
        boolean shouldAdd = bundle.getBoolean(DATA_KEY_2);
        boolean receiveData = bundle.getBoolean(DATA_KEY_3);

        addShowHideFragment(getSupportFragmentManager(), tagStacks, tab, fragmentToAdd, getCurrentFragmentFromShownStack(),
                R.id.center_frame_layout, shouldAdd, viewModel, receiveData);
        assignCurrentFragment(fragmentToAdd);
    }

    private int resolveTabPositions(String currentTab) {
        int tabIndex = 0;
        switch (currentTab) {
            case TAB_HOME_VIDEOS:
                tabIndex = R.id.action_home;
                break;
            case TAB_NOTIFICATIONS:
                tabIndex = R.id.action_heart;
                break;
            case TAB_PROFILE:
                tabIndex = R.id.action_profile;
                break;
            case TAB_CHALLENGES:
                tabIndex = R.id.action_search;
                break;
        }

        return tabIndex;
    }

    private void resolveStackLists(String tabId) {
        updateStackIndex(stackList, tabId);



        updateTabStackIndex(menuStacks, tabId);
    }


    private Fragment getCurrentFragmentFromShownStack() {
        return getSupportFragmentManager().findFragmentByTag(tagStacks.get(currentTab).elementAt(tagStacks.get(currentTab).size() - 1));
    }

    private void assignCurrentFragment(Fragment current) {
        currentFragment = current;
    }

    @Override
    public void onBackPressed() {

        // first ask your fragments to handle back-pressed event
        if (!BackFragmentHelper.fireOnBackPressedEvent(this)) {
            // lets do the default back action if fragments don't consume it
            resolveBackPressed();
        }

    }
}