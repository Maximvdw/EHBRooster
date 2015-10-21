package be.maximvdw.ehbrooster.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import be.maximvdw.ehbrooster.EHBRooster;
import be.maximvdw.ehbrooster.ui.Console;
import be.maximvdw.ehbrooster.utils.HtmlResponse;
import be.maximvdw.ehbrooster.utils.HtmlUtils;

/**
 * EHB Rooster Converter
 * 
 * Converteer de HTML versie van het lessenrooster naar een MySQL database.
 * 
 * @version 18/09/2015
 * @author Maxim Van de Wynckel
 */
public class ScheduleManager {
	private List<Group> groups = new ArrayList<Group>();
	private List<Subject> subjects = new ArrayList<Subject>();
	private List<Department> departments = new ArrayList<Department>();
	private List<Education> educations = new ArrayList<Education>();
	private List<StudyProgram> programmes = new ArrayList<StudyProgram>();
	private List<Lector> lectors = new ArrayList<Lector>();
	private TimeTable timeTable = null;
	private EntityManagerFactory managerFactory = null;
	private EntityManager entityManager = null;
	private static ScheduleManager instance = null;

	public ScheduleManager(String hostname, int port, String database, String username, String password) {
		instance = this;
		timeTable = new TimeTable();
		managerFactory = null;
		Map<String, String> persistenceMap = new HashMap<String, String>();

		persistenceMap.put("javax.persistence.jdbc.url", "jdbc:mysql://" + hostname + ":" + port + "/" + database);
		persistenceMap.put("javax.persistence.jdbc.user", username);
		persistenceMap.put("javax.persistence.jdbc.password", password);
		persistenceMap.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");

		managerFactory = Persistence.createEntityManagerFactory("myPU", persistenceMap);

		loadDatabase();
	}

	@SuppressWarnings("unchecked")
	public void loadDatabase() {
		clear();
		entityManager = managerFactory.createEntityManager();
		// Laad reeds opgehaalde gegevens uit de database
		Query subjectIdsQuery = entityManager.createQuery("SELECT id FROM subjects");
		Collection<Integer> subjectIds = subjectIdsQuery.getResultList();
		for (Integer id : subjectIds) {
			subjects.add(entityManager.find(Subject.class, id));
		}

		Query groupIdsQuery = entityManager.createQuery("SELECT id FROM groups");
		Collection<Integer> groupIds = groupIdsQuery.getResultList();
		for (Integer id : groupIds) {
			groups.add(entityManager.find(Group.class, id));
		}

		Query educationIdsQuery = entityManager.createQuery("SELECT id FROM educations");
		Collection<Integer> educationIds = educationIdsQuery.getResultList();
		for (Integer id : educationIds) {
			educations.add(entityManager.find(Education.class, id));
		}

		Query departmentIdsQuery = entityManager.createQuery("SELECT id FROM departments");
		Collection<Integer> departmentIds = departmentIdsQuery.getResultList();
		for (Integer id : departmentIds) {
			departments.add(entityManager.find(Department.class, id));
		}

		Query programmesIdsQuery = entityManager.createQuery("SELECT id FROM studyprogrammes");
		Collection<Integer> programmesIds = programmesIdsQuery.getResultList();
		for (Integer id : programmesIds) {
			programmes.add(entityManager.find(StudyProgram.class, id));
		}

		Query lectorQuery = entityManager.createQuery("SELECT id FROM lectors");
		Collection<Integer> lectorIds = lectorQuery.getResultList();
		for (Integer id : lectorIds) {
			lectors.add(entityManager.find(Lector.class, id));
		}

		Query tableQuery = entityManager.createQuery("SELECT id FROM timetable");
		Collection<Integer> tableIds = tableQuery.getResultList();
		for (Integer id : tableIds) {
			timeTable = entityManager.find(TimeTable.class, id);
		}

	}

