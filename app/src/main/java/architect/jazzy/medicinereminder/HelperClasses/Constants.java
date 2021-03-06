package architect.jazzy.medicinereminder.HelperClasses;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ScaleDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.text.Html;
import android.text.Spanned;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import architect.jazzy.medicinereminder.MedicalAssistant.Parsers.FeedParser;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 25-Mar-15.
 */
public class Constants {

  public static final String BUNDLE_SELECTED_NEWS = "selected";
  public static final String BUNDLE_MEDICINE_TAG = "MEDICINE_NAME";
  public static final String MEDICINE_POSITION = "medicinePosition";
  public static final String INPUT_SHARED_PREFS = "TimePrefs";
  public static final String BEFORE_BREAKFAST_HOUR = "bbh";
  public static final String BEFORE_BREAKFAST_MINUTE = "bbm";
  public static final String AFTER_BREAKFAST_HOUR = "abh";
  public static final String AFTER_BREAKFAST_MINUTE = "abm";
  public static final String BEFORE_LUNCH_HOUR = "blh";
  public static final String BEFORE_LUNCH_MINUTE = "blm";
  public static final String AFTER_LUNCH_HOUR = "alh";
  public static final String AFTER_LUNCH_MINUTE = "alm";
  public static final String BEFORE_DINNER_HOUR = "bdh";
  public static final String BEFORE_DINNER_MINUTE = "bdm";
  public static final String AFTER_DINNER_HOUR = "adh";
  public static final String AFTER_DINNER_MINUTE = "adm";
  public static final String MEDICINE_NAME_LIST = "medicineNameList";
  public static final String IS_24_HOURS_FORMAT = "is24hrs";
  public static final String INDEFINITE_SCHEDULE = "indefinite";
  public static final String SEARCH_FILE_NAME = "tmpMR_Search.txt";
  public static final String BUNDLE_SEARCH_TERM = "searchTerm";
  public static final String BUNDLE_WEB_DOCUMENT = "webDocument";
  public static final String BUNDLE_DOCTOR = "doctorBundle";
  public static final String SETTING_PREF = "settings";
  public static final String THEME_COLOR = "colorSelected";
  public static final String INTERNAL_PREF = "internal";
  public static final String CUSTOM_TIME__ALARM_ID_LAST = "stopAlarm";
  public static final String NEWS_LIST_LOADED = "loaded";
  public static final String SEARCH_RESULT = "searchList";
  public static ArrayList<Integer> backgroundImages = new ArrayList<>(Arrays.asList(R.drawable.ayurvedic,
      R.drawable.doc, R.drawable.fruits,
      R.drawable.leaf_pills,
      R.drawable.pill_fruits,
      R.drawable.pills));
  private static int current = 0;


  public Constants() {
    super();
  }

  public static int getThemeColor(Context context) {
//    return context.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE)
//        .getInt(Constants.THEME_COLOR, context.getResources().getColor(R.color.themeColorDefault));
    return context.getResources().getColor(R.color.themeColorDefault);
  }

  private static int getFAB(int color) {
//    float[] hsv = new float[3];
//    Color.colorToHSV(color, hsv);
//    hsv[2] = hsv[2] > 0.5 ? hsv[2] : hsv[2] + 0.5f;
//    hsv[0] = (hsv[0] + 180) % 360;
//    return Color.HSVToColor(hsv);
    return Color.parseColor("#238842");
  }

  public static int getFABColor(Context context) {
    return getFAB(getThemeColor(context));
  }

  public static int getFABColor(int color) {
    return getFAB(color);
  }

  public static ColorStateList getFabBackground(Context context) {
    return new ColorStateList(new int[][]{new int[]{0}}, new int[]{getFABColor(context)});
  }

  /**
   * Check if phone is connected to internet
   */
  public static boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager
        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  public static Spanned getSuggestionText(String o, String suggestion) {
    return Html.fromHtml("Nothing found for <i>" + o + "</i>. Showing result for <i><b>" + suggestion + "</b></i>");
  }

  /**
   * Image Size Reducing
   */
  public static Bitmap getScaledBitmap(String picturePath, int width, int height) {
    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
    sizeOptions.inJustDecodeBounds = true;
//        if(inputStream == null) {
    BitmapFactory.decodeFile(picturePath, sizeOptions);
//        }else{
//            BitmapFactory.decodeStream(inputStream, null, sizeOptions);
//        }
    int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

    sizeOptions.inJustDecodeBounds = false;
    sizeOptions.inSampleSize = inSampleSize;

//        if(inputStream == null) {
    return BitmapFactory.decodeFile(picturePath, sizeOptions);
//        }else{
//            return BitmapFactory.decodeStream(inputStream, null, sizeOptions);
//        }
//        Log.e("Constants","Get scaled Bitmap: "+bitmap.toString());
  }
//    public static Bitmap getScaledBitmap(String picturePath, int width, int height) {
//        return getScaledBitmap(picturePath, null, width, height);
//    }

