package kr.hyosang.incarapp.service;

interface IGpsService {
    void startLog();
    void stopLog();
    void stopLogAndUpload();
    void requestUpload();
    boolean isLogging();
    long getRecordCount(long tKey);
    int getCurrentTrackSeq();
    long getCurrentTimeKey();
    Location getLastPosition();
}