	public void clear() {
		groups.clear();
		subjects.clear();
		departments.clear();
		educations.clear();
		programmes.clear();
		timeTable = new TimeTable();
	}

	public static ScheduleManager getInstance() {
		return instance;
	}

	public Education createEducation(String name, String departmentCode) {
		for (Education edu : getEducations()) {
			if (edu.getName().equals(name)) {
				return edu;
			}
		}
		Education newEducation = new Education(name, departmentCode);
		educations.add(newEducation);
		return newEducation;
	}

	public Department createDepartment(String name, String code) {
		for (Department dep : getDepartments()) {
			if (dep.getCode().equals(code)) {
				return dep;
			}
		}
		Department newDepartment = new Department(name, code);
		departments.add(newDepartment);
		return newDepartment;
	}

	public Subject createSubject(String name, String subjectId, Group group) {
		for (Subject subject : getSubjects(false)) {
			if (subject.getSubjectId().equals(subjectId)) {
				return subject;
			}
		}
		Subject newSubject = new Subject(name, subjectId, group);
		subjects.add(newSubject);
		return newSubject;
	}

	public void saveStudyProgram(StudyProgram studyProgram) {
		try {
			if (entityManager.getTransaction().isActive()) {
				if (!entityManager.getTransaction().getRollbackOnly())
					entityManager.getTransaction().commit();
				else
					entityManager.flush();
			}
			entityManager.getTransaction().begin();
			if (studyProgram.getEducation() != null)
				entityManager.persist(studyProgram.getEducation());
			entityManager.persist(studyProgram);
			entityManager.flush();
			entityManager.getTransaction().commit();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public void saveGroups(List<Group> groups) {
		try {
			if (entityManager.getTransaction().isActive()) {
				if (!entityManager.getTransaction().getRollbackOnly())
					entityManager.getTransaction().commit();
				else
					entityManager.flush();
			}
			entityManager.getTransaction().begin();
			for (Group group : groups) {
				if (group.getDepartment() != null)
					entityManager.persist(group.getDepartment());
				if (group.getEducation() != null)
					entityManager.persist(group.getEducation());

				for (Subject subject : group.getSubjects()) {
					entityManager.persist(subject);
				}

				entityManager.persist(group);
			}
			// entityManager.flush();
			entityManager.getTransaction().commit();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public void saveGroup(Group group) {
		try {
			if (entityManager.getTransaction().isActive()) {
				if (!entityManager.getTransaction().getRollbackOnly())
					entityManager.getTransaction().commit();
				else
					entityManager.flush();
			}
			entityManager.getTransaction().begin();
			if (group.getDepartment() != null)
				entityManager.persist(group.getDepartment());
			if (group.getEducation() != null)
				entityManager.persist(group.getEducation());

			for (Subject subject : group.getSubjects()) {
				entityManager.persist(subject);
			}

			entityManager.persist(group);
			// entityManager.flush();
			entityManager.getTransaction().commit();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public void saveTimeTable(Sync sync) {
		try {
			if (entityManager.getTransaction().isActive()) {
				if (!entityManager.getTransaction().getRollbackOnly())
					entityManager.getTransaction().commit();
				else
					entityManager.flush();
			}
			entityManager.getTransaction().begin();
			int added = 0;
			int removed = 0;
			for (Week week : timeTable.getWeeks()) {
				for (Day day : week.getDays()) {
					if (day.getSyncDate() > timeTable.getLastSync())
						for (Activity activity : day.getActivities()) {
							try {
								if (activity.getSyncDate() > timeTable.getLastSync()) {
									if (activity.getId() == 0) {
										Console.info(activity.getName() + " is a new activity! Adding");
										added++;
									}
									entityManager.persist(activity);
								} else {
									Console.info(activity.getName() + " [" + activity.getId()
											+ "] is not updated! Removing ...");
									entityManager.remove(activity);
									removed++;
								}
							} catch (Exception ex) {
								Console.severe("ERROR IN: " + activity.getId() + "#  " + activity.getName());
								ex.printStackTrace();
							}
						}
					entityManager.persist(day);
				}
				entityManager.persist(week);
			}
			sync.setAdded(added);
			sync.setRemoved(removed);
			timeTable.setLastSync(System.currentTimeMillis() / 1000);
			entityManager.persist(timeTable);
			entityManager.persist(sync);
			entityManager.flush();
			entityManager.getTransaction().commit();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public boolean fetchLectorTimeTable(int week, List<Lector> lectors) {
		Collections.sort(lectors);
		try {
			Connection.Response res = null;

			// Get cookies again if lost
			res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Method.GET).execute();
			if (res == null) {
				Console.warning("Unable to get EHB groups from site!");
			}
			Document docCookieFetch = res.parse();
			if (docCookieFetch == null) {
				Console.warning("Unable to get EHB groups from site!");
			}

			// Required for cookie saving
			String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
			String EVENTVALIDATION = (docCookieFetch.getElementById("__EVENTVALIDATION").attr("value"));
			Map<String, String> cookies = res.cookies();

			res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).data("__EVENTTARGET", "LinkBtn_Staff")
					.data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
					.data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
					.data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
					.data("dlPeriod", "1-56").data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies)
					.method(Method.POST).execute();
			Document doc = res.parse();
			if (doc == null) {
				Console.warning("Unable to get subjects from site [#1]!");
				return false;
			}

			VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value");
			EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value");
			Connection conn = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).data("__EVENTTARGET", "")
					.data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
					.data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "Staff").data("dlFilter", "")
					.data("tWildcard", "").data("lbWeeks", " " + week).data("lbDays", "1-7").data("dlPeriod", "1-56")
					.data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS").data("bGetTimetable", "Toon+rooster")
					.data("pDays", "1-7").cookies(cookies).method(Method.POST);
			for (Lector lector : lectors) {
				conn.data("dlObject", lector.getLectorId());
			}

			res = conn.execute();
			doc = res.parse();
			if (doc == null) {
				Console.warning("Unable to get timetable for week '" + (week) + "' from site [#2]!");
				return false;
			}

			HtmlResponse getResponse = HtmlUtils.sendGetRequest(EHBRooster.getBaseTimeTableURL(), cookies, 120000);
			Document timeTableDoc = Jsoup.parse(getResponse.getSource());
			List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
			List<Element> subjectTitleElements = timeTableDoc.getElementsByClass("header-2-0-1");
			TimeTable timeTable = ScheduleManager.getInstance().getTimeTable();
			Week weekTimeTable = timeTable.getWeek(week);
			for (int subjectNr = 0; subjectNr < lectors.size(); subjectNr++) {
				// Lector lectorObj = lectors.get(subjectNr);
				subjectTitleElements.get(subjectNr).text();
				for (int i = 1; i <= 7; i++) {
					Element dayTable = dayTables.get(i - 1 + (subjectNr * 7));
					Day dayTimeTable = weekTimeTable.getWeekDay(i);
					if (dayTable.getElementsByTag("tbody").size() != 0) {
						Element tbodyElement = dayTable.getElementsByTag("tbody").first();
						List<Element> rows = tbodyElement.getElementsByTag("tr");
						for (int row = 1; row < rows.size(); row++) {
							Element rowElement = rows.get(row);
							List<Element> columnElements = rowElement.getElementsByTag("td");
							if (columnElements.size() == 9) {
								String activity = columnElements.get(0).text();
								String lessonForm = columnElements.get(1).text();
								String begin = columnElements.get(2).text();
								String end = columnElements.get(3).text();
								if (!begin.contains(":") || !end.contains(":")) {
									Console.severe("ERROR: " + activity + " NPE BEGIN/END DATE");
								}
								String duration = columnElements.get(4).text();
								String weeks = columnElements.get(5).text();
								String lector = columnElements.get(6).text();
								String classRoom = columnElements.get(7).text();
								String groupsString = columnElements.get(8).text();
								Activity activityObj = new Activity(null, activity, lessonForm, begin, end, duration,
										weeks, lector, classRoom, groupsString, dayTimeTable);
								dayTimeTable.addActivity(activityObj);
							}
						}
					}
				}
			}
			return true;
		} catch (Exception ex) {
			Console.warning("Unable to get timetable for week '" + (week) + "' from site [#3]!");
			ex.printStackTrace();
			return false;
		}
	}

	public boolean fetchTimeTable(int week, List<Subject> subjects) {
		Collections.sort(subjects);
		try {
			Connection.Response res = null;

			// Get cookies again if lost
			res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Method.GET).execute();
			if (res == null) {
				Console.warning("Unable to get EHB groups from site!");
			}
			Document docCookieFetch = res.parse();
			if (docCookieFetch == null) {
				Console.warning("Unable to get EHB groups from site!");
			}

			// Required for cookie saving
			String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
			String EVENTVALIDATION = (docCookieFetch.getElementById("__EVENTVALIDATION").attr("value"));
			Map<String, String> cookies = res.cookies();

			res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).data("__EVENTTARGET", "LinkBtn_StudentSets")
					.data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
					.data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
					.data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
					.data("dlPeriod", "1-56").data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies)
					.method(Method.POST).execute();
			Document doc = res.parse();
			if (doc == null) {
				Console.warning("Unable to get subjects from site [#1]!");
				return false;
			}

			VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value");
			EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value");
			Connection conn = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).data("__EVENTTARGET", "")
					.data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
					.data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "StudentSets").data("dlFilter", "")
					.data("dlFilter2", "").data("tWildcard", "").data("lbWeeks", " " + week).data("lbDays", "1-7")
					.data("dlPeriod", "1-56").data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS")
					.data("bGetTimetable", "Toon+rooster").data("pDays", "1-7").cookies(cookies).method(Method.POST);
			for (Subject subject : subjects) {
				conn.data("dlObject", subject.getSubjectId());
			}

