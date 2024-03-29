package vimo.database.schema;

import android.database.sqlite.SQLiteDatabase;

import vimo.database.DBHelper;

import static vimo.database.DBConstants.KEY_CUSTOM_SHORT;
import static vimo.database.DBConstants.KEY_INTENSITY;
import static vimo.database.DBConstants.KEY_PROVIDER;
import static vimo.database.DBConstants.KEY_STEPS;
import static vimo.database.DBConstants.KEY_TIMESTAMP;
import static vimo.database.DBConstants.KEY_TYPE;
import static vimo.database.DBConstants.TABLE_GBACTIVITYSAMPLES;

// Following to Upgrade the Database
public class ActivityDBCreationScript {
    public void createSchema(SQLiteDatabase db) {
        String CREATE_GBACTIVITYSAMPLES_TABLE = "CREATE TABLE " + TABLE_GBACTIVITYSAMPLES + " ("
                + KEY_TIMESTAMP + " INT,"
                + KEY_PROVIDER + " TINYINT,"
                + KEY_INTENSITY + " SMALLINT,"
                + KEY_STEPS + " TINYINT,"
                + KEY_TYPE + " TINYINT,"
                + KEY_CUSTOM_SHORT + " INT,"
                + " PRIMARY KEY (" + KEY_TIMESTAMP + "," + KEY_PROVIDER + ") ON CONFLICT REPLACE)" + DBHelper.getWithoutRowId();
        db.execSQL(CREATE_GBACTIVITYSAMPLES_TABLE);
    }
}
