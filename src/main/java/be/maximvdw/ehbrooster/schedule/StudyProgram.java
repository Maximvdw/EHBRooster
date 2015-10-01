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

@Entity(name = "studyprogrammes")
@Table(name = "studyprogrammes")
public class StudyProgram implements Comparable<StudyProgram> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String programId = "";
	private String name = "";
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "educationId")
	private Education education;
	@OneToMany(cascade = CascadeType.DETACH)
	private List<Group> groups = new ArrayList<Group>();
	private int listIndex = 0;

	public StudyProgram(String name, String programId) {
		setName(name);
		setProgramId(programId);
	}

	public StudyProgram() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public Education getEducation() {
		return education;
	}

	public void setEducation(Education education) {
		this.education = education;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void addGroup(Group group){
		if (!groups.contains(group)){
			groups.add(group);
		}
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public int getListIndex() {
		return listIndex;
	}

	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}


	@Override
	public int compareTo(StudyProgram o) {
		int idx1 = this.getListIndex();
		int idx2 = o.getListIndex();
		if (idx1 == idx2) {
			return 0;
		} else if (idx1 > idx2) {
			return 1;
		} else {
			return -1;
		}
	}
}