			res = conn.execute();
			doc = res.parse();
			if (doc == null) {
				Console.warning("Unable to get timetable for week '" + (week) + "' from site [#2]!");
				return false;
			}

			HtmlResponse getResponse = HtmlUtils.sendGetRequest(EHBRooster.getBaseTimeTableURL(), cookies, 120000);
			Document timeTableDoc = Jsoup.parse(getResponse.getSource());
			List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
			List<Element> subjectTitleElements = timeTableDoc.getElementsByClass("header-2-0-1");
			TimeTable timeTable = ScheduleManager.getInstance().getTimeTable();
			Week weekTimeTable = timeTable.getWeek(week);
			for (int subjectNr = 0; subjectNr < subjects.size(); subjectNr++) {
				Subject subject = subjects.get(subjectNr);
				subjectTitleElements.get(subjectNr).text();
				for (int i = 1; i <= 7; i++) {
					Element dayTable = dayTables.get(i - 1 + (subjectNr * 7));
					Day dayTimeTable = weekTimeTable.getWeekDay(i);
					if (dayTable.getElementsByTag("tbody").size() != 0) {
						Element tbodyElement = dayTable.getElementsByTag("tbody").first();
						List<Element> rows = tbodyElement.getElementsByTag("tr");
						for (int row = 1; row < rows.size(); row++) {
							Element rowElement = rows.get(row);
							List<Element> columnElements = rowElement.getElementsByTag("td");
							if (columnElements.size() == 9) {
								String activity = columnElements.get(0).text();
								String lessonForm = columnElements.get(1).text();
								String begin = columnElements.get(2).text();
								String end = columnElements.get(3).text();
								if (!begin.contains(":") || !end.contains(":")) {
									Console.severe("ERROR: " + activity + " NPE BEGIN/END DATE");
								}
								String duration = columnElements.get(4).text();
								String weeks = columnElements.get(5).text();
								String lector = columnElements.get(6).text();
								String classRoom = columnElements.get(7).text();
								String groupsString = columnElements.get(8).text();
								Activity activityObj = new Activity(subject, activity, lessonForm, begin, end, duration,
										weeks, lector, classRoom, groupsString, dayTimeTable);
								dayTimeTable.addActivity(activityObj);
							}
						}
					}
				}
			}
			return true;
		} catch (Exception ex) {
			Console.warning("Unable to get timetable for week '" + (week) + "' from site [#3]!");
			ex.printStackTrace();
			return false;
		}
	}

	public StudyProgram getStudyProgramByName(String name) {
		for (StudyProgram program : getProgrammes(false)) {
			if (program.getName().equals(name))
				return program;
		}
		return null;
	}

	public boolean fetchStudyProgramTimeTable(int week, List<StudyProgram> studyProgrammes) {
		Collections.sort(studyProgrammes);
		try {
			Connection.Response res = null;

			// Get cookies again if lost
			res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Method.GET).execute();
			if (res == null) {
				Console.warning("Unable to get EHB groups from site!");
			}
			Document docCookieFetch = res.parse();
			if (docCookieFetch == null) {
				Console.warning("Unable to get EHB groups from site!");
			}

			// Required for cookie saving
			String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
			String EVENTVALIDATION = (docCookieFetch.getElementById("__EVENTVALIDATION").attr("value"));
			Map<String, String> cookies = res.cookies();

			Connection conn = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).data("__EVENTTARGET", "")
					.data("__EVENTARGUMENT", "").data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
					.data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
					.data("dlFilter", "").data("tWildcard", "").data("lbWeeks", " " + week).data("lbDays", "1-7")
					.data("dlPeriod", "1-56").data("RadioType", "TextSpreadsheet;swsurl;SWS_EHB_TS")
					.data("bGetTimetable", "Toon+rooster").data("pDays", "1-7").cookies(cookies).method(Method.POST);
			for (StudyProgram studyProgram : studyProgrammes)
				conn.data("dlObject", studyProgram.getProgramId());

			res = conn.execute();
			Document doc = res.parse();
			if (doc == null) {
				Console.warning("Unable to get timetable for week '" + (week) + "' from site [#2]!");
				return false;
			}

			HtmlResponse getResponse = HtmlUtils.sendGetRequest(EHBRooster.getBaseTimeTableURL(), cookies, 120000);
			Document timeTableDoc = Jsoup.parse(getResponse.getSource());
			List<Element> dayTables = timeTableDoc.getElementsByClass("spreadsheet");
			TimeTable timeTable = ScheduleManager.getInstance().getTimeTable();
			Week weekTimeTable = timeTable.getWeek(week);
			for (int subjectNr = 0; subjectNr < studyProgrammes.size(); subjectNr++) {
				StudyProgram studyProgram = studyProgrammes.get(subjectNr);
				for (int i = 1; i <= 7; i++) {
					Element dayTable = dayTables.get(i - 1 + (subjectNr * 7));
					Day dayTimeTable = weekTimeTable.getWeekDay(i);
					if (dayTable.getElementsByTag("tbody").size() != 0) {
						Element tbodyElement = dayTable.getElementsByTag("tbody").first();
						List<Element> rows = tbodyElement.getElementsByTag("tr");
						for (int row = 1; row < rows.size(); row++) {
							Element rowElement = rows.get(row);
							List<Element> columnElements = rowElement.getElementsByTag("td");
							if (columnElements.size() == 9) {
								String activity = columnElements.get(0).text();
								String lessonForm = columnElements.get(1).text();
								String begin = columnElements.get(2).text();
								String end = columnElements.get(3).text();
								String duration = columnElements.get(4).text();
								String weeks = columnElements.get(5).text();
								String lector = columnElements.get(6).text();
								String classRoom = columnElements.get(7).text();
								String groupsString = columnElements.get(8).text();
								Activity activityObj = new Activity(null, activity, lessonForm, begin, end, duration,
										weeks, lector, classRoom, groupsString, dayTimeTable);
								activityObj.addStudyProgram(studyProgram);
								if (groupsString.equals("") || groupsString.equals(" ") || groupsString.equals(" ")
										|| groupsString.equals(",")) {
									for (Group group : studyProgram.getGroups()) {
										Subject miscSubject = createSubject("Overige", "MISC_" + group.getName(),
												group);
										if (!group.getSubjects().contains(miscSubject)) {
											group.getSubjects().add(miscSubject);
										}
										activityObj.addSubject(miscSubject);
									}
								}
								dayTimeTable.addActivity(activityObj);
							}
						}
					}
				}
			}
			return true;
		} catch (Exception ex) {
			Console.warning("Unable to get timetable for week '" + (week) + "' from site [#3]!");
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Get group by name
	 * 
	 * @param name
	 *            Groupname
	 * @return Studie richting
	 */
	public Group getGroupByName(String name) {
		List<Group> groups = getGroups(false);
		for (Group group : groups) {
			if (group.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())
					|| group.getOriginalName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) {
				return group;
			}
		}
		return null;
	}

	/**
	 * Get groups by name
	 * 
	 * @param name
	 *            Groups by name
	 * @return List of groups
	 */
	public List<Group> getGroupsByName(String name) {
		List<Group> resultGroups = new ArrayList<Group>();
		List<Group> groups = getGroups(false);
		for (Group group : groups) {
			if (group.getName().toLowerCase().contains(name.toLowerCase())
					|| group.getOriginalName().toLowerCase().contains(name.toLowerCase())) {
				resultGroups.add(group);
			}
		}
		return resultGroups;
	}

	/**
	 * Get all subjects
	 * 
	 * @return List of subjects
	 */
	public List<Subject> getSubjects(boolean force) {
		if (subjects.isEmpty() || force) {
			List<Subject> currentSubjects = new ArrayList<Subject>(subjects);
			try {
				Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Method.GET)
						.execute();
				if (res == null) {
					Console.warning("Unable to get EHB subjects from site!");
					return subjects;
				}
				Document docCookieFetch = res.parse();
				if (docCookieFetch == null) {
					Console.warning("Unable to get EHB subjects from site!");
					return subjects;
				}

				// Required for cookie saving
				String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
				String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
				Map<String, String> cookies = res.cookies();
				Document doc = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000)
						.data("__EVENTTARGET", "LinkBtn_StudentSets").data("__EVENTARGUMENT", "")
						.data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
						.data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
						.data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
						.data("pDays", "1-7").data("dlPeriod", "1-56")
						.data("RadioType", "Individual;swsurl;SWS_EHB_IND").cookies(cookies).post();
				if (doc == null) {
					Console.warning("Unable to get EHB subjects from site!");
					return subjects;
				}
				// Jsoup doet lastig met grote lijsten....

				HtmlResponse getResponse = HtmlUtils.sendGetRequest(EHBRooster.getBaseURL(), cookies, 60000);
				doc = Jsoup.parse(getResponse.getSource());

				Element selectElement = doc.getElementById("dlObject");
				List<Element> optionElements = selectElement.children();
				int idx = 0;
				for (Element optionElement : optionElements) {
					Subject subject = new Subject(optionElement.text(), optionElement.attr("value"), null);
					subject.setListIndex(idx);
					boolean exists = false;
					for (Subject currentSubject : currentSubjects) {
						if (currentSubject.getSubjectId().equals(subject.getSubjectId())) {
							exists = true;
							if (currentSubject.getListIndex() != subject.getListIndex()) {
								Console.warning("Arrangement of subjects changed on official time schedule!");
								currentSubject.setListIndex(subject.getListIndex());
							}
							break;
						}
					}
					if (!exists)
						subjects.add(subject);

					idx++;
				}
			} catch (Exception ex) {
				Console.warning("Unable to get subjects from site [#3]!");
				ex.printStackTrace();
			}
		}
		return subjects;
	}

	/**
	 * Get Studie richtingen (groups)
	 * 
	 * @return List of studie richtingen
	 */
	public List<Group> getGroups(boolean force) {
		if (groups.isEmpty() || force) {
			List<Group> currentGroups = new ArrayList<Group>(groups);
			// Get all groups from remote site
			try {
				Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Method.GET)
						.execute();
				if (res == null) {
					Console.warning("Unable to get EHB groups from site!");
					return groups;
				}
				Document docCookieFetch = res.parse();
				if (docCookieFetch == null) {
					Console.warning("Unable to get EHB groups from site!");
					return groups;
				}

				// Required for cookie saving
				String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
				String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
				Map<String, String> cookies = res.cookies();
				res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000)
						.data("__EVENTTARGET", "LinkBtn_StudentSetGroups").data("__EVENTARGUMENT", "")
						.data("__LASTFOCUS", "").data("__VIEWSTATE", VIEWSTATE)
						.data("__EVENTVALIDATION", EVENTVALIDATION).data("tLinkType", "ProgrammesOfStudy")
						.data("dlFilter", "").data("tWildcard", "").data("lbWeeks", "t").data("lbDays", "1-7")
						.data("pDays", "1-7").data("dlPeriod", "1-56")
						.data("RadioType", "Individual%3Bswsurl%3BSWS_EHB_IND").cookies(cookies).method(Method.POST)
						.execute();
				Document doc = res.parse();
				if (doc == null) {
					Console.warning("Unable to get EHB groups from site!");
					return groups;
				}

				Element selectElement = doc.getElementById("dlObject");
				List<Element> optionElements = selectElement.getElementsByTag("option");
				for (Element optionElement : optionElements) {
					Group group = new Group(optionElement.text(), optionElement.attr("value"));
					boolean exists = false;
					for (Group currentGroup : currentGroups) {
						if (currentGroup.getGroupId().equals(group.getGroupId())) {
							exists = true;
							if (currentGroup.getListIndex() != group.getListIndex()) {
								Console.warning("Arrangement of groups changed on official time schedule!");
								currentGroup.setListIndex(group.getListIndex());
							}
							break;
						}
					}
					if (!exists)
						groups.add(group);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return groups;
	}

	public static String filterGroupName(String groupName) {
		groupName = groupName.replace("1BaDig", "1/BaDig");
		groupName = groupName.replace("2BaDig-X S2IT", "2/BaDig-X S2IT");
		groupName = groupName.replace("2BaDig-X-BIT", "2BaDig-X- BIT");
		groupName = groupName.replace("/KO/K", "/K");
		groupName = groupName.replace("/LO/L", "/L");
		groupName = groupName.replace("LSO/AA ", "");
		groupName = groupName.replace("LSO/BE ", "");
		groupName = groupName.replace("/PT", "/Ba PT");
		groupName = groupName.replace("IIM/", "IIM");
		return groupName;
	}

	public TimeTable getTimeTable() {
		return timeTable;
	}

	public void setTimeTable(TimeTable timeTable) {
		this.timeTable = timeTable;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public Department getDepartmentByCode(String code) {
		Department department = null;
		List<Department> departments = getDepartments();
		for (Department dp : departments) {
			if (dp.getCode().equals(code)) {
				department = dp;
				break;
			}
		}
		return department;
	}

	public List<Education> getEducations() {
		return educations;
	}

	public void setEducations(List<Education> educations) {
		this.educations = educations;
	}

	public List<StudyProgram> getProgrammes(boolean force) {
		if (programmes.isEmpty() || force) {
			List<StudyProgram> currentProgrammes = new ArrayList<StudyProgram>(programmes);
			try {
				Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Method.GET)
						.execute();
				if (res == null) {
					Console.warning("Unable to get EHB programmes from site!");
					return programmes;
				}

				Document doc = res.parse();
				if (doc == null) {
					Console.warning("Unable to get EHB programmes from site!");
					return programmes;
				}

				Element selectElement = doc.getElementById("dlObject");
				List<Element> optionElements = selectElement.children();
				int idx = 0;
				for (Element optionElement : optionElements) {
					StudyProgram program = new StudyProgram(optionElement.text(), optionElement.attr("value"));
					program.setListIndex(idx);
					boolean exists = false;
					for (StudyProgram currentProgram : currentProgrammes) {
						if (currentProgram.getProgramId().equals(program.getProgramId())) {
							exists = true;
							break;
						}
					}
					if (!exists)
						programmes.add(program);
					idx++;
				}
			} catch (Exception ex) {
				Console.warning("Unable to get programmes from site [#3]!");
				ex.printStackTrace();
			}
		}
		return programmes;
	}

	public void setProgrammes(List<StudyProgram> programmes) {
		this.programmes = programmes;
	}

	public List<Lector> getLectors(boolean force) {
		if (lectors.isEmpty() || force) {
			List<Lector> currentLectors = new ArrayList<Lector>(lectors);
			try {
				Connection.Response res = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000).method(Method.GET)
						.execute();
				if (res == null) {
					Console.warning("Unable to get EHB lectors from site!");
					return lectors;
				}
				Document docCookieFetch = res.parse();
				if (docCookieFetch == null) {
					Console.warning("Unable to get EHB lectors from site!");
					return lectors;
				}

				// Required for cookie saving
				String VIEWSTATE = docCookieFetch.getElementById("__VIEWSTATE").attr("value");
				String EVENTVALIDATION = docCookieFetch.getElementById("__EVENTVALIDATION").attr("value");
				Map<String, String> cookies = res.cookies();
				Document doc = Jsoup.connect(EHBRooster.getBaseURL()).timeout(60000)
						.data("__EVENTTARGET", "LinkBtn_Staff").data("__EVENTARGUMENT", "").data("__LASTFOCUS", "")
						.data("__VIEWSTATE", VIEWSTATE).data("__EVENTVALIDATION", EVENTVALIDATION)
						.data("tLinkType", "ProgrammesOfStudy").data("dlFilter", "").data("tWildcard", "")
						.data("lbWeeks", "t").data("lbDays", "1-7").data("pDays", "1-7").data("dlPeriod", "1-56")
						.data("RadioType", "Individual;swsurl;SWS_EHB_IND").cookies(cookies).post();
				if (doc == null) {
					Console.warning("Unable to get EHB lectors from site!");
					return lectors;
				}
				// Jsoup doet lastig met grote lijsten....

				HtmlResponse getResponse = HtmlUtils.sendGetRequest(EHBRooster.getBaseURL(), cookies, 60000);
				doc = Jsoup.parse(getResponse.getSource());

				Element selectElement = doc.getElementById("dlObject");
				List<Element> optionElements = selectElement.children();
				int idx = 0;
				for (Element optionElement : optionElements) {
					Lector lector = new Lector(optionElement.text(), optionElement.attr("value"));
					lector.setListIndex(idx);
					boolean exists = false;
					for (Lector currentLector : currentLectors) {
						if (currentLector.getLectorId().equals(lector.getLectorId())) {
							exists = true;
							if (currentLector.getListIndex() != lector.getListIndex()) {
								Console.warning("Arrangement of lectors changed on official time schedule!");
								currentLector.setListIndex(lector.getListIndex());
							}
							break;
						}
					}
					if (!exists)
						lectors.add(lector);

					idx++;
				}
			} catch (Exception ex) {
				Console.warning("Unable to get lectors from site [#3]!");
				ex.printStackTrace();
			}
		}
		return lectors;
	}

	public void setLectors(List<Lector> lectors) {
		this.lectors = lectors;
	}
}
