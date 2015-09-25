package architect.jazzy.medicinereminder.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import architect.jazzy.medicinereminder.Adapters.NewsListAdapter;
import architect.jazzy.medicinereminder.CustomComponents.FragmentBackStack;
import architect.jazzy.medicinereminder.CustomViews.DaySelectorFragmentDialog;
import architect.jazzy.medicinereminder.Fragments.AddDoctorFragment;
import architect.jazzy.medicinereminder.Fragments.AddMedicineFragment;
import architect.jazzy.medicinereminder.Fragments.BrowserFragment;
import architect.jazzy.medicinereminder.Fragments.DashboardFragment;
import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.DoctorDetailFragment;
import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.DoctorMedicineListFragment;
import architect.jazzy.medicinereminder.Fragments.DoctorListFragment;
import architect.jazzy.medicinereminder.Fragments.EmojiSelectFragment;
import architect.jazzy.medicinereminder.Fragments.MedicineListFragment;
import architect.jazzy.medicinereminder.Fragments.NewsFragments.NewsListFragment;
import architect.jazzy.medicinereminder.Fragments.Practo.DoctorSearch;
import architect.jazzy.medicinereminder.Fragments.SearchFragments.SearchFragment;
import architect.jazzy.medicinereminder.Fragments.SettingsFragment;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.FeedItem;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.Services.AlarmSetterService;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        EmojiSelectFragment.OnFragmentInteractionListener,  DaySelectorFragmentDialog.OnFragmentInteractionListener,
        MedicineListFragment.FragmentInteractionListener, AddMedicineFragment.FragmentInteractionListener,
        NewsListAdapter.FeedClickListener, DoctorListFragment.OnMenuItemClickListener,
        DoctorMedicineListFragment.FragmentInteractionListener, DoctorListFragment.OnFragmentInteractionListenr,
        DoctorDetailFragment.ImageChangeListener, DashboardFragment.OnFragmentInteractionListener,
        AddDoctorFragment.OnFragmentInteractionListener{

    public static final String TAG="MainActivity";

    FragmentBackStack fragmentBackStack=new FragmentBackStack();
    final int SHOW_LIST_REQUEST_CODE = 123;
    FrameLayout frameLayout;
    DrawerLayout drawerLayout;
    ActivityClickListener activityClickListener;
    ActivityResultListener activityResultListener;
    EditText searchQuery;
    NavigationView navigationView;
    private InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle=new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        frameLayout=(FrameLayout)findViewById(R.id.frame);



        Intent startAlarmServiceIntent=new Intent(this, AlarmSetterService.class);
        startAlarmServiceIntent.setAction("CANCEL");
        startService(startAlarmServiceIntent);
        startAlarmServiceIntent.setAction("CREATE");
        startService(startAlarmServiceIntent);


        //TODO: uncomment ad

        interstitialAd=new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId("ca-app-pub-6208186273505028/3306536191");
        AdRequest adRequest=new AdRequest.Builder().build();
        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //.addTestDevice("8143FD5F7B003AB85585893D768C3142");

//        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
//               displayInterstitial();
            }
        });


        navigationView = (NavigationView)findViewById(R.id.navigationView);
        searchQuery=(EditText)navigationView.findViewById(R.id.searchQuery);
        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.getMenu().findItem(R.id.add).setChecked(true);

        searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    searchQuery.setText("");
                    hideKeyboard();
                }
                return false;
            }
        });



        dimNotificationBar();

        displayFragment(new DashboardFragment(), true);
    }


    private void dimNotificationBar() {
        final View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                            }
                        });
                    }
                }, 5000);
            }
        });
    }


    public void displayInterstitial()
    {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }

    }
    private void performSearch(String s){
        displayFragment(SearchFragment.initiate(s), true);
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if(activityKeyClickListener!=null){
            if(activityKeyClickListener.onBackKeyPressed()){
                return;
            }
        }
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else if(!fragmentBackStack.empty()){
            Fragment fragment=fragmentBackStack.pop();
            if(fragment==null){
                android.support.v4.app.Fragment fragment1=fragmentBackStack.popSupport();
                if(fragment1==null){
                    super.onBackPressed();
                    return;
                }
                Log.e(TAG,"popping support fragment: "+fragment1.toString());
                displaySupportFragment(fragment1,false);
                return;
            }
            Log.e(TAG,"popping fragment");
            displayFragment(fragment,false);
        }
        else{
            super.onBackPressed();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        AddMedicineFragment.setAlarm(MainActivity.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id=menuItem.getItemId();

        Fragment fragment=null;
        android.support.v4.app.Fragment supportFragment=null;
        try {
            fragment = getFragmentManager().findFragmentById(R.id.frame);
        }catch (Exception e){
            supportFragment=getSupportFragmentManager().findFragmentById(R.id.frame);
        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        switch (id){
            case R.id.showMedicineList:
                if(!(fragment instanceof MedicineListFragment)) {
                    showMedicines();
                }
                break;
            case R.id.action_settings:
                showSettings();
                break;
//            case R.id.add:
//                addMedicine(false);
//                break;
            case R.id.credits:
                showCredits();
                break;
            case R.id.news:
                if(!(fragment instanceof NewsListFragment)) {
                    displayFragment(new NewsListFragment(), true);
                }
                break;
            case R.id.addDoctor:
                if(!(fragment instanceof DoctorListFragment)) {
                    displayFragment(new DoctorListFragment(), true);
                }
                break;
            case R.id.circularTest:
                if(!(fragment instanceof DashboardFragment)) {
                    displayFragment(new DashboardFragment(), true);
                }
                break;
            case R.id.practoSearch:
                if(!(supportFragment instanceof DoctorSearch)){
                    displaySupportFragment(new DoctorSearch(), true);
                }
                break;
            default:
                break;
        }
        drawerLayout.closeDrawers();
        return false;
    }


    void showSettings(){
//        Intent prefIntent = new Intent(this, BasicPreferences.class);
//        startActivity(prefIntent);
        displayFragment(new SettingsFragment(),true);
    }

    void showMedicines(){
        Fragment fragment=new MedicineListFragment();
        displayFragment(fragment, true);
    }


    void addMedicineToView(boolean add){
        Fragment fragment=new AddMedicineFragment();
        displayFragment(fragment, add);

    }

    public void displayFragment(Fragment fragment){
        displayFragment(fragment, false);
    }

    public void displayFragment(Fragment fragment, boolean add){
        try {
            getSupportActionBar().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        Log.e(TAG, "display fragment");
        if(add) {
            addToBackStack();
        }
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
//        Log.e("MainActivity", "Back Stack Count after Push:" + getFragmentManager().getBackStackEntryCount());
    }


    void addToBackStack(){
        try {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.frame);
            if(fragment==null){
                throw new Exception();
            }
            fragmentBackStack.push(fragment);
        }catch (Exception e){
            android.support.v4.app.Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.frame);
            if(fragment==null){
                return;
            }
            fragmentBackStack.push(fragment);
        }
    }


    public void displaySupportFragment(android.support.v4.app.Fragment fragment, boolean add){
        try {
            getSupportActionBar().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        if(fragment==null){
            return;
        }


        if(add){
            addToBackStack();
        }

        try{
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.frame)).commit();
        }catch (Exception e){
            e.printStackTrace();
        }


        Log.e(TAG,"display Support fragment: "+fragment.toString());

        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame,fragment).commit();

    }

    void showCredits(){
        startActivity(new Intent(this, AboutUs.class));
    }


    public static void setAlarm(Context context) {
        Intent startAlarmServiceIntent = new Intent(context, AlarmSetterService.class);
        startAlarmServiceIntent.setAction("CANCEL");
        context.startService(startAlarmServiceIntent);
        startAlarmServiceIntent.setAction("CREATE");
        context.startService(startAlarmServiceIntent);
    }



    public void showDaySelection(View v) {
        activityClickListener.daySelectionClick();
    }


    public void showFeed(String url){
        displayFragment(BrowserFragment.getInstance(url), true);
    }

    public void showFeed(String url, boolean isNews){
        displayFragment(BrowserFragment.getInstance(url, isNews), true);
    }



    @Override
    public void onEmojiSelected(int position) {
        activityClickListener.emojiClick(position);
    }

    @Override
    public void onDaySelectionChanged(int position, Boolean isChecked) {
        activityClickListener.daySelectionChanged(position, isChecked);
    }

    @Override
    public void addMedicine() {
        addMedicineToView(true);
    }

    @Override
    public void showMedicineList() {
        displayFragment(new MedicineListFragment(), true);
    }


    @Override
    public void onAddDoctorClicked() {
        displayFragment(new AddDoctorFragment(), true);
    }

    @Override
    public void onDoctorSelected(Doctor doctor) {
        displaySupportFragment(DoctorDetailFragment.newInstance(doctor), true);
    }

    @Override
    public void addDoctor() {
        AddDoctorFragment fragment=new AddDoctorFragment();
        displayFragment(fragment, true);
    }

    @Override
    public void addMedicine(boolean addToBackStack) {
        addMedicineToView(addToBackStack);
    }
    @Override
    public void onDoctorImageChange(int resultCode, Intent data) {
        if(doctorDetailImageChangeListener!=null){
            doctorDetailImageChangeListener.onDoctorImageChanged(resultCode,data);
        }
    }

    @Override
    public void showDoctors() {
        displayFragment(new DoctorListFragment(), true);
    }

    @Override
    public void showSearch() {
        displayFragment(new SearchFragment(), true);
    }

    @Override
    public void showDetails(int position, ArrayList<Medicine> medicines) {
        Intent i = new Intent(this, MedicineDetails.class);
//        for(Medicine medicine:medicines){
//            Log.e(TAG,"Starting Intent with Medicines: "+medicine.toJSON());
//        }
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST,medicines);
//        i.putExtra(Constants.MEDICINE_NAME_LIST, medicines);
        i.putExtra(Constants.MEDICINE_POSITION, position);
        i.putExtras(bundle);
        startActivityForResult(i, SHOW_LIST_REQUEST_CODE);
    }

    @Override
    public void onFeedClick(FeedItem item) {
        showFeed(item.getUrl());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "Activity Result " + requestCode);
        if (requestCode == SHOW_LIST_REQUEST_CODE) {
            activityResultListener.medicineListActivityResult(requestCode,resultCode,data);
        }
    }



    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    ActivityKeyClickListener activityKeyClickListener;
    DoctorDetailImageChangeListener doctorDetailImageChangeListener;




    public void setDoctorDetailImageChangeListener(DoctorDetailImageChangeListener doctorDetailImageChangeListener){
        this.doctorDetailImageChangeListener=doctorDetailImageChangeListener;
    }

    public void setActivityKeyClickListener(ActivityKeyClickListener activityKeyClickListener){
        this.activityKeyClickListener=activityKeyClickListener;
    }

    public void setActivityResultListener(ActivityResultListener activityResultListener){
        this.activityResultListener=activityResultListener;
    }

    public void setActivityClickListener(ActivityClickListener activityClickListener){
        this.activityClickListener=activityClickListener;
    }



    public interface ActivityClickListener{
        void daySelectionChanged(int position, boolean isCheck);
        void daySelectionClick();
        void emojiClick(int position);
    }

    public interface ActivityResultListener{
        void medicineListActivityResult(int requestCode, int resultCode, Intent data);
    }

    public interface DoctorDetailImageChangeListener{
        void onDoctorImageChanged(int resultCode, Intent data);
    }

    public interface ActivityKeyClickListener{
        boolean onBackKeyPressed();
    }
}

