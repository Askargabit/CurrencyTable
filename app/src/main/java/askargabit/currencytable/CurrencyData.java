package askargabit.currencytable;

/**
 * Created by ASKAR on 1-Oct-17.
 */

public class CurrencyData {

    String name;
    String price;
    String symbol;

    public CurrencyData()
    {   }

    public CurrencyData(String Cname, String Cprice, String Csymbol)
    {
        this.name = Cname;
        this.price = Cprice;
        this.symbol = Csymbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

}
