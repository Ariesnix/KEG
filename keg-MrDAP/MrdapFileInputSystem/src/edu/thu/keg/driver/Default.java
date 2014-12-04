package edu.thu.keg.driver;

import java.io.File;
import java.io.IOException;

public class Default {
	public static void init() {
		File file = new File(".");
		System.getProperties().put("hadoop.home.dir", file.getAbsolutePath());

		File newbin = new File(file, "bin");
		if (!newbin.exists()) {
			newbin.mkdirs();

		}

		// System.out.println(newbin.getAbsoluteFile());

		File f = new File(newbin, "winutils.exe");
		if (!f.exists()) {

			try {
				new File(newbin, "winutils.exe").createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.getProperties().put("hadoop.home.dir",
					file.getAbsolutePath());

		}
	}
}
