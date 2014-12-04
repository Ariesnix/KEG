package edu.thu.keg.link.client.hdfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.client.hdfs.FSConnPool.HDFS;

public class MFile {
	private Path path = null;

	public MFile(String path) {
		if (path.toLowerCase().startsWith("hdfs://")) {
			this.path = new Path(path);

		} else {
			this.path = new Path(Default.getValue("HDFS_URL") + path);
		}
	}

	public boolean exists() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();
		boolean result = hdfs.exists(path);
		hdfs.close();

		return result;
	}

	public long getLen() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();
		long len = hdfs.getFileStatus(path).getLen();
		hdfs.close();
		return len;
	}

	public MFile(Path path)

	{
		if (path.toString().toLowerCase().startsWith("hdfs://")) {
			this.path = path;
		} else {
			this.path = new Path(Default.getValue("HDFS_URL") + path.toString());
		}

	}

	// move
	public boolean rename(String dist) throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();

		Path newPath = new Path(dist);

		boolean result = hdfs.rename(path, newPath);

		if (result == true) {
			this.path = newPath;
		}

		hdfs.close();
		return result;
	}

	public String getParent() {
		return this.path.getParent().toString();
	}

	// 返回下一级的目录
	public MFile[] list() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();
		if (hdfs.isFile(path)) {
			throw new IOException("this is not a Directory !");
		}

		FileStatus[] fstats = hdfs.listStatus(path);
		MFile[] mfs = new MFile[fstats.length];

		Path p = null;

		for (int i = 0; i < fstats.length; i++) {
			p = fstats[i].getPath();
			mfs[i] = new MFile(p.toString());
		}
		hdfs.close();
		return mfs;

	}

	// 判断是否文件夹
	public boolean isDirectory() throws IOException {
		boolean result = true;
		HDFS hdfs = FSConnPool.getHDFS();
		result = hdfs.isDirectory(path);
		hdfs.close();

		return result;

	}

	public long totalSize() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();
		long size = totalSize(hdfs, this.path);
		hdfs.close();
		return size;
	}

	// 使用递归方式统计文夹的大小
	private long totalSize(HDFS fs, Path path) throws IOException {

		if (fs.isFile(path)) {
			return fs.getFileStatus(path).getLen();
		}

		FileStatus[] fstats = fs.listStatus(path);

		long len = 0;
		for (FileStatus fstat : fstats) {

			len += totalSize(fs, fstat.getPath());
		}

		return len;

	}

	public String getName() {
		return path.getName();
	}

	public String getPath() {
		return path.toString();
	}

	@Override
	public String toString() {
		return path.getName() + ":" + path.toString();
	}

	// 当是文件夹时可用
	public String[] visitAll() throws IOException {
		ArrayList<String> info = new ArrayList<String>();

		HDFS hdfs = FSConnPool.getHDFS();

		visitAll(hdfs, path, info);

		hdfs.close();

		return info.toArray(new String[0]);

	}

	public boolean mkdir() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();

		boolean b = hdfs.mkdirs(path);

		hdfs.close();

		return b;
	}

	public boolean delete() throws IOException {

		HDFS hdfs = FSConnPool.getHDFS();
		boolean b = hdfs.delete(path, true);

		hdfs.close();

		return b;
	}

	public String getOwner() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();

		FileStatus fileStatus = hdfs.getFileStatus(path);
		String owner = fileStatus.getOwner();
		hdfs.close();
		return owner;
	}

	public String getPermission() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();
		FileStatus fileStatus = hdfs.getFileStatus(path);
		String perm = fileStatus.getPermission().toString();
		hdfs.close();
		return perm;

	}

	public FSDataInputStream open() throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();
		FSDataInputStream dis = hdfs.open(path);
		hdfs.close();

		return dis;
	}

	public FSDataOutputStream create(Path f, boolean overwrite)
			throws IOException {
		HDFS hdfs = FSConnPool.getHDFS();
		FSDataOutputStream dos = hdfs.create(f, overwrite);

		hdfs.close();

		return dos;
	}

	private void visitAll(HDFS fs, Path p, List<String> list)
			throws IOException {

		if (fs.isFile(p)) {
			list.add(p.getParent().toString() + ":" + p.getName() + ":F");
			return;
		}

		list.add(p.getParent().toString() + ":" + p.getName() + ":D");

		FileStatus[] fstats = fs.listStatus(p);

		for (FileStatus fstat : fstats) {
			visitAll(fs, fstat.getPath(), list);
		}

	}

}
