package be.maximvdw.ehbrooster.schedule;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "sync")
@Table(name = "sync")
public class Sync {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private long timeStamp = 0L;
	private int added = 0;
	private int removed = 0;
	private long duration = 0;
	private int groups = 0;
	private int subjects = 0;
	private int educations = 0;
	private int studyProgrammes = 0;
	private int activities = 0;

	public Sync(long timeStamp, int added, int removed, long duration) {
		setTimeStamp(timeStamp);
		setAdded(added);
		setRemoved(removed);
		setDuration(duration);
	}

	public Sync() {

	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getAdded() {
		return added;
	}

	public void setAdded(int added) {
		this.added = added;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getRemoved() {
		return removed;
	}

	public void setRemoved(int removed) {
		this.removed = removed;
	}

	public int getActivities() {
		return activities;
	}

	public void setActivities(int activities) {
		this.activities = activities;
	}

	public int getStudyProgrammes() {
		return studyProgrammes;
	}

	public void setStudyProgrammes(int studyProgrammes) {
		this.studyProgrammes = studyProgrammes;
	}

	public int getEducations() {
		return educations;
	}

	public void setEducations(int educations) {
		this.educations = educations;
	}

	public int getSubjects() {
		return subjects;
	}

	public void setSubjects(int subjects) {
		this.subjects = subjects;
	}

	public int getGroups() {
		return groups;
	}

	public void setGroups(int groups) {
		this.groups = groups;
	}

}
