package com.example.jnubus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

public class TypeActivity extends AppCompatActivity implements View.OnClickListener {


    private CardView cvStudent;
    private CardView cvTeacher;
    private CardView cvStuff;
    private CardView cvStart;
    private CardView [] cardViews;

    private TextView tvStudent;
    private TextView tvTeacher;
    private TextView tvStuff;
    private TextView tvStart;
    private TextView [] textViews;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private int prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor            = sharedPreferences.edit();

        cvStudent = findViewById(R.id.cvStudent);
        cvTeacher = findViewById(R.id.cvTeacher);
        cvStuff   = findViewById(R.id.cvStuff);
        cvStart   = findViewById(R.id.cvStart);

        tvStudent = findViewById(R.id.tvStudent);
        tvTeacher = findViewById(R.id.tvTeacher);
        tvStuff   = findViewById(R.id.tvStuff);
        tvStart   = findViewById(R.id.tvStart);

        cardViews = new CardView[] {cvStudent, cvTeacher, cvStuff};
        textViews = new TextView[] {tvStudent, tvTeacher, tvStuff};
        prev      = 0;
        for(int i=0; i<cardViews.length; i++)makeNonSelected(cardViews[i], textViews[i]);
        clearPrev(0);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.cvStart) {
            editor.putInt("type", prev+1);
            editor.commit();
            goHome();
            return;
        }
        for(int i=0; i<cardViews.length; i++)
            if(cardViews[i].getId() == viewId)clearPrev(i);
    }

    private void clearPrev (int now) {
        makeNonSelected(cardViews[prev], textViews[prev]);
        makeSelected(cardViews[now], textViews[now]);
        prev = now;
    }

    private void makeNonSelected (CardView cardView, TextView textView) {
        cardView.setCardBackgroundColor(getBackgroundColor(this, R.color.colorBackground));
        cardView.setRadius(0.0f);
        cardView.setCardElevation(0.0f);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void makeSelected (CardView cardView, TextView textView) {
        cardView.setCardBackgroundColor(getBackgroundColor(this, R.color.colorWhite));
        cardView.setRadius(4.0f);
        cardView.setCardElevation(4.0f);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_black_24dp, 0);
    }

    private int getBackgroundColor (Context context, int i) {
        if (Build.VERSION.SDK_INT >= 23)return context.getColor(i);
        return context.getResources().getColor(i);
    }

    private void goHome (){
        startActivity( new Intent(TypeActivity.this, HomeActivity.class) );
    }
}
