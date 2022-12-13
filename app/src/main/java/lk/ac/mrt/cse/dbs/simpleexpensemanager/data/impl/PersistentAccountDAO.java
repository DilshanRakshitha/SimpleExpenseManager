package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    //References: Android developers website-https://developer.android.com/training/data-storage/sqlite#java
    //References: SQLiteOpenHelper class-https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
    private SQLiteDatabase db;

    public PersistentAccountDAO(SQLiteDatabase db){
        this.db=db;
    }


    @Override
    public  List<String> getAccountNumbersList(){

        //List of column names we want to project
        String[] projection = {
                "AccountNumber"
        };

        //Query to get data, cursor is returned hence we can iterate and separate data.
        Cursor cursor = db.query(
                "Account",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        //This array list is to keep the accountNumbers
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow("AccountNumber"));
            itemIds.add(itemId);
        }
        cursor.close();
        return itemIds;
    }

    @Override
    public List<Account> getAccountsList(){

        //List of column names to project
        String[] projection = {
                "AccountNumber",
                "BankName",
                "Balance",
                "AccountHolderName"
        };


        Cursor cursor = db.query(
                "Account",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        //create a set of accounts and take data from the database and put them into each account object and those objects a re stored in the database.
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String accNum = cursor.getString(
                    cursor.getColumnIndexOrThrow("AccountNumber"));
            String bankName = cursor.getString(
                    cursor.getColumnIndexOrThrow("BankName"));
            String accHolderName = cursor.getString(
                    cursor.getColumnIndexOrThrow("AccountHolderName"));
            Long balance = cursor.getLong(
                    cursor.getColumnIndexOrThrow("Balance"));
            Account newAcc = new Account(accNum, bankName, accHolderName, balance);
            itemIds.add(newAcc);
        }
        cursor.close();
        return itemIds;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException{

        //list of the column name to be projected.
        String[] projection = {
                "AccountNumber",
                "BankName",
                "Balance",
                "AccountHolderName"
        };

        // column name to where cluase to be applied
        String selection = "AccountNumber" + " = ?";
        // contrain to where cluse to be applied
        String[] selectionArgs = { accountNo };


        Cursor cursor = db.query(
                "Account",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        //one account object will be created
        Account newAcc=null;
        while(cursor.moveToNext()) {
            String accNum = cursor.getString(
                    cursor.getColumnIndexOrThrow("AccountNumber"));
            String bankName = cursor.getString(
                    cursor.getColumnIndexOrThrow("BankName"));
            String accHolderName = cursor.getString(
                    cursor.getColumnIndexOrThrow("AccountHolderName"));
            Long balance = cursor.getLong(
                    cursor.getColumnIndexOrThrow("Balance"));
            newAcc = new Account(accNum, bankName, accHolderName, balance);
        }
        cursor.close();
        return newAcc;

    }

    @Override
    public void addAccount(Account account){

        //get a set of tuples which maps the column name and the relevant values to be added to the database entry
        ContentValues values = new ContentValues();
        values.put("AccountNumber", account.getAccountNo());
        values.put("BankName", account.getBankName());
        values.put("AccountHolderName", account.getAccountHolderName());
        values.put("Balance", account.getBalance());

        //database insert query
        db.insert("Account", null, values);
    }

    public void removeAccount(String accountNo) throws InvalidAccountException{

        //column to where clause to be applied
        String selection = "AccountNumber" + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { accountNo };
        //sql delete query
        int deletedRows = db.delete("Account", selection, selectionArgs);
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException{

        //get the account relevant to the account number
        Account acc=this.getAccount(accountNo);
        //gte the available balance int he account
        double presentBalance=acc.getBalance();

        //if the expense type is an expense we should check whether the balance of the account is sufficient enough to draw back
        if(expenseType==ExpenseType.EXPENSE) {
            double newBalance = presentBalance - amount;
            //if the balance is sufficient, subtract the amount from the available balance and update the value in the database.
            //if not do nothing
            if (newBalance >= 0) {
                ContentValues values = new ContentValues();
                values.put("Balance", newBalance);

                // Which row to update, based on the title
                String selection = "AccountNumber" + " LIKE ?";
                String[] selectionArgs = {accountNo};

                int count = db.update(
                        "Account",
                        values,
                        selection,
                        selectionArgs);
            }
        }
        //if the expense type is a income, add the amount to the available balance and update the database
        else{
            double newBalance = presentBalance + amount;
                ContentValues values = new ContentValues();
                values.put("Balance", newBalance);

                // Which row to update, based on the title
                String selection = "AccountNumber" + " LIKE ?";
                String[] selectionArgs = {accountNo};

                int count = db.update(
                        "Account",
                        values,
                        selection,
                        selectionArgs);
        }
    }


}
