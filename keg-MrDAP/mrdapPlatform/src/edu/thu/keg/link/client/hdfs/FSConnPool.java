package edu.thu.keg.link.client.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.thu.keg.link.client.env.Default;

public class FSConnPool {

	private static final Configuration conf = new Configuration();
	private static final FSConnPool fscpp = new FSConnPool();

	static {

		String ADDR = Default.getValue("HDFS_URL");
		conf.set("fs.defaultFS", ADDR);
	}

	private FSConnPool() {

	}

	private static FileSystem getInstance() throws IOException {
		return FileSystem.get(conf);

	}

	private static final ArrayList<FileSystem> pool = new ArrayList<FileSystem>();

	private static synchronized FileSystem getFS() throws IOException {
		if (pool.isEmpty()) {
			return getInstance();
		}
		return pool.remove(pool.size() - 1);

	}

	private synchronized static void close(FileSystem fs) {
		// System.out.println(fs);
		pool.add(fs);
		// closed++;
		// System.out.println("closed:"+closed+",pool.length:"+pool.size());
	}

	public static class HDFS {
		private FileSystem fs = null;
		private FSConnPool fscp = null;

		private HDFS(FileSystem fs, FSConnPool fscp) {
			this.fs = fs;
		}

		public boolean delete(Path f, boolean recursive) throws IOException {
			return fs.delete(f, recursive);
		}

		public boolean mkdirs(Path f) throws IOException {

			return fs.mkdirs(f);
		}

		public boolean isFile(Path f) throws IOException {
			return fs.isFile(f);
		}

		public boolean exists(Path f) throws IOException {
			return fs.exists(f);
		}

		public FSDataOutputStream create(Path f, boolean overwrite)
				throws IOException {
			return fs.create(f, overwrite);

		}

		public FSDataInputStream open(Path f) throws IOException {
			return fs.open(f);
		}

		public FileStatus getFileStatus(Path f) throws IOException {

			return fs.getFileStatus(f);
		}

		public FileStatus[] listStatus(Path f) throws FileNotFoundException,
				IOException {
			return fs.listStatus(f);
		}

		public boolean isDirectory(Path f) throws IOException {
			return fs.isDirectory(f);
		}

		public void close() {
			fscp.close(fs);
			fs = null;
		}

		public boolean rename(Path src, Path dst) throws IOException {
			return fs.rename(src, dst);
		}

	}

	public static HDFS getHDFS() throws IOException {
		return new HDFS(getFS(), fscpp);
	}

}
