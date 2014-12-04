package edu.thu.keg.mrdap.init;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.client.taskClient.TaskClient;
import edu.thu.keg.mrdap.impl.PlatformImpl;

/**
 * 启动服务器时候的初始化server
 * 
 * @author Yuan Bozhi
 * 
 */
@WebListener("Socket Init")
public class ServerInitial implements ServletContextListener {
	private static Logger log = Logger.getLogger(ServerInitial.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.info("Server destroying!");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// try {
		// Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		// Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
		// Class.forName("oracle.jdbc.driver.OracleDriver");
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		try {
			System.out.println(new File(".").getCanonicalPath().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServletContext sc = arg0.getServletContext();
		String P_Config = ResourceBundle.getBundle("platform_initial")
				.getString("PlatformImpl_CONFIG");
		Default.initDefault(arg0.getServletContext().getRealPath(
				"/WEB-INF/" + "default.xml"));
		// Default.initDefault("default.xml");
		TaskClient.reload();
		PlatformImpl p = new PlatformImpl(arg0.getServletContext().getRealPath(
				"/WEB-INF/" + P_Config));

		sc.setAttribute("platform", p);
		System.out.println("启动MrDAP服务器中...");

		try {
			System.out.println("MrDAP服务器根目录"
					+ (new File(".")).getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("启动MrDAP服务器");
		p.crud();
		System.out.println("启动MrDAP完成！");
	}
}
