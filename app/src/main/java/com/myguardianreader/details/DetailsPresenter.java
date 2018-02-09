package com.myguardianreader.details;

import android.util.Log;

import com.myguardianreader.repository.remote.GuardianService;
import com.myguardianreader.common.BasePresenter;
import com.myguardianreader.common.BasePresenterView;
import com.reader.android.api.model.ApiArticleContent;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;

public class DetailsPresenter extends BasePresenter<DetailsPresenter.View> {
    private final Scheduler uiScheduler;
    private final Scheduler ioScheduler;
    private final GuardianService guardianRepository;

    private static final String TAG = "DetailsPresenter";

    public DetailsPresenter(Scheduler uiScheduler, Scheduler ioScheduler, GuardianService guardianRepository) {
        this.uiScheduler = uiScheduler;
        this.ioScheduler = ioScheduler;
        this.guardianRepository = guardianRepository;
    }

    @Override
    public void register(View view, String url) {
        super.register(view, url);

        Observable<String> apiArticleFieldsObservable  = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                Log.d(TAG, "observableEmitter");
                observableEmitter.onNext(url);
                observableEmitter.onComplete();
            }
        });

        addToUnsubscribe(apiArticleFieldsObservable
                .switchMapSingle(ignored -> guardianRepository.getArticle(url).subscribeOn(ioScheduler))
                .observeOn(uiScheduler)
                .subscribe(
                        apiArticleContent -> {
                            Log.d(TAG,"subscribe apiArticle");
                            view.showDetails(apiArticleContent);
                            view.dismissProgressDialog();
                        },
                        error -> {
                            Log.e(TAG,"Error subscribe" + error);
                            view.displayMessage("An error occurs while loading the data, please try again.");
                            view.dismissProgressDialog();
                        }));
    }

    interface View extends BasePresenterView {

        void showDetails(ApiArticleContent apiArticleContent);

        void displayMessage(String errorMessage);

        void dismissProgressDialog();
    }
}
