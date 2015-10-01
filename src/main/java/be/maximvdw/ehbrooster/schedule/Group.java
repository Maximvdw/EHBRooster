package be.maximvdw.ehbrooster.schedule;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import be.maximvdw.ehbrooster.ui.Console;

/**
 * Studie richting group
 * 
 * @author Maxim Van de Wynckel
 */

@Entity(name = "groups")
@Table(name = "groups")
public class Group {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "groupId")
	private String groupId = "";
	@Column(name = "groupName")
	private String name = "";
	@Column(name = "groupOriginalName")
	private String originalName = "";
	@OneToMany(cascade = CascadeType.DETACH)
	private List<Subject> subjects = new ArrayList<Subject>();
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "departmentId")
	private Department department;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "educationId")
	private Education education;
	private int listIndex = 0;
	private String alias = "";

	public Group(String name, String groupId) {
		setOriginalName(name.substring(name.indexOf("/") + 1));
		setAlias(originalName);
		setName(ScheduleManager.filterGroupName(getOriginalName()));
		String departmentString = name.substring(0, name.indexOf("/"));
		department = ScheduleManager.getInstance().getDepartmentByCode(departmentString);
		if (department == null) {
			Console.warning("No department for '" + departmentString + "' in group " + getOriginalName());
		} else {
			department.getGroups().add(this);
		}
		setGroupId(groupId);
	}

	public Group() {

	}

	public Education getEducation() {
		return education;
	}

	public Department getDepartment() {
		return department;
	}

	public void setEducation(Education education) {
		education.getGroups().add(this);
		this.education = education;
	}

	private void fetchSubjects(boolean force) {
		subjects.clear();
		List<Subject> subjects = ScheduleManager.getInstance().getSubjects(force);
		for (Subject subject : subjects) {
			if (subject.getFullName().contains(getName())) {
				this.subjects.add(subject);
			}
		}
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Subject> getSubjects() {
		if (subjects.isEmpty()) {
			fetchSubjects(false);
		}
		return subjects;
	}

	public List<Subject> getSubjects(boolean force) {
		if (subjects.isEmpty()) {
			fetchSubjects(force);
		}
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public int getListIndex() {
		return listIndex;
	}

	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
