package net.flyingbags.flyingapps.presenter;

/**
 * Created by User on 2017-10-08.
 */

public class NavTabPresenter {
    public interface view {
        void showNavTab();
        void showHome();
        void showProfile();
    }

    public interface presenter {
        void showNavTab();
        void showHome();
        void showProfile();
    }
}
