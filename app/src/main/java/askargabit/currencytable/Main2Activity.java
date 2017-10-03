package askargabit.currencytable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {

    private ListView listView;
    ArrayList<HashMap<String, String>> currencylist;
    DatabaseHelper db;

    @Override
    public void onBackPressed() {
        Intent a = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        db = new DatabaseHelper(getApplicationContext());

        currencylist = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listview2);
        registerForContextMenu(listView);
        ArrayList<CurrencyData> Yahoo = (ArrayList<CurrencyData>) db.getAllCurrencyDATA();
        for(int i = 0; i < Yahoo.size(); i++) {
            String currencyname = Yahoo.get(i).getName();
            String currencyprice = Yahoo.get(i).getPrice();
            String currencysymbol = Yahoo.get(i).getSymbol();

            HashMap<String, String> currencydata = new HashMap<>();
            currencydata.put("name", currencyname);
            currencydata.put("price", currencyprice);
            currencydata.put("symbol", currencysymbol);

            currencylist.add(currencydata);
        }

        ListAdapter adapter = new SimpleAdapter(Main2Activity.this, currencylist, R.layout.listview, new String[]{"name", "price", "symbol"}, new int[]{R.id.currencyname, R.id.currencyprice, R.id.currencysymbol});
        listView.setAdapter(adapter);
        db.closeDB();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contexmenu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.myitem1)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String toast = Integer.toString(info.position);
            ArrayList<CurrencyData> Yahoo = (ArrayList<CurrencyData>) db.getAllCurrencyDATA();
            Toast.makeText(Main2Activity.this, Yahoo.get(info.position).getName() , Toast.LENGTH_SHORT).show();
            long id = db.CreateFav(Yahoo.get(info.position).getName());
        }
        return super.onContextItemSelected(item);
    }


}
