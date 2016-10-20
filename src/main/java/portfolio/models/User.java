package portfolio.models;

import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import portfolio.PortfolioWebManager;

public class User {
	
	public String email;
	public Integer usertype;
	public String username;
	public List<String> ref_projects; //obj referencing projects
	public List<Project> projects; //obj projects
	public Double availableWorkLoad;
	public List<String> possibleRoles;
	
	public static final String[] fields = { "email",
											"usertype",
											"username",
											"ref_projects",
											"projects",
											"available_work_load",
											"possible_roles"
							 			  };
	public enum UserType{
		PortfolioManager(0),
		Investor(1),
		ProjectManager(2),
		CommonUser(3);
		
		private final int id;
	    UserType(int id) { this.id = id; }
	    public int getValue() { return id; }
	}
	
	private static User UserInstance = null;
	
	private User(){
		ref_projects = new LinkedList<String>();
		projects = new LinkedList<Project>();
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
				this.ref_projects = projects;
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
			else if(field.equals("ref_projects"))
				json.put(field, this.ref_projects);
		}
		return json;
	}
	
	public static User getInstance(){
		if(UserInstance == null)
			UserInstance = new User();
		return UserInstance;
	}
	
	public void savePreferences(){
    	Preferences prefs = Preferences.userNodeForPackage(portfolio.models.User.class);
    	for(String field : User.fields)
    	{
    		if(field.equals("email"))
				prefs.put(field, this.email);
			else if(field.equals("usertype"))
				prefs.putInt(field, this.usertype);
			else if(field.equals("username"))
				prefs.put(field, this.username);
			else if(field.equals("available_work_load"))
				prefs.putDouble(field, this.availableWorkLoad);
			else if(field.equals("ref_projects"))
				prefs.putByteArray(field, PortfolioWebManager.serializeObject(this.ref_projects));
			else if(field.equals("possible_roles"))
				prefs.putByteArray(field, PortfolioWebManager.serializeObject(this.possibleRoles));
			else if(field.equals("projects"))
				prefs.putByteArray(field, PortfolioWebManager.serializeObject(this.projects));
    	}
    }
    
    @SuppressWarnings("unchecked")
	public void getPreferences(){
    	Preferences prefs = Preferences.userNodeForPackage(portfolio.models.User.class);
    	for(String field : User.fields)
    	{
    		if(field.equals("email"))
				this.email = prefs.get(field, "");
			else if(field.equals("usertype"))
				this.usertype = prefs.getInt(field, -1);
			else if(field.equals("username"))
				this.username = prefs.get(field, "");
			else if(field.equals("available_work_load"))
				this.availableWorkLoad = prefs.getDouble(field, -1);
			else if(field.equals("possible_roles"))
				this.possibleRoles = (List<String>) PortfolioWebManager.deserializeObject(prefs.getByteArray(field, null));
			else if(field.equals("ref_projects"))
				this.ref_projects = (List<String>) PortfolioWebManager.deserializeObject(prefs.getByteArray(field, null));
			else if(field.equals("projects"))
				this.projects = (List<Project>) PortfolioWebManager.deserializeObject(prefs.getByteArray(field, null));
    	}
    }
	
	public String toString(){
		String s = "";
		String roles = "";
		String projects = "";
		for(String role : this.possibleRoles)
			roles += role + " ";
		if(this.projects.size() == 0)
			for(String project : this.ref_projects)
				projects += project + " ";
		else
			for(Project project : this.projects)
				projects += project.toString() + "\n";
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
