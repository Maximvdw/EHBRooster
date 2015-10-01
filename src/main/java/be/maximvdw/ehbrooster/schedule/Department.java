package be.maximvdw.ehbrooster.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name = "departments")
@Table(name = "departments")
public class Department {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "department")
	private List<Group> groups = new ArrayList<Group>();
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "department")
	private List<Education> educations = new ArrayList<Education>();
	private String code = "";
	private String name = "";

	public Department(){
		
	}
	public Department(String name, String code) {
		setName(name);
		setCode(code);
	}

	public List<Group> getGroups() {
		return groups;
	}

	public List<Education> getEducations() {
		return educations;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
