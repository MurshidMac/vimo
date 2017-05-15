package nodomain.strive.vimo.devices.pebble;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import nodomain.strive.vimo.devices.AbstractSampleProvider;
import nodomain.strive.vimo.devices.SampleProvider;
import nodomain.strive.vimo.entities.DaoSession;
import nodomain.strive.vimo.entities.PebbleMorpheuzSample;
import nodomain.strive.vimo.entities.PebbleMorpheuzSampleDao;
import nodomain.strive.vimo.impl.GBDevice;

public class PebbleMorpheuzSampleProvider extends AbstractSampleProvider<PebbleMorpheuzSample> {

    protected float movementDivisor = 5000f;

    public PebbleMorpheuzSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    @Override
    public AbstractDao<PebbleMorpheuzSample, ?> getSampleDao() {
        return null;
    }

    @Override
    protected Property getTimestampSampleProperty() {
        return PebbleMorpheuzSampleDao.Properties.Timestamp;
    }

    @Override
    protected Property getRawKindSampleProperty() {
        return null; // not supported
    }

    @Override
    protected Property getDeviceIdentifierSampleProperty() {
        return PebbleMorpheuzSampleDao.Properties.DeviceId;
    }

    @Override
    public PebbleMorpheuzSample createActivitySample() {
        return new PebbleMorpheuzSample();
    }


    @Override
    public float normalizeIntensity(int rawIntensity) {
        return rawIntensity / movementDivisor;
    }

    @Override
    public int getID() {
        return SampleProvider.PROVIDER_PEBBLE_MORPHEUZ;
    }

    @Override
    public int normalizeType(int rawType) {
        return rawType;
    }

    @Override
    public int toRawActivityKind(int activityKind) {
        return activityKind;
    }
}
