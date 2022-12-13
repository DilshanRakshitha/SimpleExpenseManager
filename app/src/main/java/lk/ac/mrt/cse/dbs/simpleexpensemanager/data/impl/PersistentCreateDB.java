package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistentCreateDB {
    //References: Android developers website-https://developer.android.com/training/data-storage/sqlite#java
    //References: SQLiteOpenHelper class-https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper

    Context context;
    public PersistentCreateDB(Context context){this.context=context;}

    //Create two tables as Account and BAnkTransaction
    public class AccountTable extends SQLiteOpenHelper{

        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME = "Account";
        public static final String AccNumCol = "AccountNumber";
        public static final String Balance = "Balance";
        public static final String BankName = "BankName";
        public static final String AccHolderName = "AccountHolderName";
        public static final String TABLE_NAME_Transaction = " BankTransaction ";
        public static final String DateCol_Transaction = "Date";
        public static final String AccountNumCol_Transaction = "AccountNo";
        public static final String ExpenseTypeCol_Transaction = "ExpenseType";
        public static final String AmountCol_Transaction = "Amount";

        public AccountTable() {
            super(context, "200502U", null, DATABASE_VERSION);

        }
        public void onCreate(SQLiteDatabase db) {

            //Query to create Account table
            final String SQL_CREATE_ENTRIES_Account =
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                            AccNumCol + " VARCHAR PRIMARY KEY, " +
                            BankName + " VARCHAR, " +
                            AccHolderName + " VARCHAR, " +
                            Balance + " REAL); ";

            //Query to create Transaction table
            final String SQL_CREATE_ENTRIES_Transaction =

                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_Transaction + " ( " +
                            " TransactionID INTEGER PRIMARY KEY Autoincrement, " +
                            AccountNumCol_Transaction + " VARCHAR," +
                            ExpenseTypeCol_Transaction + " INT," +
                            AmountCol_Transaction + " REAL, " +
                            DateCol_Transaction + " DATE," +
                            " FOREIGN KEY (AccountNo) REFERENCES Account(AccountNumber)); ";

            db.execSQL(SQL_CREATE_ENTRIES_Account);
            db.execSQL(SQL_CREATE_ENTRIES_Transaction);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This is implemented in case of there is any change in the schema of the database.
            final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + TABLE_NAME;
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
