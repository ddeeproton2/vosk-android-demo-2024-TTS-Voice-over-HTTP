package org.vosk.demo.download;

import android.net.NetworkInfo;

import org.vosk.demo.ui.selector.ModelItem;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class EventBus {
    private static EventBus _instance;

    private final PublishSubject<Download> downloadModelProgressEventSubject = PublishSubject.create();
    private final PublishSubject<ModelItem> startDownloadEventSubject = PublishSubject.create();
    private final PublishSubject<ModelItem> modelSelectedEventSubject = PublishSubject.create();
    private final PublishSubject<Error> errorEventSubject = PublishSubject.create();
    private final PublishSubject<NetworkInfo.State> connectionEventSubject = PublishSubject.create();
    private final PublishSubject<ModelItem> deleteDownloadedModel = PublishSubject.create();

    public EventBus() {
    }

    public static EventBus getInstance() {
        if (_instance == null) {
            _instance = new EventBus();
        }
        return _instance;
    }

    public Observable<Download> getDownloadStatusObservable() {
        return downloadModelProgressEventSubject;
    }

    public void postDownloadStatus(Download stateId) {
        downloadModelProgressEventSubject.onNext(stateId);
    }


    public Observable<ModelItem> getDownloadStartObservable() {
        return startDownloadEventSubject;
    }

    public void postDownloadStart(ModelItem modelItem) {
        startDownloadEventSubject.onNext(modelItem);
    }

    public Observable<ModelItem> getModelSelectedObservable() {
        return modelSelectedEventSubject;
    }

    public void postModelSelectedObservable(ModelItem modelItem) {
        modelSelectedEventSubject.onNext(modelItem);
    }

    public Observable<Error> geErrorObservable() {
        return errorEventSubject;
    }

    public void postErrorStatus(Error error) {
        errorEventSubject.onNext(error);
    }

    public void postConnectionEvent(NetworkInfo.State connection) {
        connectionEventSubject.onNext(connection);
    }

    public Observable<NetworkInfo.State> getConnectionEvent() {
        return connectionEventSubject;
    }

    public Observable<ModelItem> getDeleteDownloadedModelObservable() {
        return deleteDownloadedModel;
    }

    public void postDeleteDownloadedModel(ModelItem modelItem) {
        deleteDownloadedModel.onNext(modelItem);
    }
}
