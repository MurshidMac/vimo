package vimo.devices.pebble;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import vimo.devices.AbstractSampleProvider;
import vimo.devices.SampleProvider;
import vimo.entities.DaoSession;
import vimo.entities.PebbleMisfitSample;
import vimo.entities.PebbleMisfitSampleDao;
import vimo.impl.GBDevice;

public class PebbleMisfitSampleProvider extends AbstractSampleProvider<PebbleMisfitSample> {

    protected final float movementDivisor = 300f;

    public PebbleMisfitSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    @Override
    public int normalizeType(int rawType) {
        return rawType;
    }

    @Override
    public int toRawActivityKind(int activityKind) {
        return activityKind;
    }

    @Override
    public float normalizeIntensity(int rawIntensity) {
        return rawIntensity / movementDivisor;
    }

    @Override
    public PebbleMisfitSample createActivitySample() {
        return new PebbleMisfitSample();
    }

    @Override
    public int getID() {
        return SampleProvider.PROVIDER_PEBBLE_MISFIT;
    }

    @Override
    public AbstractDao<PebbleMisfitSample, ?> getSampleDao() {
        return getSession().getPebbleMisfitSampleDao();
    }

    @Override
    protected Property getRawKindSampleProperty() {
        return null;
    }

    protected Property getTimestampSampleProperty() {
        return PebbleMisfitSampleDao.Properties.Timestamp;
    }

    protected Property getDeviceIdentifierSampleProperty() {
        return PebbleMisfitSampleDao.Properties.DeviceId;
    }
}
