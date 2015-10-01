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

@Entity(name = "weeks")
@Table(name = "weeks")
public class Week {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "week")
	private List<Day> days = new ArrayList<Day>();
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "timeTableId")
	private TimeTable timeTable;
	private int weekInYear = 0;
	private long weekTimeStamp = 0;

	public Week() {

	}

	public Week(int week, TimeTable timeTable) {
		setTimeTable(timeTable);
		weekTimeStamp = timeTable.getStartTimeStamp();
		weekTimeStamp += ((week - 1) * 7 * 24 * 60 * 60);
		setWeekInYear(week);
		for (int i = 0; i < 7; i++) {
			days.add(new Day( i + 1, this));
		}
	}

	public Day getWeekDay(int weekDay) {
		if (weekDay >= 1) {
			if (weekDay <= days.size()) {
				return days.get(weekDay - 1);
			}
		}
		return null;
	}

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}



	public List<Activity> getActivitiesInWeek() {
		List<Activity> activities = new ArrayList<Activity>();
		for (Day day : getDays()) {
			activities.addAll(day.getActivities());
		}
		return activities;
	}

	public int getWeekInYear() {
		return weekInYear;
	}

	public void setWeekInYear(int weekInYear) {
		this.weekInYear = weekInYear;
	}

	public long getWeekTimeStamp() {
		return weekTimeStamp;
	}

	public void setWeekTimeStamp(long weekTimeStamp) {
		this.weekTimeStamp = weekTimeStamp;
	}

	public TimeTable getTimeTable() {
		return timeTable;
	}

	public void setTimeTable(TimeTable timeTable) {
		this.timeTable = timeTable;
	}
}
