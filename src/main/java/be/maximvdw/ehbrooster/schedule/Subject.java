package be.maximvdw.ehbrooster.schedule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "subjects")
@Table(name = "subjects")
public class Subject implements Comparable<Subject> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "subjectId")
	private String subjectId = "";
	@Column(name = "subjectName")
	private String name = "";
	@Column(name = "subjectFullName")
	private String fullName = "";
	private int listIndex = 0;

	public Subject(String name, String subjectId, Group group) {
		setFullName(name);
		setName(name.substring(name.lastIndexOf("/") + 1));
		setSubjectId(subjectId);
	}

	public Subject() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public int compareTo(Subject o) {
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

	public int getListIndex() {
		return listIndex;
	}

	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}
}
