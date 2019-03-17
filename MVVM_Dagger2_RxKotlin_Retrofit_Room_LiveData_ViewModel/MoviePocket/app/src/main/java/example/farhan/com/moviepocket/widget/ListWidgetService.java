package example.farhan.com.moviepocket.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

// Service class which connects our list view adapter to widget list view
public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}
