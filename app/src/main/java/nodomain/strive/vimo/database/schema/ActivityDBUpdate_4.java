package nodomain.strive.vimo.database.schema;

import android.database.sqlite.SQLiteDatabase;

import nodomain.strive.vimo.database.DBHelper;
import nodomain.strive.vimo.database.DBUpdateScript;
import nodomain.strive.vimo.database.DBConstants;

/**
 * Upgrade and downgrade with DB versions <= 5 is not supported.
 * Just recreates the default schema. Those GB versions may or may not
 * work with that, but this code will probably not create a DB for them
 * anyway.
 */
public class ActivityDBUpdate_4 extends ActivityDBCreationScript implements DBUpdateScript {
    @Override
    public void upgradeSchema(SQLiteDatabase db) {
        recreateSchema(db);
    }

    @Override
    public void downgradeSchema(SQLiteDatabase db) {
        recreateSchema(db);
    }

    private void recreateSchema(SQLiteDatabase db) {
        DBHelper.dropTable(DBConstants.TABLE_GBACTIVITYSAMPLES, db);
        createSchema(db);
    }
}
