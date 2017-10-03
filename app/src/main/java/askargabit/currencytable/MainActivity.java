package askargabit.currencytable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    ListView listView;
    DatabaseHelper db;
    Button button;
    TextView textView;
    TextView textView2;
    EditText edittext;

    private static String url = "https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";

    ArrayList<HashMap<String, String>> currencylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currencylist = new ArrayList<>();
        db = new DatabaseHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.listview);
        button = (Button) findViewById(R.id.button);
        registerForContextMenu(listView);
        textView = (TextView) findViewById(R.id.textview);
        edittext = (EditText) findViewById(R.id.edittext);
        textView2 = (TextView) findViewById(R.id.textview2);
        Long ctime = System.currentTimeMillis()/1000;
        String MyPREFERENCES = "time";

        SharedPreferences sPrefs = getSharedPreferences("MyPrefs", MODE_APPEND);
        SharedPreferences.Editor editor = sPrefs.edit();
        String bCheck = sPrefs.getString(MyPREFERENCES, null);

        if (bCheck == null) {
            String time = Long.toString(ctime);
            new GetCurrencyInfo().execute();
            Refresh();
            editor.putString(MyPREFERENCES, time).apply();
        }
        else if(ctime - Long.parseLong(sPrefs.getString(MyPREFERENCES, null)) > 43200)
        {
            new GetCurrencyInfo().execute();
            Refresh();
            String time = ctime.toString();
            editor.putString(MyPREFERENCES, time).apply();
        }
        else if(ctime - Long.parseLong(sPrefs.getString(MyPREFERENCES, null)) < 43200)
        {
            Refresh();
        }
        else
        {
            Log.e("Remove" , ctime.toString());
            Log.e("Remove" , "4");
            new GetCurrencyInfo().execute();
            String time = Long.toString(ctime);
            editor.putString(MyPREFERENCES, time).apply();
            Refresh();
        }
        db.closeDB();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(a);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
               String edit = edittext.getText().toString();
                if(edit.matches(""))
                {
                    Toast.makeText(MainActivity.this, "Please, enter amount you want to convert.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    double editnumber = Double.parseDouble(edit);
                    HashMap<String, String> currencydata = (HashMap<String, String>) adapter.getItemAtPosition(position);
                    String View = currencydata.get("name");
                    String Main = db.getMainData();
                    int curindex = 0;
                    ArrayList<CurrencyData> Yahoo = (ArrayList<CurrencyData>) db.getAllCurrencyDATA();
                    for(int j = 0; j < Yahoo.size(); j++)
                    {
                        if(Objects.equals(Yahoo.get(j).getName(), Main))
                        {
                            curindex = j;
                            break;
                        }
                    }
                    double maincurprice = Double.parseDouble(Yahoo.get(curindex).getPrice());
                    double price = Double.parseDouble(currencydata.get("price"));
                    double result = price * editnumber/maincurprice;
                    textView2.setText(String.valueOf(result));
                }


            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu2, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.myitem22)
        {
            long id = db.CreateFav(db.getMainData());
            db.deleteMain();
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            ArrayList<String> Yahooo = (ArrayList<String>) db.getallFavData();
            long i = db.CreateMain(Yahooo.get(info.position));
            String textV = "";
            int curindex = 0;
            ArrayList<CurrencyData> Yahoo = (ArrayList<CurrencyData>) db.getAllCurrencyDATA();
            for(int j = 0; j < Yahoo.size(); j++)
            {
                if(Objects.equals(Yahoo.get(j).getName(), Yahooo.get(info.position)))
                {
                    curindex = j;
                    break;
                }
            }
            textV = "" + Yahoo.get(curindex).getName() + " Price:" + Yahoo.get(curindex).getPrice() + "Symbol:" + Yahoo.get(curindex).getSymbol();
            textView.setText(textV);
        }
        else if(item.getItemId() == R.id.myitem33)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            List<String> Yahoo = db.getallFavData();
            String id = Yahoo.get(info.position);
            if(Yahoo.isEmpty())
            {
                Log.e("Remove" , "Empty");
            }
            db.deleteFav(id);
            Toast.makeText(MainActivity.this, "Removed from Favourites" , Toast.LENGTH_SHORT).show();
            Refresh();
        }
        return super.onContextItemSelected(item);
    }

    public void Refresh()
    {
            ArrayList<CurrencyData> Yahoo = (ArrayList<CurrencyData>) db.getAllCurrencyDATA();
            List<String> FavName = db.getallFavData();
            currencylist.clear();
            for (int i = 0; i < FavName.size(); i++) {
                int curindex = 0;
                for (int j = 0; j < Yahoo.size(); j++) {
                    if (Objects.equals(Yahoo.get(j).getName(), FavName.get(i))) {
                        curindex = j;
                        break;
                    }
                }

                String currencyname = Yahoo.get(curindex).getName();
                String currencyprice = Yahoo.get(curindex).getPrice();
                String currencysymbol = Yahoo.get(curindex).getSymbol();

                HashMap<String, String> currencydata = new HashMap<>();
                currencydata.put("name", currencyname);
                currencydata.put("price", currencyprice);
                currencydata.put("symbol", currencysymbol);

                currencylist.add(currencydata);
            }
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, currencylist, R.layout.listview, new String[]{"name", "price", "symbol"}, new int[]{R.id.currencyname, R.id.currencyprice, R.id.currencysymbol});
            listView.setAdapter(adapter);
    }

    private class GetCurrencyInfo extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Updating data");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler yahooCur = new HttpHandler();
            String json = yahooCur.makeServiceCall(url);

            if(json != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(json).getJSONObject("list");
                    JSONArray jsonArray = jsonObject.getJSONArray("resources");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject temp = jsonArray.getJSONObject(i);

                        String currencyname = temp.getJSONObject("resource").getJSONObject("fields").getString("name");
                        String currencyprice = temp.getJSONObject("resource").getJSONObject("fields").getString("price");
                        String currencysymbol = temp.getJSONObject("resource").getJSONObject("fields").getString("symbol");

                        CurrencyData currencyData = new CurrencyData(currencyname, currencyprice, currencysymbol);
                        long id = db.CreateRow(currencyData, new long[]{i});
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });}
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
             if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }

}
