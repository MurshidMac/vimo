package nodomain.strive.vimo.database.schema;

import android.database.sqlite.SQLiteDatabase;

import nodomain.strive.vimo.database.DBHelper;
import nodomain.strive.vimo.database.DBUpdateScript;
import nodomain.strive.vimo.database.DBConstants;

/**
 * Adds a column "customShort" to the table "GBActivitySamples"
 */
public class ActivityDBUpdate_6 implements DBUpdateScript {
    @Override
    public void upgradeSchema(SQLiteDatabase db) {
        if (!DBHelper.existsColumn(DBConstants.TABLE_GBACTIVITYSAMPLES, DBConstants.KEY_CUSTOM_SHORT, db)) {
            String ADD_COLUMN_CUSTOM_SHORT = "ALTER TABLE " + DBConstants.TABLE_GBACTIVITYSAMPLES + " ADD COLUMN "
                    + DBConstants.KEY_CUSTOM_SHORT + " INT;";
            db.execSQL(ADD_COLUMN_CUSTOM_SHORT);
        }
    }

    @Override
    public void downgradeSchema(SQLiteDatabase db) {
    }
}
