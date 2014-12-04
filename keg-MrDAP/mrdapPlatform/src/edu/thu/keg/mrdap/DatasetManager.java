package edu.thu.keg.mrdap;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;

public interface DatasetManager {

	public void refreshDatasetInHadoop();

	public void refreshDatasetInHBase();

	public Collection<Dataset> getDatasetList();

	public Dataset getDataset(String id);

	public List<Dataset> getHDatasets(String typeId);

	public boolean containsDataset(String id);

	public void saveXMLChanges() throws IOException;

	public Dataset createDataset(String id, String args, String serial,
			Date date, String type, String name, String owner, int rowCount,
			String path, long sizeMb, boolean isDic);

	public void addDataset2Hbase(String tableId, String type);

	public void removeDatasetInHbase(String tableId);

	public List<String> getAllFilesPath(String rootPath);
}