  public static Bitmap getScaledBitmap(Uri uri, Context context, int width, int height) {
    Bitmap bitmap;
    try {
      bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
//        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
//        sizeOptions.inJustDecodeBounds = true;
//        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);
//
//        sizeOptions.inJustDecodeBounds = false;
//        sizeOptions.inSampleSize = inSampleSize;

//        int width = bm.getWidth();
//        int height = bm.getHeight();
    float scaleWidth = ((float) width) / bitmap.getWidth();
    float scaleHeight = ((float) height) / bitmap.getHeight();
    // CREATE A MATRIX FOR THE MANIPULATION
    Matrix matrix = new Matrix();
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight);

    // "RECREATE" THE NEW BITMAP
    Bitmap resizedBitmap = Bitmap.createBitmap(
        bitmap, 0, 0, width, height, matrix, false);
    bitmap.recycle();
    return resizedBitmap;
  }

  /**
   * Scale the drawables in editText
   *
   * @param context
   * @param editText
   * @param drawableImage
   */
  public static void scaleEditTextImage(Context context, final EditText editText, @DrawableRes int drawableImage, int color, final boolean scale, String leftRight) {
    final double IMAGE_SCALE_RATIO = 0.6;
    final ScaleDrawable icon = new ScaleDrawable(context.getResources().getDrawable(drawableImage).mutate(), Gravity.CENTER, 1F, 1F) {
      @Override
      public int getIntrinsicHeight() {
        return scale ? ((int) (editText.getHeight() * IMAGE_SCALE_RATIO)) : super.getIntrinsicHeight();
      }

      @Override
      public int getIntrinsicWidth() {
        return scale ? ((int) (editText.getHeight() * IMAGE_SCALE_RATIO)) : super.getIntrinsicWidth();
      }
    };
    icon.setLevel(10000);
    if (leftRight.equalsIgnoreCase("right")) {
      editText.setCompoundDrawables(null, null, icon, null);
    } else {
      editText.setCompoundDrawables(icon, null, null, null);
    }
    editText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        try {
          editText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    editText.getCompoundDrawables()[2].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
  }

  public static void scaleEditTextImage(Context context, final EditText editText, @DrawableRes int drawableImage) {
    scaleEditTextImage(context, editText, drawableImage, context.getResources().getColor(R.color.editTextIconColor));
  }

  public static void scaleEditTextImage(Context context, final EditText editText, @DrawableRes int drawableImage, int color) {
    scaleEditTextImage(context, editText, drawableImage, color, true, "right");
  }

  public static void scaleEditTextImage(Context context, final EditText editText, @DrawableRes int drawableImage, int color, String leftRight) {
    scaleEditTextImage(context, editText, drawableImage, color, true, leftRight);
  }

  public static void scaleEditTextImage(Context context, final EditText editText, @DrawableRes int drawableImage, int color, boolean scale) {
    scaleEditTextImage(context, editText, drawableImage, color, scale, "right");
  }


  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
  }

  /**
   * To convert hashmap to post request for sending to server
   *
   * @param params the hashmap
   * @return post request string
   * @throws UnsupportedEncodingException
   */
  public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (first)
        first = false;
      else
        result.append("&");

