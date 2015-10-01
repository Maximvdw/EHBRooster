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

@Entity(name = "days")
@Table(name = "days")
public class Day {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "day")
	private List<Activity> activities = new ArrayList<Activity>();
	private int dayInWeek = 0;
	private long dayTimeStamp = 0;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "weekId")
	private Week week;
	private long syncDate = 0L;

	public Day() {

	}

	public Day(int day, Week week) {
		setDayInWeek(day);
		setWeek(week);
		dayTimeStamp = week.getWeekTimeStamp();
		dayTimeStamp += ((day - 1) * 24 * 60 * 60);
	}

	public Day addActivity(Activity activity) {
		for (int i = 0; i < activities.size(); i++) {
			Activity presentActivity = activities.get(i);
			if (presentActivity.getBeginTimeUnix() == activity.getBeginTimeUnix()
					&& presentActivity.getEndTimeUnix() == activity.getEndTimeUnix()
					&& presentActivity.getLector().equals(activity.getLector())) {
				if (((!presentActivity.getLector().equals("") && !presentActivity.getLector().equals(" ")
						&& !presentActivity.getLector().equals(" ")))
						|| presentActivity.getName().equalsIgnoreCase(activity.getName())) {
					if (presentActivity.getName().length() < activity.getName().length()) {
						presentActivity.setName(activity.getName());
					}
					if (!presentActivity.getGroups().contains(activity.getGroups())) {
						presentActivity.setGroups(presentActivity.getGroups() + "," + activity.getGroups());
					}
					presentActivity.setSyncDate(activity.getSyncDate());
					presentActivity.addSubjects(activity.getSubjects());
					presentActivity.addStudyProgram(activity.getStudyProgrammes());
					setSyncDate(System.currentTimeMillis() / 1000);
					activities.set(i, presentActivity);
					return this;
				}
			}
		}
		setSyncDate(System.currentTimeMillis() / 1000);
		activities.add(activity);
		return this;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	public int getDayInWeek() {
		return dayInWeek;
	}

	public void setDayInWeek(int dayInWeek) {
		this.dayInWeek = dayInWeek;
	}

	public long getDayTimeStamp() {
		return dayTimeStamp;
	}

	public void setDayTimeStamp(long dayTimeStamp) {
		this.dayTimeStamp = dayTimeStamp;
	}

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}

	public long getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(long syncDate) {
		this.syncDate = syncDate;
	}

}