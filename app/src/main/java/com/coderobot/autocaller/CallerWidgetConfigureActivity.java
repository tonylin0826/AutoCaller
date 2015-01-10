package com.coderobot.autocaller;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.coderobot.autocaller.com.coderobot.autocaller.views.ImageCutter;

/**
 * The configuration screen for the {@link CallerWidget CallerWidget} AppWidget.
 */
public class CallerWidgetConfigureActivity extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int SELECT_PHOTO = 1000;
    private static final String TAG = "CallerWidgetConfigureActivity";
    private EditText mEditPhoneEdit;
    private ImageCutter mImageCutter;
    private Button mButtonAddWidget;
    private Button mBtnAddImage;


    public CallerWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.caller_widget_configure);
        setViews();

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageCutter.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO: {
                Uri photoUri = imageReturnedIntent.getData();
                String imageFilePath = getImageFilePath(photoUri);
                int imageOrientation = getOrientation(photoUri);

                if (imageFilePath == null || imageFilePath.length() == 0) break;

                Bitmap mBitMap = loadBitmapFromFile(imageFilePath, imageOrientation);

                mImageCutter.setImageBitmap(mBitMap);
                Point p = mImageCutter.getBitmapSize();

                log("p.x : " + p.x);
                log("p.y : " + p.y);

                mImageCutter.getLayoutParams().width = p.x;
                mImageCutter.getLayoutParams().height = 50;
                mImageCutter.invalidate();
                break;
            }
            default:
                break;
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = CallerWidgetConfigureActivity.this;
            switch (v.getId()) {
                case R.id.add_button: {
                    CallerDB db = CallerDB.getInstance(context);
                    PhoneSet phoneSet = new PhoneSet(mAppWidgetId, mEditPhoneEdit.getText().toString(), "none");

                    if (phoneSet.phoneNum == null || phoneSet.phoneNum.length() == 0) {
                        return;
                    }

                    db.updatePhoneNumber(phoneSet);
                    db.printDB();
                    // It is the responsibility of the configuration activity to update the app widget
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    CallerWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, phoneSet);

                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                    break;
                }
                case R.id.add_image_button: {
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("image/*");
                    startActivityForResult(i, SELECT_PHOTO);
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void setViews() {
        mEditPhoneEdit = (EditText) findViewById(R.id.edit_phone_num);
        mButtonAddWidget = (Button) findViewById(R.id.add_button);
        mBtnAddImage = (Button) findViewById(R.id.add_image_button);
        mImageCutter = (ImageCutter) findViewById(R.id.image_cutter_contact);

        mButtonAddWidget.setOnClickListener(mOnClickListener);
        mBtnAddImage.setOnClickListener(mOnClickListener);
    }

    private Bitmap loadBitmapFromFile(String path, int orientation) {
        log("loadBitmapFromFile : " + path);
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        bitmap = BitmapFactory.decodeFile(path, options);

        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    private String getImageFilePath(Uri photoUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
        if (cursor.getCount() != 1) {
            return "";
        }

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }

    private int getOrientation(Uri photoUri) {
        Cursor cursor = getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }

}



