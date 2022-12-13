package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    //References: Android developers website-https://developer.android.com/training/data-storage/sqlite#java
    //References: SQLiteOpenHelper class-https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
    private SQLiteDatabase db;
    public PersistentTransactionDAO(SQLiteDatabase db){this.db=db;}
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){

        //write the log only if a transaction happens.
        //That is we should check the available balance is sufficient to draw back the money or not.
        //create a object of PersistentAccountDAO and using the getaccount method get the status of the account relevant to the account number
        //and check whether the balance is greater than or equal to the amount
        PersistentAccountDAO temp= new PersistentAccountDAO(db);
        Double balance=0.0;
        try {
            balance = (temp).getAccount(accountNo).getBalance();
        }
        catch(Exception e){}
        //if balance is greater than or equal to the amount expense or income can happen.
        //is balace is less than the amount only income can be happen.
        if( (balance>=amount) | ((balance<amount) & (expenseType == ExpenseType.INCOME)) ) {
            int num = 0;
            if (expenseType == ExpenseType.EXPENSE) {
                num = 0;
            } else {
                num = 1;
            }
            ContentValues values = new ContentValues();
            values.put("AccountNo", accountNo);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                values.put("Date", (new SimpleDateFormat("dd-MM-yyyy")).format(date));
            }

            values.put("ExpenseType", num);
            values.put("Amount", amount);

            db.insert("BankTransaction", null, values);

        }
    }

    public List<Transaction> getAllTransactionLogs() {

        List<Transaction> transactions = new ArrayList<>();

        //raw query to get account information from the database table
        String query = "SELECT Date, AccountNo, Expensetype, Amount from BankTransaction";
        //query is operated on the database table and cursor is iterated.
        //this cursor is iterated to access the each column value.
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        try {
            do {
                String temp = cursor.getString(0);
                Date date = new Date();
                try {
                    date = (new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())).parse(temp);
                } catch (Exception e) {
                }
                String accountNumber = cursor.getString(1);
                ExpenseType expenseType = null;
                if (cursor.getInt(2) == 0) {
                    expenseType = ExpenseType.EXPENSE;
                } else if (cursor.getInt(2) == 1) {
                    expenseType = ExpenseType.INCOME;
                }
                double amount = cursor.getDouble(3);

                //create transaction object for each column values.
                Transaction transaction = new Transaction(date, accountNumber, expenseType, amount);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }catch(Exception e){}
        cursor.close();
        return transactions;

    }

    public List<Transaction> getPaginatedTransactionLogs(int limit){

        //get specific number of transactions using the getAllTransactionLogs method
        List temp = this.getAllTransactionLogs();
        int size = temp.size();
        if (size <= limit) {
            return temp;
        }

        return temp.subList(size - limit, size);

    }

}
