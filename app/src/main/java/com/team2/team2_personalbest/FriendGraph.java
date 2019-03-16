package com.team2.team2_personalbest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team2.team2_personalbest.FirebaseCloudMessaging.ChatRoomActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.team2.team2_personalbest.HomePage.isNumeric;

public class FriendGraph extends AppCompatActivity {

    Context context;
    ProgressDialog Dialog;
    String myName;
    String myEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_graph);
        context = this;

        //int friend_ID_string = getIntent().getInt("friend_ID");

        // Get my Data
        SharedPreferences sharedPreferences = getSharedPreferences("appname_prefs", MODE_PRIVATE);
        myEmail = sharedPreferences.getString("userID", "");
        myName = sharedPreferences.getString("user name", "");


        // Get Friend Data
        Bundle bundle = getIntent().getExtras();

        String friend_name;
        int friend_id;
        try {
            friend_name = bundle.getString("friend_name");
            friend_id = bundle.getInt("friend_id");
        } catch (NullPointerException e) {
            friend_id = 0;
        }

        FirebaseApp.initializeApp(this);

        Dialog = new ProgressDialog(this);
        Dialog.setMessage("Loading ");
        Dialog.show();

        new FillEntriesTask().execute(friend_id);
    }
    public void dismissDialog(){
        Dialog.hide();
    }



    private class FillEntriesTask extends AsyncTask<Integer, Void, List<BarEntry>> {

        @Override
        protected List<BarEntry> doInBackground(Integer... friends) {



            SharedPreferences userPref = context.getSharedPreferences("appname_prefs", 0);
            FirestoreUser user = new FirestoreUser(userPref.getString("user name", "Anton"), userPref.getString("userID", "aopanis@gmail.com"));
            int friend = friends[0];

//            List<Pair<Integer, Integer>> walks = user.getWalks(UserUtilities.emailToUniqueId(friend));
            List<Pair<Integer, Integer>> walks = user.getWalks(friend);

            //String friend = friends[0];

            List<BarEntry> entries = new ArrayList<>();
            for(int i = 0; i < 28; i++) {
                Pair<Integer, Integer> day = walks.get(i);
                if(day != null) {
                    Integer trackedSteps = day.first;
                    Integer untrackedSteps = day.second - trackedSteps;
                    entries.add(new BarEntry(28 - i, new float[]{untrackedSteps, trackedSteps}));
                } else {
                    entries.add(new BarEntry(28 - i, new float[]{0, 0}));
                }
            }

            return entries;

        }
        @Override
        protected void onPostExecute(List<BarEntry> entries) {
            generateBarChart(entries);
            dismissDialog();
        }
    }



    public void generateBarChart(List<BarEntry> entries) {
        CombinedChart chart = findViewById(R.id.chart);
        chart.setScaleEnabled(false);

        final ArrayList<String> xLabel = new ArrayList<>();
//        String[] days = DateHelper.getLastSevenWeekDays(DateHelper.getDayOfWeek());

        xLabel.add(DateHelper.dayDateToString(DateHelper.previousDay(27)));
        for(int i=1; i < 28; i++) {
            xLabel.add("");
        }

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceMin(.5f);
        xAxis.setSpaceMax(.5f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });

        BarDataSet barDataSet = new BarDataSet(entries, "Daily walking");
        int color1 = ContextCompat.getColor(this, R.color.colorAccent);
        int color2 = ContextCompat.getColor(this, R.color.colorPrimary);
        //int color3 = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        barDataSet.setColors(color1, color2);

        BarData barData = new BarData(barDataSet);


        CombinedData combinedData = new CombinedData();
        combinedData.setData((barData));
        combinedData.setData(generateLine());


        chart.setData(combinedData);
        chart.invalidate();
    }

    // @TODO fix goal line
    private LineData generateLine() {
        LineData data = new LineData();
        int goal = 5000;

        SharedPreferences sharedPreferences = getSharedPreferences("goal", MODE_PRIVATE);
        String newGoal = sharedPreferences.getString("newgoal", "");
        if(isNumeric(newGoal)) {
            goal = Integer.parseInt(newGoal);
        }

        ArrayList<Entry> lineEntries = new ArrayList<>();
        lineEntries.add(new Entry(0, goal));
        lineEntries.add(new Entry(28, goal));

        LineDataSet set = new LineDataSet(lineEntries, "Line Dataset");
        set.setLineWidth(2.5f);
        set.setCircleRadius(1f);

        data.addDataSet(set);

        return data;

    }

    public void sendMessage(View view){
        String TAG = ChatRoomActivity.class.getSimpleName();

        Toast toast = Toast.makeText(getApplicationContext(), "Message Sent",
                Toast.LENGTH_SHORT);
        toast.show();
        String COLLECTION_KEY = "chats";
        String MESSAGES_KEY = "messages";
        String FROM_KEY = "fromUserName";
        String TEXT_KEY = "text";
        String TIMESTAMP_KEY = "timestamp";
        String DOCUMENT_KEY;

        CollectionReference chat;

        String fromUserName;
        String toUserName;
        String fromUserEmail;
        String toUserEmail;
        int fromUserId;
        int toUserId;

        Bundle bundle = getIntent().getExtras();
        toUserName = bundle.getString("friend_name");
        toUserId = bundle.getInt("friend_id");
        toUserEmail = bundle.getString("friend_email");

        // Get my Data
        SharedPreferences sharedPreferences = getSharedPreferences("appname_prefs", MODE_PRIVATE);
        fromUserEmail = sharedPreferences.getString("userID", "");
        fromUserName = sharedPreferences.getString("user name", "");
        fromUserId = UserUtilities.emailToUniqueId(fromUserEmail);

        EditText messageView = findViewById(R.id.textView);
        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, fromUserName);
        newMessage.put(TEXT_KEY, messageView.getText().toString());
        DOCUMENT_KEY = Integer.toString(getChatID(toUserId, fromUserId));
        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGES_KEY);
        chat.add(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    public void goToChat(View view){
        //setContentView(R.layout.activity_friend_graph);
        SharedPreferences sharedPreferences = getSharedPreferences("popup", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("openedFromGraph", true).apply();
        // Retrieve username from previous activity
        Bundle bundle = getIntent().getExtras();
        String userName;
        int friend_id;
        try {
            //userName = bundle.getString("friend'sName");
            friend_id = bundle.getInt("friend_id");
        } catch (NullPointerException e) {
            friend_id = 0;
        }
        Intent intent = new Intent(this, ChatRoomActivity.class);
        // Pass username to next activity
        intent.putExtra("friend_id", friend_id);
        startActivity(intent);
        finish();
    }
    private int getChatID(int user1, int user2){
        if(user1 >= user2)
            return user1-user2;
        else
            return user2-user1;
    }


}
