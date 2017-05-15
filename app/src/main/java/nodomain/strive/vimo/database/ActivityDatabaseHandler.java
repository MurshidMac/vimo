package nodomain.strive.vimo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;

import nodomain.strive.vimo.database.schema.ActivityDBCreationScript;
import nodomain.strive.vimo.database.schema.SchemaMigration;
import nodomain.strive.vimo.entities.DaoMaster;
import nodomain.strive.vimo.entities.DaoSession;
import nodomain.strive.vimo.util.GB;

/**
 * @deprecated can be removed entirely, only used for backwards compatibility
 * Devices Older than Android Api v19
 * Not that Much Needed
 */
public class ActivityDatabaseHandler extends SQLiteOpenHelper implements DBHandler {

    private static final int DATABASE_VERSION = 7;
    private static final String UPDATER_CLASS_NAME_PREFIX = "ActivityDBUpdate_";
    private final Context context;

    public ActivityDatabaseHandler(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            ActivityDBCreationScript script = new ActivityDBCreationScript();
            script.createSchema(db);
        } catch (RuntimeException ex) {
            GB.toast("Error creating database.", Toast.LENGTH_SHORT, GB.ERROR, ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        new SchemaMigration(UPDATER_CLASS_NAME_PREFIX).onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        new SchemaMigration(UPDATER_CLASS_NAME_PREFIX).onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public void closeDb() {
    }

    @Override
    public void openDb() {
    }

    @Override
    public SQLiteOpenHelper getHelper() {
        return this;
    }

    public Context getContext() {
        return context;
    }

    public boolean hasContent() {
        File dbFile = getContext().getDatabasePath(getDatabaseName());
        if (dbFile == null || !dbFile.exists()) {
            return false;
        }

        try {
            try (SQLiteDatabase db = this.getReadableDatabase()) {
                try (Cursor cursor = db.query(DBConstants.TABLE_GBACTIVITYSAMPLES, new String[]{DBConstants.KEY_TIMESTAMP}, null, null, null, null, null, "1")) {
                    return cursor.moveToFirst();
                }
            }
        } catch (Exception ex) {
            // can't expect anything
            GB.log("Error looking for old activity data: " + ex.getMessage(), GB.ERROR, ex);
            return false;
        }
    }

    @Override
    public DaoSession getDaoSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DaoMaster getDaoMaster() {
        throw new UnsupportedOperationException();
    }
}
