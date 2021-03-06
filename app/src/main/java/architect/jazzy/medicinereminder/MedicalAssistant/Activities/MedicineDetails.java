package architect.jazzy.medicinereminder.MedicalAssistant.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Adapters.MedicineDetailsViewPagerAdapter;
import architect.jazzy.medicinereminder.MedicalAssistant.CustomViews.CustomViewPager;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.EmojiSelectFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.MedicineDetailFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class MedicineDetails extends AppCompatActivity implements EmojiSelectFragment.OnFragmentInteractionListener {

  private static final String TAG = "MedicineDetailActivity";
  CustomViewPager viewPager;
  ArrayList<Medicine> dataSet;
  MedicineDetailsViewPagerAdapter pagerAdapter;
  Button edit, delete;
  //    LinearLayout edit, delete;
//    TextView editTextView, deleteTextView;
  Toolbar toolbar;
  Boolean isScrollingEnabled = true;
  onEmojiSetListener emojiSetListener = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_medicine_details);


    edit = (Button) findViewById(R.id.editMedicine);
    delete = (Button) findViewById(R.id.deleteMedicine);

//        edit = (LinearLayout) findViewById(R.id.editMedicine);
//        delete = (LinearLayout) findViewById(R.id.deleteMedicine);
//        dataSet=getIntent().getParcelableArrayListExtra(Constants.MEDICINE_NAME_LIST);

    toolbar = (Toolbar) findViewById(R.id.toolBar);
    setSupportActionBar(toolbar);
    toolbar.setBackgroundColor(Constants.getThemeColor(this));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

    Bundle bundle = getIntent().getExtras();
    dataSet = bundle.getParcelableArrayList(Constants.MEDICINE_NAME_LIST);

    int currentPosition;
    try {
      currentPosition = getIntent().getIntExtra(Constants.MEDICINE_POSITION, 0);
    } catch (Exception e) {
      currentPosition = 0;
    }
    pagerAdapter = new MedicineDetailsViewPagerAdapter(getFragmentManager(), dataSet);

    viewPager = (CustomViewPager) findViewById(R.id.frame);
    viewPager.setAdapter(pagerAdapter);
    viewPager.setCurrentItem(currentPosition);
    viewPager.setEnabledSwipe(true);


//        editTextView = (TextView) findViewById(R.id.editButtonText);
//        deleteTextView = (TextView) findViewById(R.id.deleteButtonText);
    edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        editMedicine();
      }
    });
    delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        deleteMedicine();
      }
    });

    dimNotificationBar();
  }

  private void deleteMedicine() {
    Log.e(TAG, "Deleting Medicine");
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Confirm delete");
    builder.setMessage("Are you sure you want to delete the selected medicines from the list?");
    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        return;
      }
    });
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

        int currentIndex = viewPager.getCurrentItem();
//                Log.e("Current Fragment",String.valueOf(currentIndex));
        final MedicineDetailFragment medFragment = pagerAdapter.getFragment(currentIndex);
//                Log.e("Current Fragment", medFragment.getMedName() + " 55");
        Medicine medicine = medFragment.getMedicine();

        DataHandler handler = new DataHandler(MedicineDetails.this);
        if (!handler.deleteMedicine(medicine)) {
          Toast.makeText(getApplicationContext(), "Error deleting medicine", Toast.LENGTH_LONG).show();
          return;
        }
        dataSet.remove(medicine);
        viewPager.removeAllViews();
        viewPager.setAdapter(new MedicineDetailsViewPagerAdapter(getFragmentManager(), dataSet));
        viewPager.setCurrentItem(currentIndex + 1, true);
        handler.close();
        if (dataSet.size() == 0)
          finish();
        Toast.makeText(MedicineDetails.this, "Medicine removed", Toast.LENGTH_LONG).show();
      }
    });
    MainActivity.setAlarm(getApplicationContext());
    builder.show();
  }

  private void editMedicine() {
    final int currentIndex = viewPager.getCurrentItem();
    final MedicineDetailFragment medFragment = pagerAdapter.getFragment(currentIndex);
    if (isScrollingEnabled) {
            /*in save mode*/
      medFragment.edit();
      viewPager.setEnabledSwipe(false);
      isScrollingEnabled = false;
      edit.setText("Save");
      delete.setText("Discard");
      delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          medFragment.discard(dataSet.get(currentIndex));
          isScrollingEnabled = true;
          viewPager.setEnabledSwipe(true);
          edit.setText("Edit");
          delete.setText("Delete");
          Toast.makeText(getApplicationContext(), "Changes Discarded", Toast.LENGTH_LONG).show();
        }
      });
    } else {
            /*save mode exit*/
      medFragment.save();
      viewPager.setEnabledSwipe(true);
      edit.setText("Edit");
      delete.setText("Delete");
      delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          deleteMedicine();
        }
      });
      isScrollingEnabled = true;
    }
    MainActivity.setAlarm(getApplicationContext());
  }

  public void goBack(View v) {
    onBackPressed();
  }

  @Override
  protected void onResume() {
    super.onResume();

    overridePendingTransition(R.anim.detailsopen_detail_show, R.anim.detailsopen_list_hide);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_common, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    int id = item.getItemId();
    switch (id) {
      case android.R.id.home:
        finish();
        break;
    }
    return true;
  }

  @Override
  public void onEmojiSelected(int position) {
    if (emojiSetListener != null) {
      emojiSetListener.onEmojiSet(position);
    }
  }

  public void setEmojiSetListener(onEmojiSetListener onEmojiSetListener) {
    this.emojiSetListener = onEmojiSetListener;
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
            MedicineDetails.this.runOnUiThread(new Runnable() {
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

  public interface onEmojiSetListener {
    void onEmojiSet(int position);
  }
}
