package vimo.database.schema;

import android.database.sqlite.SQLiteDatabase;

import vimo.database.DBHelper;
import vimo.database.DBUpdateScript;
import vimo.entities.DeviceAttributesDao;

/*
 * adds heart rate column to health table
 */

public class GadgetbridgeUpdate_15 implements DBUpdateScript {
    @Override
    public void upgradeSchema(SQLiteDatabase db) {
        if (!DBHelper.existsColumn(DeviceAttributesDao.TABLENAME, DeviceAttributesDao.Properties.VolatileIdentifier.columnName, db)) {
            String ADD_COLUMN_VOLATILE_IDENTIFIER = "ALTER TABLE " + DeviceAttributesDao.TABLENAME + " ADD COLUMN "
                    + DeviceAttributesDao.Properties.VolatileIdentifier.columnName + " TEXT;";
            db.execSQL(ADD_COLUMN_VOLATILE_IDENTIFIER);
        }
    }

    @Override
    public void downgradeSchema(SQLiteDatabase db) {
    }
}
