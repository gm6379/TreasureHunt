package georgemcdonnell.com.treasurehunt.dialogs;

import georgemcdonnell.com.treasurehunt.hunts.HuntOptionsFragment;

/**
 * Created by George on 08/11/2015.
 */
public interface AsyncResponse {

    void processFinish(String output);

    void processFinish(HuntOptionsFragment fragment);
}
