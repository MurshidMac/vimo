package nodomain.strive.vimo.model;

public interface SummaryOfDay {
    byte getProvider();

    int getSteps();

    int getDayStartWakeupTime();

    int getDayEndFallAsleepTime();

}
