package portfolio.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Project {
	
	public String name;
	public String description;
	public List<String> refHumanResources; //obj referencing users
	public String refManager; //obj referencing user
	public List<User> humanResources; //obj users
	public User manager; //obj user
	public Double totalValue;
	public Double availableValue;
	public Integer status;
	public String category;
	public Double workLoad;
	public List<String> projectRoles;
	public List<String> projectFiles; //array com a url dos arquivos
	public Date beginDate;
	public Date endDate;
	
	public static final String[] fields = { "name",
											"description",
											"ref_human_resources",
											"human_resources",
											"ref_manager",
											"manager",
											"total_value",
											"available_value",
											"status",
											"category",
											"work_load",
											"project_roles",
											"project_files",
											"begin_date",
											"end_date"
										  };
	
	public Project(){
		refHumanResources = new LinkedList<String>();
		humanResources = new LinkedList<User>();
		projectRoles = new LinkedList<String>();
		projectFiles = new LinkedList<String>();
		beginDate = new Date();
		endDate = new Date();
	}
	
	public void readJSON(JSONObject json) throws JSONException, ParseException{
		for(String field : fields)
		{
			if(field.equals("name"))
			{
				this.name = json.getString(field);
			}
			else if(field.equals("description"))
			{
				this.description = json.getString(field);
			}
			else if(field.equals("human_resources"))
			{
				List<String> ref_human_resources = new LinkedList<String>();
				JSONArray array = json.getJSONArray(field);
				for(Object obj : array)
					ref_human_resources.add((String) obj);
				this.refHumanResources = ref_human_resources;
			}
			else if(field.equals("manager"))
			{
				this.refManager = json.getString(field);
			}
			else if(field.equals("total_value"))
			{
				this.totalValue = json.getDouble(field);
			}
			else if(field.equals("available_value"))
			{
				this.availableValue = json.getDouble(field);
			}
			else if(field.equals("status"))
			{
				this.status = json.getInt(field);
			}
			else if(field.equals("category"))
			{
				this.category = json.getString(field);
			}
			else if(field.equals("work_load"))
			{
				this.workLoad = json.getDouble(field);
			}
			else if(field.equals("project_roles"))
			{
				List<String> roles = new LinkedList<String>();
				JSONArray array = json.getJSONArray(field);
				for(Object obj : array)
					roles.add((String) obj);
				this.projectRoles = roles;
			}
			else if(field.equals("project_files"))
			{
				List<String> files = new LinkedList<String>();
				JSONArray array = json.getJSONArray(field);
				for(Object obj : array)
					files.add((String) obj);
				this.projectFiles = files;
			}
			else if(field.equals("begin_date"))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			    Date convertedCurrentDate = sdf.parse(json.getString(field));
			    this.beginDate = convertedCurrentDate;
			}
			else if(field.equals("end_date"))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			    Date convertedCurrentDate = sdf.parse(json.getString(field));
			    this.endDate = convertedCurrentDate;
			}
		}
	}
	
}
