package portfolio.models;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Program {
	
	public String name;
	public String description;
	public List<String> projects; //obj ref projects
	public Double totalValue;
	public Integer status;
	public String category;
	public Date beginDate;
	public Date endDate;
	
	public Map<String, Object> values;

	public Program(){
		projects = new LinkedList<String>();
		values = new HashMap<String, Object>();
	}
}
