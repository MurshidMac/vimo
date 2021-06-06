package vimo.database.schema;

import android.database.sqlite.SQLiteDatabase;

import vimo.database.DBHelper;
import vimo.database.DBUpdateScript;

import static vimo.database.DBConstants.KEY_CUSTOM_SHORT;
import static vimo.database.DBConstants.TABLE_GBACTIVITYSAMPLES;

/**
 * Adds a column "customShort" to the table "GBActivitySamples"
 */
public class ActivityDBUpdate_6 implements DBUpdateScript {
    @Override
    public void upgradeSchema(SQLiteDatabase db) {
        if (!DBHelper.existsColumn(TABLE_GBACTIVITYSAMPLES, KEY_CUSTOM_SHORT, db)) {
            String ADD_COLUMN_CUSTOM_SHORT = "ALTER TABLE " + TABLE_GBACTIVITYSAMPLES + " ADD COLUMN "
                    + KEY_CUSTOM_SHORT + " INT;";
            db.execSQL(ADD_COLUMN_CUSTOM_SHORT);
        }
    }

    @Override
    public void downgradeSchema(SQLiteDatabase db) {
    }
}
