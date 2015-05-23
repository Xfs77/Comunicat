import play.*;
import play.libs.*;
import play.libs.F.Promise;

import java.util.*;

import models.*;
import play.data.format.Formatters;
import play.data.format.Formatters.*;
import play.db.jpa.JPA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.libs.F.*;
import static play.mvc.Results.*;

public class Global extends GlobalSettings {

	public Promise<Result> onError(RequestHeader request, Throwable t) {
		return Promise.<Result> pure(internalServerError(views.html.errorPage
				.render(t)));
	}

	public void onStart(Application app) {
		// Register our DateFormater
		Formatters.register(Date.class, new SimpleFormatter<Date>() {

			private final static String PATTERN = "dd/MM/yyyy";

			public Date parse(String text, Locale locale)
					throws java.text.ParseException {
				if (text == null || text.trim().isEmpty()) {
					return null;
				}
				SimpleDateFormat sdf = new SimpleDateFormat(PATTERN, locale);
				sdf.setLenient(false);
				return sdf.parse(text);
			}

			public String print(Date value, Locale locale) {
				if (value == null) {
					return "";
				}
				return new SimpleDateFormat(PATTERN, locale).format(value);
			}

		});

		Formatters.register(Comunitat.class,
				new Formatters.SimpleFormatter<Comunitat>() {

					public Comunitat parse(String text, Locale locale)
							throws java.text.ParseException {
						Comunitat comunitat;
						try {
							comunitat = Comunitat.recercaPerNif(text);
							return comunitat;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					@Override
					public String print(Comunitat arg0, Locale arg1) {
						// TODO Auto-generated method stub
						return arg0.nif;
					}

				});

		Formatters.register(EstatNota.class,
				new Formatters.SimpleFormatter<EstatNota>() {

					public EstatNota parse(String text, Locale locale)
							throws java.text.ParseException {
						String t = text;
						EstatNota estat;
						try {
							estat = EstatNota.recerca(text);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							estat=null;
							e.printStackTrace();
						}
						return estat;
					}

					@Override
					public String print(EstatNota arg0, Locale arg1) {
						// TODO Auto-generated method stub
						return arg0.estat;
					}

				});

		Formatters.register(EstatReunio.class,
				new Formatters.SimpleFormatter<EstatReunio>() {

					public EstatReunio parse(String text, Locale locale)
							throws java.text.ParseException {
						String t = text;
						EstatReunio estat = EstatReunio.recerca(text);
						return estat;
					}

					@Override
					public String print(EstatReunio arg0, Locale arg1) {
						// TODO Auto-generated method stub
						return arg0.estat;
					}

				});

		Formatters.register(Element.class,
				new Formatters.SimpleFormatter<Element>() {

					public Element parse(String text, Locale locale)
							throws java.text.ParseException {
						final String comunitat = text.substring(0,
								text.indexOf('&'));
						final String element = text.substring(text.indexOf('&') + 1);
						Comunitat c;
						try {
							c = (Comunitat.recercaPerNif(comunitat));
							Element e = (Element.recercaPerCodi(c, element));
							return e;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						return null;
					}

					@Override
					public String print(Element arg0, Locale arg1) {
						// TODO Auto-generated method stub
						return (arg0.comunitat.nif + "&" + arg0.codi);
					}

				});

		Formatters.register(Usuari.class,
				new Formatters.SimpleFormatter<Usuari>() {

					public Usuari parse(String text, Locale locale)
							throws java.text.ParseException {
						Usuari u = null;
						try {
							u = (Usuari.recercaPerDni(text));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return u;
					}

					@Override
					public String print(Usuari arg0, Locale arg1) {
						// TODO Auto-generated method stub
						return (arg0.dni);
					}

				});

		Formatters.register(TipusVei.class,
				new Formatters.SimpleFormatter<TipusVei>() {

					public TipusVei parse(String text, Locale locale)
							throws java.text.ParseException {
						TipusVei t = (TipusVei.recerca(text));
						return t;
					}

					@Override
					public String print(TipusVei arg0, Locale arg1) {
						// TODO Auto-generated method stub
						return (arg0.tipus);
					}

				});

		Formatters.register(Nota.class, new Formatters.SimpleFormatter<Nota>() {

			public Nota parse(String text, Locale locale)
					throws java.text.ParseException {
				Nota n;
				try {
					n = (Nota.recercaPerCodi(Integer.parseInt(text)));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					n=null;
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					n=null;
					e.printStackTrace();
				}
				return n;
			}

			@Override
			public String print(Nota arg0, Locale arg1) {
				// TODO Auto-generated method stub
				return Integer.toString(arg0.codi);
			}

		});

		Formatters.register(MovimentNota.class,
				new Formatters.SimpleFormatter<MovimentNota>() {

					public MovimentNota parse(String arg1, Locale locale)
							throws java.text.ParseException {
						final String nota = arg1.substring(0, arg1.indexOf('&'));
						final String moviment = arg1.substring(arg1
								.indexOf('&') + 1);
						Nota n;
						try {
							n = (Nota.recercaPerCodi(Integer.parseInt(nota)));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							n=null;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							n=null;
							e.printStackTrace();
						}
						MovimentNota m;
						try {
							m = (MovimentNota.recercaPerCodi(n,
									Integer.parseInt(moviment)));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							m=null;
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							m=null;
							e.printStackTrace();
						}
						return m;
					}

					@Override
					public String print(MovimentNota arg0, Locale arg1) {
						// TODO Auto-generated method stub
						return (Integer.toString(arg0.nota.codi) + "&" + Integer
								.toString(arg0.codi));
					}

				});

	}

}