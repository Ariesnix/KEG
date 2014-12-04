package edu.thu.keg.util;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTools {
	public static void unzip(String zipFilePath, String targetPath)
			throws IOException {
		OutputStream os = null;
		InputStream is = null;
		ZipFile zipFile = null;
		String directoryPath = "";
		try {
			zipFile = new ZipFile(zipFilePath);

			if (null == targetPath || "".equals(targetPath)) {
				directoryPath = zipFilePath.substring(0,
						zipFilePath.lastIndexOf("."));
			} else {
				directoryPath = targetPath;
			}
			Enumeration entryEnum = zipFile.entries();
			if (null != entryEnum) {
				ZipEntry zipEntry = null;
				while (entryEnum.hasMoreElements()) {
					zipEntry = (ZipEntry) entryEnum.nextElement();
					if (zipEntry.isDirectory()) {
						directoryPath = directoryPath + File.separator
								+ zipEntry.getName();
						System.out.println(directoryPath);
						continue;
					}
					if (zipEntry.getSize() > 0) {
						// 文件
						File targetFile = ZipTools.buildFile(directoryPath
								+ File.separator + zipEntry.getName(), false);
						os = new BufferedOutputStream(new FileOutputStream(
								targetFile));
						is = zipFile.getInputStream(zipEntry);
						byte[] buffer = new byte[4096];
						int readLen = 0;
						while ((readLen = is.read(buffer, 0, 4096)) >= 0) {
							os.write(buffer, 0, readLen);
						}

						os.flush();
						os.close();
					} else {
						// 空目录
						ZipTools.buildFile(directoryPath + File.separator
								+ zipEntry.getName(), true);
					}
				}
			}
		} catch (IOException ex) {
			System.err.println(zipFilePath + " " + "文件解压有错误！");
			throw ex;
		} finally {
			if (null != zipFile) {
				zipFile = null;
			}
			if (null != is) {
				is.close();
			}
			if (null != os) {
				os.close();
			}
			System.out.println(zipFilePath + " " + "成功解压到" + directoryPath);
		}
	}

	public static File buildFile(String fileName, boolean isDirectory) {

		File target = new File(fileName);

		if (isDirectory) {

			target.mkdirs();

		} else {

			if (!target.getParentFile().exists()) {

				target.getParentFile().mkdirs();

				target = new File(target.getAbsolutePath());

			}

		}

		return target;

	}
}
