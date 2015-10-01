package be.maximvdw.ehbrooster.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import be.maximvdw.ehbrooster.ui.Console;

@Entity(name = "activities")
@Table(name = "activities")
public class Activity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToMany
	private List<Subject> subjects = new ArrayList<Subject>();
	@ManyToMany
	private List<StudyProgram> studyProgrammes = new ArrayList<StudyProgram>();
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "dayId")
	private Day day;
	private String name = "";
	private String lector = "";
	private String classRoom = "";
	private String lessonForm = "";
	private String beginTime = "";
	private String endTime = "";
	private long beginTimeUnix = 0;
	private long endTimeUnix = 0;
	private String weeks = "";
	@Column(name = "groups", length = 1024)
	private String groups = "";
	private String duration = "";
	private long syncDate = 0L;

	public Activity() {

	}

	public Activity(Subject subject, String name, String lessonForm, String begin, String end, String duration,
			String weeks, String lector, String classRoom, String groupsString, Day day) {
		setDay(day);
		setSyncDate(System.currentTimeMillis() / 1000);
		setWeeks(weeks);
		setDuration(duration);
		setName(name.equals("&nbsp;") ? "" : name);
		if (name.equals("")) {
			if (subject != null)
				setName(subject.getName());
		}
		setLector(lector.equals("&nbsp;") ? "" : lector);
		if (getLector().contains("/")) {
			setLector(getLector().substring(getLector().lastIndexOf("/") + 1));
		}
		setClassRoom(classRoom.equals("&nbsp;") ? "" : classRoom);
		setLessonForm(lessonForm.equals("&nbsp;") ? "" : lessonForm);
		setBeginTime(begin);
		setEndTime(end);
		String[] beginData = beginTime.split(":");
		String[] endData = endTime.split(":");
		try {
			setBeginTimeUnix(getDay().getDayTimeStamp() + (Integer.parseInt(beginData[0]) * 60 * 60)
					+ (Integer.parseInt(beginData[1]) * 60));
			setEndTimeUnix(getDay().getDayTimeStamp() + (Integer.parseInt(endData[0]) * 60 * 60)
					+ (Integer.parseInt(endData[1]) * 60));
		} catch (Exception ex) {
			Console.severe("Unable to get time for: " + getName());
			ex.printStackTrace();
		}
		setGroups(groupsString);
		if (subject != null)
			addSubject(subject);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLector() {
		return lector;
	}

	public void setLector(String lector) {
		this.lector = lector;
	}

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}

	public String getLessonForm() {
		return lessonForm;
	}

	public void setLessonForm(String lessonForm) {
		this.lessonForm = lessonForm;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public long getBeginTimeUnix() {
		return beginTimeUnix;
	}

	public void setBeginTimeUnix(long beginTimeUnix) {
		this.beginTimeUnix = beginTimeUnix;
	}

	public long getEndTimeUnix() {
		return endTimeUnix;
	}

	public void setEndTimeUnix(long endTimeUnix) {
		this.endTimeUnix = endTimeUnix;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public long getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(long syncDate) {
		this.syncDate = syncDate;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public boolean addSubjects(List<Subject> subjects) {
		for (Subject sub : subjects)
			addSubject(sub);
		return true;
	}

	public boolean addSubject(Subject subject) {
		if (!subjects.contains(subject)) {
			subjects.add(subject);
			return true;
		}
		return false;
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public boolean addStudyProgram(List<StudyProgram> programmes) {
		for (StudyProgram program : programmes)
			addStudyProgram(program);
		return true;
	}

	public boolean addStudyProgram(StudyProgram studyProgram) {
		if (!studyProgrammes.contains(studyProgram)) {
			studyProgrammes.add(studyProgram);
			return true;
		}
		return false;
	}

	public List<StudyProgram> getStudyProgrammes() {
		return studyProgrammes;
	}

	public void setStudyProgrammes(List<StudyProgram> studyProgrammes) {
		this.studyProgrammes = studyProgrammes;
	}
}
