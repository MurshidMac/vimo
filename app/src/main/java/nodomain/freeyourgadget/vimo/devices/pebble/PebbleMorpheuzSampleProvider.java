package nodomain.freeyourgadget.vimo.devices.pebble;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import nodomain.freeyourgadget.vimo.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.vimo.devices.SampleProvider;
import nodomain.freeyourgadget.vimo.entities.DaoSession;
import nodomain.freeyourgadget.vimo.entities.PebbleMorpheuzSample;
import nodomain.freeyourgadget.vimo.entities.PebbleMorpheuzSampleDao;
import nodomain.freeyourgadget.vimo.impl.GBDevice;

public class PebbleMorpheuzSampleProvider extends AbstractSampleProvider<PebbleMorpheuzSample> {

    protected float movementDivisor = 5000f;

    public PebbleMorpheuzSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    @Override
    public AbstractDao<PebbleMorpheuzSample, ?> getSampleDao() {
        return getSession().getPebbleMorpheuzSampleDao();
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
