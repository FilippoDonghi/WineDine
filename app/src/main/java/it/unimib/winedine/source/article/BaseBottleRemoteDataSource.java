package it.unimib.winedine.source.article;

import it.unimib.winedine.repository.wine.BottleResponseCallback;

public abstract class BaseBottleRemoteDataSource {
    protected BottleResponseCallback bottleCallback;

    public void setBottleCallback(BottleResponseCallback bottleCallback) {
        this.bottleCallback = bottleCallback;
    }

   //public abstract void getArticles();
}