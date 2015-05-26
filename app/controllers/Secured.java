package controllers;

import java.util.Date;

import play.Play;
import play.i18n.Messages;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Controller.*;


public class Secured extends Security.Authenticator {
    
public boolean expired;
    @Override
    public String getUsername(Context ctx) {
        
        // see if the session is expired
        String previousTick = play.mvc.Controller.session("userTime");
        if (previousTick != null && !previousTick.equals("")) {
            long previousT = Long.valueOf(previousTick);
            long currentT = new Date().getTime();
            long timeout = Long.valueOf(Play.application().configuration().getString("sessionTimeout")) * 1000 * 60;
            float dif=(currentT-previousT)/(1000*60);
            if ((currentT - previousT) > timeout) {
                // session expired
            	play.mvc.Controller.session().clear();
            	expired=true;
            } 
        }
 
        // update time in session
        String tickString = Long.toString(new Date().getTime());
        play.mvc.Controller.session("userTime", tickString);

    	return ctx.session().get("dni");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        if (expired==false) play.mvc.Controller.flash("error",Messages.get("must_login"));
        return redirect(routes.Application.index());
    }
}