      result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
      result.append("=");
      result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
    }

    return result.toString();
  }

  public static void setColoredProgressText(final TextView text, int[] colors, final int delay) {
    if (colors == null) {
      colors = new int[]{Color.parseColor("#ff2f8b2a"), Color.parseColor("#ff295db3"), Color.parseColor("#fff45f30"), Color.BLACK};
    }
    final int[] finalColors = colors;

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
    ObjectAnimator animator = ObjectAnimator.ofInt(text, "textColor", finalColors[0], finalColors[1], finalColors[2], finalColors[3]);
    animator.setRepeatCount(20);
    animator.setDuration((delay * finalColors.length + delay) * 20);
    animator.start();
//
//                current = getNextInt(current, finalColors);
//                text.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        text.setTextColor(finalColors[current]);
//                    }
//                });
//            }
//        }, delay);

  }

  private static int getNextInt(int current, int[] ints) {
    if (current == ints.length - 1) {
      return 0;
    } else {
      current++;
      return current;
    }
  }

  public static class MedicineNetUrls {
    public static final ArrayList<Pair<String, String>> urls = new ArrayList<>();

    private static void setup() {
      urls.clear();
      urls.add(Pair.create("Daily News", FeedParser.feedUrl));
      urls.add(Pair.create("Allergies", "http://www.medicinenet.com/rss/general/allergies.xml"));
      urls.add(Pair.create("Alzheimer's Disease", "http://www.medicinenet.com/rss/general/alzheimers.xml"));
      urls.add(Pair.create("Arthritis", "http://www.medicinenet.com/rss/general/arthritis.xml"));
      urls.add(Pair.create("Asthma", "http://www.medicinenet.com/rss/general/asthma.xml"));
      urls.add(Pair.create("Cancer", "http://www.medicinenet.com/rss/general/cancer.xml"));
      urls.add(Pair.create("Cholesterol", "http://www.medicinenet.com/rss/general/cholesterol.xml"));
      urls.add(Pair.create("Chronic Pain", "http://www.medicinenet.com/rss/general/chronic_pain.xml"));
      urls.add(Pair.create("Cold & Flu", "http://www.medicinenet.com/rss/general/cold_and_flu.xml"));
      urls.add(Pair.create("Depression", "http://www.medicinenet.com/rss/general/depression.xml"));
      urls.add(Pair.create("Diabetes", "http://www.medicinenet.com/rss/general/diabetes.xml"));
      urls.add(Pair.create("Diet", "http://www.medicinenet.com/rss/general/diet_and_weight_management.xml"));
      urls.add(Pair.create("Digestion", "http://www.medicinenet.com/rss/general/digestion.xml"));
      urls.add(Pair.create("Eyesight", "http://www.medicinenet.com/rss/general/eyesight.xml"));
      urls.add(Pair.create("Hearing", "http://www.medicinenet.com/rss/general/hearing.xml"));
      urls.add(Pair.create("Heart", "http://www.medicinenet.com/rss/general/heart.xml"));
      urls.add(Pair.create("High Blood Pressure", "http://www.medicinenet.com/rss/general/high_blood_pressure.xml"));
      urls.add(Pair.create("HIV", "http://www.medicinenet.com/rss/general/hiv.xml"));
      urls.add(Pair.create("Infectious Disease", "http://www.medicinenet.com/rss/general/infectious_disease.xml"));
      urls.add(Pair.create("Lung Conditions", "http://www.medicinenet.com/rss/general/lung_conditions.xml"));
      urls.add(Pair.create("Medications", "http://www.medicinenet.com/rss/general/medications.xml"));
      urls.add(Pair.create("Menopause", "http://www.medicinenet.com/rss/general/menopause.xml"));
      urls.add(Pair.create("Men's Health", "http://www.medicinenet.com/rss/general/mens_health.xml"));
      urls.add(Pair.create("Mental Health", "http://www.medicinenet.com/rss/general/mental_health.xml"));
      urls.add(Pair.create("Migraine", "http://www.medicinenet.com/rss/general/migraine.xml"));
      urls.add(Pair.create("Neurology", "http://www.medicinenet.com/rss/general/neurology.xml"));
      urls.add(Pair.create("Oral Health", "http://www.medicinenet.com/rss/general/oral_health.xml"));
      urls.add(Pair.create("Kids Health", "http://www.medicinenet.com/rss/general/kids_health.xml"));
      urls.add(Pair.create("Pregnancy", "http://www.medicinenet.com/rss/general/pregnancy.xml"));
      urls.add(Pair.create("Prevention & Wellness", "http://www.medicinenet.com/rss/general/prevention_and_wellness.xml"));
      urls.add(Pair.create("Senior Health", "http://www.medicinenet.com/rss/general/senior_health.xml"));
      urls.add(Pair.create("Sexual Health", "http://www.medicinenet.com/rss/general/sexual_health.xml"));
      urls.add(Pair.create("Skin", "http://www.medicinenet.com/rss/general/skin.xml"));
      urls.add(Pair.create("Sleep", "http://www.medicinenet.com/rss/general/sleep.xml"));
      urls.add(Pair.create("Thyroid", "http://www.medicinenet.com/rss/general/thyroid.xml"));
      urls.add(Pair.create("Travel Health", "http://www.medicinenet.com/rss/general/travel_health.xml"));
      urls.add(Pair.create("Women's Health", "http://www.medicinenet.com/rss/general/womens_health.xml"));
    }

    public static ArrayList<Pair<String, String>> getUrls() {
      setup();
      return urls;
    }
  }

}
