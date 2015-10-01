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

@Entity(name = "timetable")
@Table(name = "timetable")
public class TimeTable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "timeTable")
	private List<Week> weeks = new ArrayList<Week>();
	private long lastSync = System.currentTimeMillis() / 1000;
	private long startTimeStamp = 1442786400;

	public TimeTable() {
		for (int i = 0; i < 52; i++) {
			Week week = new Week(i + 1, this);
			weeks.add(week);
		}
	}

	public Week getWeek(int weekNumber) {
		if (weekNumber >= 1) {
			if (weekNumber <= weeks.size()) {
				return weeks.get(weekNumber - 1);
			}
		}
		return null;
	}

	public List<Week> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<Week> weeks) {
		this.weeks = weeks;
	}

	public long getLastSync() {
		return lastSync;
	}

	public void setLastSync(long lastSync) {
		this.lastSync = lastSync;
	}

	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}
}
