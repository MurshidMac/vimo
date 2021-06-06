package vimo.devices.miband;

import android.support.annotation.NonNull;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import vimo.devices.AbstractSampleProvider;
import vimo.entities.DaoSession;
import vimo.entities.MiBandActivitySample;
import vimo.entities.MiBandActivitySampleDao;
import vimo.impl.GBDevice;

/**
 * Base class for Mi1 and Mi2 sample providers. At the moment they both share the
 * same activity sample class.
 */
public abstract class AbstractMiBandSampleProvider extends AbstractSampleProvider<MiBandActivitySample> {

    // maybe this should be configurable 256 seems way off, though.
    private final float movementDivisor = 180.0f; //256.0f;

    public AbstractMiBandSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    @Override
    public float normalizeIntensity(int rawIntensity) {
        return rawIntensity / movementDivisor;
    }

    @Override
    public AbstractDao<MiBandActivitySample, ?> getSampleDao() {
        return getSession().getMiBandActivitySampleDao();
    }

    @NonNull
    @Override
    protected Property getTimestampSampleProperty() {
        return MiBandActivitySampleDao.Properties.Timestamp;
    }

    @NonNull
    @Override
    protected Property getDeviceIdentifierSampleProperty() {
        return MiBandActivitySampleDao.Properties.DeviceId;
    }

    @Override
    protected Property getRawKindSampleProperty() {
        return MiBandActivitySampleDao.Properties.RawKind;
    }

    @Override
    public MiBandActivitySample createActivitySample() {
        return new MiBandActivitySample();
    }
}
