package portfolio.models;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {
	
	public String email;
	public Integer usertype;
	public String username;
	public List<String> projects; //obj ref projects
	public Double availableWorkLoad;
	public List<String> possibleRoles;
	
	public static final String[] fields = { "email",
											"usertype",
											"username",
											"projects",
											"available_work_load",
											"possible_roles"
							 			  };
		
	private static User UserInstance = null;
	
	private User(){
		projects = new LinkedList<String>();
		possibleRoles = new LinkedList<String>();
	}
	
	public void readJSON(JSONObject json){
		for(String field : fields)
		{
			if(field.equals("email"))
				this.email = json.getString(field);
			else if(field.equals("usertype"))
				this.usertype = json.getInt(field);
			else if(field.equals("username"))
				this.username = json.getString(field);
			else if(field.equals("available_work_load"))
				this.availableWorkLoad = json.getDouble(field);
			else if(field.equals("possible_roles"))
			{
				List<String> roles = new LinkedList<String>();
				JSONArray array = json.getJSONArray(field);
				for(Object obj : array)
					roles.add((String) obj);
				this.possibleRoles = roles;
			}
			else if(field.equals("projects"))
			{
				List<String> projects = new LinkedList<String>();
				JSONArray array = json.getJSONArray(field);
				for(Object obj : array)
					projects.add((String) obj);
				this.projects = projects;
			}
		}
	}
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		for(String field : fields)
		{
			if(field.equals("email"))
				json.put(field, this.email);
			else if(field.equals("usertype"))
				json.put(field, this.usertype);
			else if(field.equals("username"))
				json.put(field, this.username);
			else if(field.equals("available_work_load"))
				json.put(field, this.availableWorkLoad);
			else if(field.equals("possible_roles"))
				json.put(field, this.possibleRoles);
			else if(field.equals("projects"))
				json.put(field, this.projects);
		}
		return json;
	}
	
	public static User getInstance(){
		if(UserInstance == null)
			UserInstance = new User();
		return UserInstance;
	}
	
	public String toString(){
		String s = "";
		String roles = "";
		String projects = "";
		for(String role : this.possibleRoles)
			roles += role + " ";
		for(String project : this.projects)
			projects += project + " ";
		s += this.email + "\n" +
			 Integer.toString(this.usertype) + "\n" +
			 this.username + "\n" +
			 Double.toString(this.availableWorkLoad) + "\n" +
			 "[ " + roles + "]" + "\n" +
			 "[ " + projects + "]" + "\n"
			;
		return s;
	}
	
}
