package askargabit.currencytable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String LOG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;

    private static final String CURRENCY = "currency";
    private static final String FAVOURITE = "favourite";
    private static final String MAIN = "main";

    private static final String DATABASE_NAME = "YAHOO_DATA";

    private static final String name = "name";
    private static final String price = "price";
    private static final String symbol = "symbol";

    private static final String CREATE_TABLE_CURRENCY = "CREATE TABLE " + CURRENCY + "(" + name + " TEXT," + price + " TEXT," + symbol + " TEXT" + ")";
    private static final String CREATE_TABLE_FAVOURITE = "CREATE TABLE " + FAVOURITE + "(" + name + " TEXT" + ")";
    private static final String CREATE_TABLE_MAIN = "CREATE TABLE " + MAIN + "(" + name  + " TEXT" + ")";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CURRENCY);
        db.execSQL(CREATE_TABLE_FAVOURITE);
        db.execSQL(CREATE_TABLE_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CURRENCY);
        db.execSQL("DROP TABLE IF EXISTS " + FAVOURITE);
        db.execSQL("DROP TABLE IF EXISTS " + MAIN);
        onCreate(db);
    }

    public long CreateRow(CurrencyData currencyData, long[] tag_ids) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(name, currencyData.getName());
        values.put(price, currencyData.getPrice());
        values.put(symbol, currencyData.getSymbol());
        long todo_id = db.insert(CURRENCY, null, values);
        return todo_id;
    }

    public long CreateFav(String favname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(name, favname);
        long tag_id = db.insert(FAVOURITE, null, values);
        return tag_id;
    }

    public long CreateMain(String mainname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(name, mainname);
        long tag_id = db.insert(MAIN, null, values);
        return tag_id;
    }

    public void deleteMain() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + MAIN;
        db.execSQL(query);
    }

    public void deleteFav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(id.contains("USD"))
        {
            id = id.substring(4,7);
        }
        String query = "DELETE FROM " + FAVOURITE + " WHERE " + name + " LIKE '%" + id + "%'";
        db.execSQL(query);
    }

    public CurrencyData getData(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + CURRENCY + " WHERE " + name + " LIKE " + id;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        CurrencyData td = new CurrencyData();
        td.setName(c.getString(c.getColumnIndex(name)));
        td.setPrice((c.getString(c.getColumnIndex(price))));
        td.setSymbol(c.getString(c.getColumnIndex(symbol)));

        return td;
    }

    public List<CurrencyData> getAllCurrencyDATA() {
        List<CurrencyData> todos;
        todos = new ArrayList<CurrencyData>();
        String selectQuery = "SELECT  * FROM " + CURRENCY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int i = 0;
        if (c.moveToFirst()) {
            do {
                CurrencyData td = new CurrencyData();
                td.setName(c.getString(c.getColumnIndex(name)));
                td.setPrice(c.getString(c.getColumnIndex(price)));
                td.setSymbol(c.getString(c.getColumnIndex(symbol)));
                i++;
                todos.add(td);
                if(i == 188) {
                    break;
                }
            } while (c.moveToNext());
        }
        return todos;
    }

    public List<String> getallFavData()
    {
        List<String> todos;
        todos = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT  * FROM " + FAVOURITE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int i = 0;
        if (c.moveToFirst()) {
            do {
                i++;
                todos.add(c.getString(c.getColumnIndex(name)));
                if(i == 188) {
                    break;
                }
            } while (c.moveToNext());
        }
        return todos;
    }

    public String getMainData()
    {
        String todos = null;
        String selectQuery = "SELECT  * FROM " + MAIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                todos = c.getString(c.getColumnIndex(name));
            } while (c.moveToNext());
        }
        return todos;
    }

    public int updateCurrence(CurrencyData currencyData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(price, currencyData.getPrice());
        values.put(symbol, currencyData.getSymbol());

        return db.update(CURRENCY, values, name + " = ?",
                new String[] { String.valueOf(currencyData.getName()) });
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
