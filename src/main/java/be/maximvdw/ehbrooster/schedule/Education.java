package be.maximvdw.ehbrooster.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name = "educations")
@Table(name = "educations")
public class Education {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "departmentId")
	private Department department;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "education")
	private List<Group> groups = new ArrayList<Group>();
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "education")
	private List<StudyProgram> studyProgrammes = new ArrayList<StudyProgram>();
	private String name = "";

	public Education(String name, String departmentCode) {
		setDepartment(ScheduleManager.getInstance().getDepartmentByCode(departmentCode));
		setName(name);
	}

	public Education() {

	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<StudyProgram> getStudyProgrammes() {
		return studyProgrammes;
	}

	public void setStudyProgrammes(List<StudyProgram> studyProgrammes) {
		this.studyProgrammes = studyProgrammes;
	}
	
}
