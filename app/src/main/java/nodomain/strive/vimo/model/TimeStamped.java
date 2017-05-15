package nodomain.strive.vimo.model;

public interface TimeStamped {
    /**
     * Unix timestamp of the sample, i.e. the number of seconds since 1970-01-01 00:00:00 UTC.
     */
    int getTimestamp();
}
