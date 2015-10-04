package be.maximvdw.ehbrooster.schedule;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "lectors")
@Table(name = "lectors")
public class Lector implements Comparable<Lector> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name = "";
	private String lectorId = "";
	private int listIndex = 0;

	public Lector() {

	}

	public Lector(String name, String lectorId) {
		setName(name);
		setLectorId(lectorId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLectorId() {
		return lectorId;
	}

	public void setLectorId(String lectorId) {
		this.lectorId = lectorId;
	}

	public int getListIndex() {
		return listIndex;
	}

	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}
	
	@Override
	public int compareTo(Lector o) {
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
