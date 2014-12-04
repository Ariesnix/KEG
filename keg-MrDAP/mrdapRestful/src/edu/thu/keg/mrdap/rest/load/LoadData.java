package edu.thu.keg.mrdap.rest.load;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.json.JSONWithPadding;

import edu.thu.keg.LoadManager;
import edu.thu.keg.link.client.fs.FS;
import edu.thu.keg.link.client2.HBaseTableOp;
import edu.thu.keg.link.client2.ZK_info;
import edu.thu.keg.mr.IHBaseManager;
import edu.thu.keg.mr.MroDao;
import edu.thu.keg.mr.MrsDao;
import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.Platform;
import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.impl.DatasetManagerImpl;
import edu.thu.keg.mrdap.rest.classes.JDataset;
import edu.thu.keg.mrdap.rest.dataset.DsGetFunctions;
import edu.thu.keg.util.Util;

@Path("/load")
public class LoadData {
	/**
	 * 
	 */
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	@Context
	ServletContext servletcontext;
	@Context
	HttpServletRequest httpServletRequest;
	@Context
	HttpServletResponse httpServletResponse;

	HttpSession session = null;
	private static Logger log = Logger
			.getLogger(LoadData.class.getSimpleName());

	@GET
	@Path("/hello")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding heloo(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		System.out.println("asdf");
		return new JSONWithPadding(new GenericEntity<String>("hello") {
		}, jsoncallback);
	}

	@GET
	@Path("/start")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding startMigration(@QueryParam("input") String input,
			@QueryParam("type") String type,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		JSONObject job = new JSONObject();
		try {
			job.put("status", "FAILED");
			job.put("msg", "未知文件类型！");

			LoadManager lm = LoadManager.getInstance();
			IHBaseManager hBase = null;
			if (type.toLowerCase().equals("mro")) {
				hBase = new MroDao();
				hBase.init(input, "", type, 1, "new");
				// System.out.println(hBase.getTableName(type));
				lm.addTableImpoting(hBase.getTableName(type), hBase);
				hBase.startMigration();
				// hBase.inputHbaseMetaData();
				job.put("status", "OK");
			} else if (type.toLowerCase().equals("mrs")) {
				hBase = new MrsDao();
				hBase.init(input, "", type, 1, "new");
				System.out.println(hBase.getTableName(type));
				lm.addTableImpoting(hBase.getTableName(type), hBase);
				hBase.startMigration();
				// hBase.inputHbaseMetaData();
				job.put("status", "OK");
			}

		} catch (Exception e) {
			System.out.print(e.getMessage());
			try {
				job.put("msg", e.getMessage());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		DatasetManagerImpl.getInstance().refreshDatasetInHBase();
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/idlist")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getListMigration(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		JSONArray job = new JSONArray();
		LoadManager lm = LoadManager.getInstance();
		IHBaseManager hBase = null;
		for (String s : lm.getMigrationTableList()) {
			job.put(s);
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/getalltbs")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getExsitTables(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		JSONArray job = new JSONArray();
		try {
			JSONArray ja = HBaseTableOp.list();
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jb = ja.getJSONObject(i);
				JSONObject jobj = new JSONObject();
				jobj.put("id", jb.get("id"));
				jobj.put("builtTime", jb.get("builtTime"));
				jobj.put("rowCount", jb.get("lineCount"));
				job.put(jobj);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/process")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getProcess(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		LoadManager lm = LoadManager.getInstance();
		IHBaseManager hBase = lm.getIHB(id);
		JSONObject job = new JSONObject();
		try {
			if (hBase == null)
				job.put("status", "FAILED");
			else {
				job.put("status", hBase.getProcess());
				if (hBase.getProcess() == 1)
					lm.removeMigrationTable(id);
			}

		} catch (JSONException e) {
			try {
				job.put("msg", e.getMessage());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/kill")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding killImport(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		LoadManager lm = LoadManager.getInstance();
		IHBaseManager hBase = lm.getIHB(id);
		JSONObject job = new JSONObject();
		try {
			if (hBase == null)
				job.put("status", "FAILED");
			else {
				boolean bool = lm.removeMigrationTable(id);
				hBase.killMigration();
				job.put("status", "OK");
			}
		} catch (JSONException e) {
			try {
				job.put("msg", e.getMessage());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/rm")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding removeTable(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		JSONObject job = new JSONObject();
		try {
			job.put("status", "FAILED");
			int index = Integer.parseInt(HBaseTableOp.delete(id));
			Util.removeHtable(Util.getConList(), id);
			DatasetManagerImpl.getInstance().refreshDatasetInHBase();
			// dm.removeDatasetInHbase(id);
			if (index > 0)
				job.put("status", "OK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				job.put("msg", e.getMessage());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/path")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getPath(@QueryParam("path") String path,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		JSONObject job = null;
		try {
			job = FS.getList(path);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(job.toString());
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}
}
