package portfolio.models;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Project {
	
	public String name;
	public String description;
	public List<String> humanResources; //obj ref users
	public String manager; //obj ref user
	public Double totalValue;
	public Double availableValue;
	public Integer status;
	public String category;
	public Double workLoad;
	public List<String> projectRoles;
	public List<String> projectFiles; //array com a url dos arquivos
	public Date beginDate;
	public Date endDate;
	
	public Map<String, Object> values;
	
	public Project(){
		humanResources = new LinkedList<String>();
		projectRoles = new LinkedList<String>();
		projectFiles = new LinkedList<String>();
		beginDate = new Date();
		endDate = new Date();
		values = new HashMap<String, Object>();
	}
	
}
