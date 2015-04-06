package com.josepablorocha.ribbit;

import android.app.Application;
import android.util.Log;

import com.josepablorocha.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
/**
 * Created by Pablo on 3/29/2015.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        // Enable Local Datastore
        //Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                "PtqbtQ09wpxo9NWSDDxRrBEw9mml51gw20YGN9eS",
                "P8RfDhedjHQGwy8kKyQIMgrQ3asPmI4NbZGeoolg");

        ParsePush.subscribeInBackground("");
    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
