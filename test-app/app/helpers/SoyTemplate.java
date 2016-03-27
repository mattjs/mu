package helpers;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

public class SoyTemplate {
	private static String TEMPLATE_PATH = "app/views";
	private static int RELOAD_INTERVAL_MILLIS = 10000;
	
	private static SoyFileSet sfs;
	private static SoyTofu tofu;
	
	public static String render(String template) {
		if (tofu == null) initialize();
	    return tofu.newRenderer(template).render();
	}
	
	private static void initialize() {
		System.out.println("Initializing soy templates");
		loadTemplates();
		reloadOnInterval();
	}
	
	private static void loadTemplates() {
		File folder = new File(TEMPLATE_PATH);
		File[] templates = folder.listFiles();
		SoyFileSet.Builder builder = SoyFileSet.builder();
		for (int i = 0; i < templates.length; i++) {
			builder.add(templates[i]);
		}
		sfs = builder.build();
		tofu = sfs.compileToTofu();
	}
	
	private static void reloadOnInterval() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Reloading soy templates");
				loadTemplates();
			}
		}, RELOAD_INTERVAL_MILLIS, RELOAD_INTERVAL_MILLIS);
	}
}