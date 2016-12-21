package com.swm.app.superrun;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.swm.body.PersonalModule;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GenderAndAgeActivity extends SwmBaseActivity implements View.OnClickListener
                                            , DatePickerDialog.OnDateSetListener{
    private Button mMale;
    private Button mFemale;
    private Button mSwmCta;
    private TextView mBirthday;
    private DatePickerDialog mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_and_age);
        mBirthday = (TextView) findViewById(R.id.swm_birthday);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        mBirthday.setText(year+ "/" + month + "/" + day);
        findViewById(R.id.swm_pick_day).setOnClickListener(this);
        mMale = (Button)findViewById(R.id.swm_male);
        mMale.setOnClickListener(this);
        mFemale = (Button) findViewById(R.id.swm_female);
        mFemale.setOnClickListener(this);
        mSwmCta = (Button) findViewById(R.id.swm_cta);
        mSwmCta.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.swm_male) {
            PersonalModule.getIns().setGender(PersonalModule.GENDER_MALE);
            mMale.setBackground(getResources().getDrawable(R.drawable.swm_cta_pressed_background));
            mFemale.setBackground(getResources().getDrawable(R.drawable.swm_disabled_button));
        }

        if (v.getId() == R.id.swm_female) {
            PersonalModule.getIns().setGender(PersonalModule.GENDER_FEMALE);
            mMale.setBackground(getResources().getDrawable(R.drawable.swm_disabled_button));
            mFemale.setBackground(getResources().getDrawable(R.drawable.swm_cta_pressed_background));
        }

        if(v.getId() == R.id.swm_cta) {

            Calendar calendar = Calendar.getInstance();
            int thisYear = calendar.get(Calendar.YEAR);
            String birthday = (String) mBirthday.getText();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date date = sdf.parse(birthday);
                calendar.setTime(date);
                int birthYear = calendar.get(Calendar.YEAR);
                int age = thisYear - birthYear;
                PersonalModule.getIns().setAge(age);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, SuperRunActivity.class);
            startActivity(intent);
            finish();
        }

        if (v.getId() == R.id.swm_pick_day) {
            String dateString = (String) mBirthday.getText();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            try {
                Date date = sdf.parse(dateString);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDatePicker = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            mDatePicker.show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mBirthday.setText(year + "/" + (month + 1)+ "/" + dayOfMonth);
    }
}
