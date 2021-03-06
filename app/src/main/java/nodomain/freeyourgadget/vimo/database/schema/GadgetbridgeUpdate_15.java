package nodomain.freeyourgadget.vimo.database.schema;

import android.database.sqlite.SQLiteDatabase;

import nodomain.freeyourgadget.vimo.database.DBHelper;
import nodomain.freeyourgadget.vimo.database.DBUpdateScript;
import nodomain.freeyourgadget.vimo.entities.DeviceAttributesDao;

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
