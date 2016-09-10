package georgemcdonnell.com.treasurehunt.model;

import java.util.ArrayList;

/**
 * Created by George on 21/10/2015.
 */
public class Hunt {

    private String creator;
    private String name;
    private boolean isCreatorCurrentUser;

    public Hunt(String creator, String name, boolean isCreatorCurrentUser) {
        this.creator = creator;
        this.name = name;
        this.isCreatorCurrentUser = isCreatorCurrentUser;
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public boolean isCreatorCurrentUser() {
        return isCreatorCurrentUser;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\n" + "Creator: " + creator;
    }
}
