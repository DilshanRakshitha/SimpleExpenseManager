package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentCreateDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager{
    private Context context;
    public PersistentExpenseManager(Context context) {
        this.context=context;
        this.setup();

    }

    @Override
    public void setup()  {
        //Below is the implementation of database and tables by calling the sqlite quries directly.
        //SQLiteDatabase db = context.openOrCreateDatabase("200502U", Context.MODE_PRIVATE, null);
        //String sqlQ1 = "CREATE TABLE IF NOT EXISTS Account(AccountNumber VARCHAR PRIMARY KEY, BankName VARCHAR, AccountHolderName VARCHAR, Balance REAL);";
        //String sqlQ2 = "CREATE TABLE IF NOT EXISTS BankTransaction(TransactionID INTEGER PRIMARY KEY Autoincrement, AccountNo VARCHAR, ExpenseType INT, Amount REAL, Date DATE, FOREIGN KEY (AccountNO) REFERENCES Account(AccountNumber));";

        //db.execSQL(sqlQ1);
        //db.execSQL(sqlQ2);

        //----------------------

        //Fllowing is the implementation of the database using SQLiteOpenHelper class
        //References: Android developers website-https://developer.android.com/training/data-storage/sqlite#java
        //References: SQLiteOpenHelper class-https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
        //SQLiteDatabase db = context.openOrCreateDatabase("200502U", Context.MODE_PRIVATE, null);
        PersistentCreateDB dbHelper = new PersistentCreateDB(context);
        PersistentCreateDB.AccountTable obj = dbHelper. new AccountTable();
        SQLiteDatabase db = obj.getWritableDatabase();

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(db);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(db);
        setAccountsDAO(persistentAccountDAO);



    }
}
