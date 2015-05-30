package models;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import views.html.accessFailed;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Transactional
public class MyDeadboltHandler extends AbstractDeadboltHandler
{
    public F.Promise<Result> beforeAuthCheck(Http.Context context)
    {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return F.Promise.pure(null);
    }

    public Subject getSubject(Http.Context context)
    {
      final Usuari usuari=null;
    	final String dni=play.mvc.Controller.session().get(
				"dni");
    	final Usuari c[] = { null };
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() {
				try {
					c[0] = (Usuari.recercaPerDni(dni));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		return (c[0]);		
    /*	try {
            return JPA.withTransaction(new play.libs.F.Function0<Usuari>() {
                public Usuari apply() {       
                    try {
						Usuari u= Usuari.recercaPerDni("52439667n");
						return u;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
                }
            });
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }  */
        
    }


    @Override
    public F.Promise<Result> onAuthFailure(Http.Context context,
                                                 String content)
    {
        // you can return any result from here - forbidden, etc
        return F.Promise.promise(new F.Function0<Result>()
        {
            @Override
            public Result apply() throws Throwable {
                return ok(accessFailed.render());
            }
        });
    }
}
