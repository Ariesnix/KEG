package edu.thu.keg.link.client.taskQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.codehaus.jettison.json.JSONException;

import edu.thu.keg.link.client.taskQuery.TaskQuery.TaskInfo;


public class Test
{

	public static void main(String[] args) throws JSONException
	{
		// TODO Auto-generated method stub
		String taskId="application_1409032717862_0002";
		TaskInfo ti=TaskQuery.getTaskInfo(taskId);
	/*	System.out.println(ti.getAmHostHttpAddress());
		System.out.println(ti.getApplicationType());
		System.out.println(ti.getId());
		System.out.println(ti.getState());
		System.out.println(ti.getFinalStatus());
		System.out.println(ti.getName());
		System.out.println(ti.getStartedTime());
		*/
		System.out.println(ti);
		
		

		

	}
	
	/*public static void main(String[] args) throws InterruptedException
	{
		String[] cmd = {
				"/bin/sh",
				"-c",
				"/usr/local/spark-1.0.1/bin/spark-submit 2>&1"
				};
		
		String[] cmd2 = {
				"/bin/sh",
				"-c",
				"/usr/local/hadoop-2.2.0/bin/yarn 2>&1"
				};
		
		String cmdd="sh -c /usr/local/spark-1.0.1/bin/spark-submit 2>&1";
		
				 
		//exec(cmd2);
		
		
		
	}
	*/
	private static String exec(String[] command) throws InterruptedException
	{
		BufferedReader br=null;
		try
		{
			Process process=Runtime.getRuntime().exec(command);
		
			 br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String temp=null;
			
			while(true)
			{
				
				temp=br.readLine();
				if(temp==null)
				{
					break;
				}

				System.out.println(temp);
			}
			
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(br!=null){
			try
			{
				
				br.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
		return null;
	}

	
}
