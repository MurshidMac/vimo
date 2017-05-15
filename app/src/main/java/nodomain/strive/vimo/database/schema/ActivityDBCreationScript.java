package nodomain.strive.vimo.database.schema;

import android.database.sqlite.SQLiteDatabase;

import nodomain.strive.vimo.database.DBHelper;
import nodomain.strive.vimo.database.DBConstants;

// Following to Upgrade the Database
public class ActivityDBCreationScript {
    public void createSchema(SQLiteDatabase db) {
        String CREATE_GBACTIVITYSAMPLES_TABLE = "CREATE TABLE " + DBConstants.TABLE_GBACTIVITYSAMPLES + " ("
                + DBConstants.KEY_TIMESTAMP + " INT,"
                + DBConstants.KEY_PROVIDER + " TINYINT,"
                + DBConstants.KEY_INTENSITY + " SMALLINT,"
                + DBConstants.KEY_STEPS + " TINYINT,"
                + DBConstants.KEY_TYPE + " TINYINT,"
                + DBConstants.KEY_CUSTOM_SHORT + " INT,"
                + " PRIMARY KEY (" + DBConstants.KEY_TIMESTAMP + "," + DBConstants.KEY_PROVIDER + ") ON CONFLICT REPLACE)" + DBHelper.getWithoutRowId();
        db.execSQL(CREATE_GBACTIVITYSAMPLES_TABLE);
    }
}
