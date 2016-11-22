package portfolio.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Program implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1679719866078904654L;
	public String name;
	public String description;
	public String refManager; //obj referencing user
	public User manager; //obj user
	public List<String> refProjects; //obj ref projects
	public List<Project> projects; //obj projects
	public Double totalValue;
	public Integer status;
	public String category;
	public Date beginDate;
	public Date endDate;
	
	public static final String[] fields = { "name",
											"description",
											"manager",
											"projects",
											"total_value",
											"status",
											"category",
											"begin_date",
											"end_date"
										  };

	public Program(){
		refProjects = new LinkedList<String>();
		projects = new LinkedList<Project>();
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
			else if(field.equals("manager"))
			{
				this.refManager = json.getString(field);
			}
			else if(field.equals("projects"))
			{
				List<String> ref_projects = new LinkedList<String>();
				JSONArray array = json.getJSONArray(field);
				for(Object obj : array)
					ref_projects.add((String) obj);
				this.refProjects = ref_projects;
			}
			else if(field.equals("total_value"))
			{
				this.totalValue = json.getDouble(field);
			}
			else if(field.equals("status"))
			{
				this.status = json.getInt(field);
			}
			else if(field.equals("category"))
			{
				this.category = json.getString(field);
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
	
	public String toString(){
		return null;
	}
}
