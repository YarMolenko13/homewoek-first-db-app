package com.techstroy.dbfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.techstroy.dbfirstapp.db.DbHelper;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private SQLiteDatabase sdb;

    private LinearLayout fieldsWrapper;
    private TextInputEditText emailInput;
    private TextInputEditText nameInput;
    private Button addButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fieldsWrapper = (LinearLayout) findViewById(R.id.fields);
        addButton = (Button) findViewById(R.id.add);
        deleteButton = (Button) findViewById(R.id.delete);

        dbHelper = new DbHelper(getBaseContext());
        sdb = dbHelper.getWritableDatabase();

        refresh();

        deleteButton.setOnClickListener(v -> {
            sdb.execSQL("DELETE FROM " + DbHelper.USER_TABLE);
            refresh();
            Toast.makeText(getBaseContext(), "All users were deleted", Toast.LENGTH_SHORT).show();
        });

        addButton.setOnClickListener(v -> {
            emailInput = (TextInputEditText) findViewById(R.id.email);
            String email = emailInput.getText().toString();
            nameInput = (TextInputEditText) findViewById(R.id.name);
            String name = nameInput.getText().toString();

            if (!email.trim().equals("") && !name.trim().equals("")) {
                ContentValues values = new ContentValues();
                values.put(DbHelper.COLUMN_EMAIL, email);
                values.put(DbHelper.COLUMN_NAME, name);

                sdb.insert(DbHelper.USER_TABLE, null, values);

                refresh();

                emailInput.setText("");
                nameInput.setText("");
            }
        });
    }

    private void refresh() {
        fieldsWrapper.removeAllViews();

        Cursor cursor = sdb.rawQuery("SELECT " + DbHelper.COLUMN_EMAIL + "," + DbHelper.COLUMN_NAME +
                " FROM " + DbHelper.USER_TABLE + ";", null);
        cursor.moveToFirst();

        try {
            do {
                addField(cursor.getString(0), cursor.getString(1));
            } while (cursor.moveToNext());
        } catch (Exception e) {
            cursor.close();
        } finally {
            cursor.close();
        }
    }

    private void addField(String email, String name) {
        TextView emailView = new TextView(getBaseContext());
        emailView.setText(email);
        emailView.setTextAppearance(R.style.Theme_FieldText);
        emailView.setPadding(0, 0, 100, 0);

        TextView nameView = new TextView(getBaseContext());
        nameView.setTextAppearance(R.style.Theme_FieldText);
        nameView.setText(name);

        LinearLayout dataLayout = new LinearLayout(getBaseContext());
        dataLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        dataLayout.setOrientation(LinearLayout.HORIZONTAL);
        dataLayout.setPadding(0, 0, 0, 28);
        dataLayout.addView(emailView);
        dataLayout.addView(nameView);

        CardView field = new CardView(getBaseContext());
        field.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));


        field.addView(dataLayout);

        fieldsWrapper.addView(field);

    }
}