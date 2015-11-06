package be.maximvdw.ehbrooster;

import java.util.ArrayList;
import java.util.List;

import be.maximvdw.ehbrooster.config.Configuration;
import be.maximvdw.ehbrooster.schedule.Education;
import be.maximvdw.ehbrooster.schedule.Group;
import be.maximvdw.ehbrooster.schedule.Lector;
import be.maximvdw.ehbrooster.schedule.ScheduleManager;
import be.maximvdw.ehbrooster.schedule.StudyProgram;
import be.maximvdw.ehbrooster.schedule.Subject;
import be.maximvdw.ehbrooster.schedule.Sync;
import be.maximvdw.ehbrooster.ui.Console;

/**
 * EHB Rooster Converter
 * 
 * Converteer de HTML versie van het lessenrooster naar een MySQL database.
 * 
 * @version 18/09/2015
 * @author Maxim Van de Wynckel
 */
public class EHBRooster {
	private static String baseURL = "http://rooster.ehb.be/Scientia/sws/syl_prd_2015/default.aspx";
	private static String baseTimeTableURL = "http://rooster.ehb.be/Scientia/sws/syl_prd_2015/showtimetable.aspx";
	private ScheduleManager manager = null;

	public EHBRooster(String... args) {
		new Configuration(1); // Version 1
		String hostname = Configuration.getString("hostname");
		String database = Configuration.getString("database");
		String username = Configuration.getString("username");
		String password = Configuration.getString("password");
		int port = Configuration.getInt("port");

		Console.info("=====================================");
		Console.info(" EHBRooster Converter v1.7");
		Console.info(" (c) Maxim Van de Wynckel 2015-2015");
		Console.info("=====================================");

		setManager(new ScheduleManager(hostname, port, database, username, password));
		sync();
	}

	public void sync() {
		System.gc(); // Garbage collect
		long syncStartTime = System.currentTimeMillis();

		int currentWeekNumber = 1;
		long currentTime = System.currentTimeMillis() / 1000;
		for (int i = 1; i <= 52; i++) {
			long weekStart = getManager().getTimeTable().getStartTimeStamp() + ((i - 1) * 7 * 24 * 60 * 60);
			long weekEnd = weekStart + (7 * 24 * 60 * 60);
			if (weekStart < currentTime && weekEnd > currentTime) {
				currentWeekNumber = i;
				break;
			}
		}
		int startSyncWeek = currentWeekNumber;
		if (startSyncWeek >= 3) {
			startSyncWeek = startSyncWeek - 1;
		} else {
			startSyncWeek = 1;
		}
		int endSyncWeek = currentWeekNumber + 20;
		if (endSyncWeek > 52) {
			endSyncWeek = 52;
		}

		startSyncWeek = 1;
		Console.info("Creating departments");
		getManager().createDepartment("Design & Technologie", "DT");
		getManager().createDepartment("Onderwijs & Pedagogie", "EDU");
		getManager().createDepartment("Gezondheidszorg & Landschapsarchitectuur", "GL");
		getManager().createDepartment("Management, Media & Maatschappij", "MMM");
		getManager().createDepartment("Beeld, Geluid, Montage, Production Management & Podiumtechnieken", "RITCS");

		final List<Group> groups = new ArrayList<Group>();

		Console.info("Caching all groups ...");
		getManager().getGroups(true);

		Console.info("Caching all study programmes ...");
		getManager().getProgrammes(true);

		Console.info("Caching all lectors ...");
		final List<Lector> lectors = getManager().getLectors(true);

		Console.info("Loading groups ...");
		// Get 1st year Digix
		List<Group> digix1 = getManager().getGroupsByName("1BaDig-X");
		Education digix1EDU = getManager().createEducation("1 Ba Toegepast Informatica", "DT");
		for (Group g : digix1) {
			g.setEducation(digix1EDU);
		}
		groups.addAll(digix1);
		List<Group> digix1TI = getManager().getGroupsByName("1BaTI");
		for (Group g : digix1TI) {
			g.setEducation(digix1EDU);
		}
		groups.addAll(digix1TI);
		// Get 2nd year Digix
		List<Group> digix2 = getManager().getGroupsByName("2BaDig-X");
		Education digix2EDU = getManager().createEducation("2 Ba Toegepast Informatica", "DT");
		for (Group g : digix2) {
			g.setEducation(digix2EDU);
		}
		groups.addAll(digix2);
		// Get 3rd year Digix
		List<Group> digix3 = getManager().getGroupsByName("3BaDig-X");
		Education digix3EDU = getManager().createEducation("3 Ba Toegepast Informatica", "DT");
		for (Group g : digix3) {
			g.setEducation(digix3EDU);
		}
		groups.addAll(digix3);
		// Get 1st year Multec
		List<Group> multec1 = getManager().getGroupsByName("1BaMCT");
		Education multec1EDU = getManager().createEducation("1 Ba Multimedia en Communicatietechnologie", "DT");
		for (Group g : multec1) {
			g.setEducation(multec1EDU);
		}
		groups.addAll(multec1);
		// Get 2nd year Multec
		List<Group> multec2 = getManager().getGroupsByName("2BaMultec");
		Education multec2EDU = getManager().createEducation("2 Ba Multimedia en Communicatietechnologie", "DT");
		for (Group g : multec2) {
			g.setEducation(multec2EDU);
		}
		groups.addAll(multec2);
		// Get 3rd year Multec
		List<Group> multec3 = getManager().getGroupsByName("3BaMultec");
		Education multec3EDU = getManager().createEducation("3 Ba Multimedia en Communicatietechnologie", "DT");
		for (Group g : multec3) {
			g.setEducation(multec3EDU);
		}
		groups.addAll(multec3);
		// Get 1st year Drama - Regie
		List<Group> draReg1 = getManager().getGroupsByName("1/Ba Reg");
		Education draReg1EDU = getManager().createEducation("1 Ba in het Drama - Regie", "RITCS");
		for (Group g : draReg1) {
			g.setEducation(draReg1EDU);
		}
		groups.addAll(draReg1);
		// Get 2nd year Drama - Regie
		List<Group> draReg2 = getManager().getGroupsByName("2/Ba Reg");
		Education draReg2EDU = getManager().createEducation("2 Ba in het Drama - Regie", "RITCS");
		for (Group g : draReg2) {
			g.setEducation(draReg2EDU);
		}
		groups.addAll(draReg2);
		// Get 3rd year Drama - Regie
		List<Group> draReg3 = getManager().getGroupsByName("3/Ba Reg");
		Education draReg3EDU = getManager().createEducation("3 Ba in het Drama - Regie", "RITCS");
		for (Group g : draReg3) {
			g.setEducation(draReg3EDU);
		}
		groups.addAll(draReg3);
		// Get Master year Drama - Regie
		List<Group> draRegMA = getManager().getGroupsByName("Ma Reg");
		Education draRegMAEDU = getManager().createEducation("Master in het Drama - Regie", "RITCS");
		for (Group g : draRegMA) {
			g.setEducation(draRegMAEDU);
		}
		groups.addAll(draRegMA);
		// Get 1st year Drama - Acteren
		List<Group> draAct1 = getManager().getGroupsByName("1/Ba Act");
		Education draAct1EDU = getManager().createEducation("1 Ba in het Drama - Acteren", "RITCS");
		for (Group g : draAct1) {
			g.setEducation(draAct1EDU);
		}
		groups.addAll(draAct1);
		// Get 2nd year Drama - Acteren
		List<Group> draAct2 = getManager().getGroupsByName("2/Ba Act");
		Education draAct2EDU = getManager().createEducation("2 Ba in het Drama - Acteren", "RITCS");
		for (Group g : draAct2) {
			g.setEducation(draAct2EDU);
		}
		groups.addAll(draAct2);
		// Get 3rd year Drama - Acteren
		List<Group> draAct3 = getManager().getGroupsByName("3/Ba Act");
		Education draAct3EDU = getManager().createEducation("3 Ba in het Drama - Acteren", "RITCS");
		for (Group g : draAct3) {
			g.setEducation(draAct3EDU);
		}
		groups.addAll(draAct3);
		// Get Master year Drama - Acteren
		List<Group> draActMA = getManager().getGroupsByName("Ma Act");
		Education draActMAEDU = getManager().createEducation("Master in het Drama - Acteren", "RITCS");
		for (Group g : draActMA) {
			g.setEducation(draActMAEDU);
		}
		groups.addAll(draActMA);

		Education ptEDU = getManager().createEducation("Podiumtechnieken", "RITCS");
		// Get 1st year Podiumtechnieken
		List<Group> pt1 = getManager().getGroupsByName("1/Ba PT");
		for (Group g : pt1) {
			g.setEducation(ptEDU);
		}
		groups.addAll(pt1);
		// Get 2nd year Podiumtechnieken
		List<Group> pt2 = getManager().getGroupsByName("2/Ba PT");
		for (Group g : pt2) {
			g.setEducation(ptEDU);
		}
		groups.addAll(pt2);
		// Get 3rd year Podiumtechnieken
		List<Group> pt3 = getManager().getGroupsByName("3/Ba PT");
		for (Group g : pt3) {
			g.setEducation(ptEDU);
		}
		groups.addAll(pt3);
		// Get 1st year Communcatie
		Education ca1EDU = getManager().createEducation("1 Ba Communicatiemanagement", "MMM");
		List<Group> ca1 = getManager().getGroupsByName("1/OM");
		for (Group g : ca1) {
			g.setEducation(ca1EDU);
		}
		groups.addAll(ca1);
		// Get 2nd year Communcatie
		Education ca2EDU = getManager().createEducation("2 Ba Communicatiemanagement", "MMM");
		List<Group> ca2 = getManager().getGroupsByName("2/OM");
		for (Group g : ca2) {
			g.setEducation(ca2EDU);
		}
		groups.addAll(ca2);
		// Get 3rd year Communcatie
		Education ca3EDU = getManager().createEducation("3 Ba Communicatiemanagement", "MMM");
		List<Group> ca3 = getManager().getGroupsByName("3/OM");
		for (Group g : ca3) {
			g.setEducation(ca3EDU);
		}
		groups.addAll(ca3);
		
		// Get 1st year Communomtie
		Education om1EDU = getManager().createEducation("1 Ba Communiomtiemanagement", "MMM");
		List<Group> om1 = getManager().getGroupsByName("1/OM");
		for (Group g : om1) {
			g.setEducation(om1EDU);
		}
		groups.addAll(om1);
		// Get 2nd year Communomtie
		Education om2EDU = getManager().createEducation("2 Ba Office Management", "MMM");
		List<Group> om2 = getManager().getGroupsByName("2/OM");
		for (Group g : om2) {
			g.setEducation(om2EDU);
		}
		groups.addAll(om2);
		// Get 3rd year Communomtie
		Education om3EDU = getManager().createEducation("3 Ba Office Management", "MMM");
		List<Group> om3 = getManager().getGroupsByName("3/OM");
		for (Group g : om3) {
			g.setEducation(om3EDU);
		}
		groups.addAll(om3);
		
		// Get 1 AK
		Education ak1EDU = getManager().createEducation("1 Ba Audiovisuele Kunsten", "RITCS");
		Group akRadio1 = getManager().getGroupByName("1/Radio"); // OK
		akRadio1.setEducation(ak1EDU);
		akRadio1.setAlias("1 Ba AK - Radio");
		groups.add(akRadio1);
		Group akASS1 = getManager().getGroupByName("1/PM"); // OK
		akASS1.setName("1/Ba PM");
		akASS1.setEducation(ak1EDU);
		akASS1.setAlias("1 Ba AK - Assistentie");
		groups.add(akASS1);
		Group akB1 = getManager().getGroupByName("1/B");
		akB1.setName("1/Ba B");
		akB1.setEducation(ak1EDU);
		akB1.setAlias("1 Ba AK - Beeld");
		groups.add(akB1);
		Group akG1 = getManager().getGroupByName("1/G");
		akG1.setName("1/Ba G");
		akG1.setEducation(ak1EDU);
		akG1.setAlias("1 Ba AK - Geluid");
		groups.add(akG1);
		Group akM1 = getManager().getGroupByName("1/M");
		akM1.setName("1/Ba M");
		akM1.setEducation(ak1EDU);
		akM1.setAlias("1 Ba AK - Montage");
		groups.add(akM1);
		Group akD1 = getManager().getGroupByName("1/Ba D");
		akD1.setName("1/Ba D");
		akD1.setEducation(ak1EDU);
		akD1.setAlias("1 Ba AK - FILM, TV, DOC, SCHRIJVEN");
		groups.add(akD1);
		// Get 2 AK
		Education ak2EDU = getManager().createEducation("2 Ba Audiovisuele Kunsten", "RITCS");
		Group akRadio2 = getManager().getGroupByName("2/Radio"); // OK
		akRadio2.setEducation(ak2EDU);
		akRadio2.setAlias("2 Ba AK - Radio");
		groups.add(akRadio2);
		Group akASS2 = getManager().getGroupByName("2/PM"); // OK
		akASS2.setName("2/Ba PM");
		akASS2.setEducation(ak2EDU);
		akASS2.setAlias("2 Ba AK - Assistentie");
		groups.add(akASS2);
		Group akB2 = getManager().getGroupByName("2/B");
		akB2.setName("2/Ba B");
		akB2.setEducation(ak2EDU);
		akB2.setAlias("2 Ba AK - Beeld");
		groups.add(akB2);
		Group akG2 = getManager().getGroupByName("2/G");
		akG2.setName("2/Ba G");
		akG2.setEducation(ak2EDU);
		akG2.setAlias("2 Ba AK - Geluid");
		groups.add(akG2);
		Group akM2 = getManager().getGroupByName("2/M");
		akM2.setName("2/Ba M");
		akM2.setEducation(ak2EDU);
		akM2.setAlias("2 Ba AK - Montage");
		groups.add(akM2);
		Group akD2 = getManager().getGroupByName("2/Ba D");
		akD2.setName("2/Ba D");
		akD2.setEducation(ak2EDU);
		akD2.setAlias("2 Ba AK - FILM, TV, DOC, SCHRIJVEN");
		groups.add(akD2);
		// Get 3 AK
		Education ak3EDU = getManager().createEducation("3 Ba Audiovisuele Kunsten", "RITCS");
		Group akRadio3 = getManager().getGroupByName("3/Radio"); // OK
		akRadio3.setEducation(ak3EDU);
		akRadio3.setAlias("3 Ba AK - Radio");
		groups.add(akRadio3);
		Group akASS3 = getManager().getGroupByName("3/PM"); // OK
		akASS3.setName("3/Ba PM");
		akASS3.setEducation(ak3EDU);
		akASS3.setAlias("3 Ba AK - Assistentie");
		groups.add(akASS3);
		Group akB3 = getManager().getGroupByName("3/B");
		akB3.setName("3/Ba B");
		akB3.setEducation(ak3EDU);
		akB3.setAlias("3 Ba AK - Beeld");
		groups.add(akB3);
		Group akG3 = getManager().getGroupByName("3/G");
		akG3.setName("3/Ba G");
		akG3.setEducation(ak3EDU);
		akG3.setAlias("3 Ba AK - Geluid");
		groups.add(akG3);
		Group akM3 = getManager().getGroupByName("3/M");
		akM3.setName("3/Ba M");
		akM3.setEducation(ak3EDU);
		akM3.setAlias("3 Ba AK - Montage");
		groups.add(akM3);
		Group akD3 = getManager().getGroupByName("3/Ba D");
		akD3.setName("3/Ba D");
		akD3.setEducation(ak3EDU);
		akD3.setAlias("3 Ba AK - Documentaire");
		groups.add(akD3);
		Group akTV3 = getManager().getGroupByName("3/Ba T-TA");
		akTV3.setName("3/Ba T-TA");
		akTV3.setEducation(ak3EDU);
		akTV3.setAlias("3 Ba AK - TV");
		groups.add(akTV3);
		Group akS3 = getManager().getGroupByName("3/Ba S");
		akS3.setName("3/Ba S");
		akS3.setEducation(ak3EDU);
		akS3.setAlias("3 Ba AK - Schrijven");
		groups.add(akS3);
		Group akF3 = getManager().getGroupByName("3/Ba F-TA");
		akF3.setName("3/Ba F-TA");
		akF3.setEducation(ak3EDU);
		akF3.setAlias("3 Ba AK - Film");
		groups.add(akF3);
		Education akMAEDU = getManager().createEducation("Ma Audiovisuele Kunsten", "RITCS");
		// Get Animatiefilm
		Group akAN1 = getManager().getGroupByName("1/Ba AN-R-P"); // OK
		akAN1.setName("1/Ba AN-R-P");
		akAN1.setEducation(ak1EDU);
		akAN1.setAlias("1 Ba AK - Animatiefilm");
		groups.add(akAN1);
		Group akAN2 = getManager().getGroupByName("2/Ba AN-R-P"); // OK
		akAN2.setName("2/Ba AN-R-P");
		akAN2.setEducation(ak2EDU);
		akAN2.setAlias("2 Ba AK - Animatiefilm");
		groups.add(akAN2);
		Group akAN3 = getManager().getGroupByName("3/Ba AN-R-P"); // OK
		akAN3.setName("3/Ba AN-R-P");
		akAN3.setEducation(ak3EDU);
		akAN3.setAlias("3 Ba AK - Animatiefilm");
		groups.add(akAN3);
		Group akANMA = getManager().getGroupByName("Ma AN"); // OK
		akANMA.setEducation(akMAEDU);
		akANMA.setAlias("Ma AK - Animatiefilm");
		groups.add(akANMA);
		// Get 1st year Sociaal Werk
		List<Group> draSW1 = getManager().getGroupsByName("1/SW-MW");
		Education draSW1EDU = getManager().createEducation("1 Ba Sociaal Werk", "MMM");
		for (Group g : draSW1) {
			g.setEducation(draSW1EDU);
		}
		groups.addAll(draSW1);
		// Get 2nd year Sociaal Werk
		List<Group> draSWMW2 = getManager().getGroupsByName("2/SW-MW");
		Education draSWMW2EDU = getManager().createEducation("2 Ba Sociaal Werk - MW", "MMM");
		for (Group g : draSWMW2) {
			g.setEducation(draSWMW2EDU);
		}
		groups.addAll(draSWMW2);

		List<Group> draSWSCW2 = getManager().getGroupsByName("2/SW-SCW");
		Education draSWSCW2EDU = getManager().createEducation("2 Ba Sociaal Werk - SCW", "MMM");
		for (Group g : draSWSCW2) {
			g.setEducation(draSWSCW2EDU);
		}
		groups.addAll(draSWSCW2);

		List<Group> draSWSJD2 = getManager().getGroupsByName("2/SW-SJD");
		Education draSWSJD2EDU = getManager().createEducation("2 Ba Sociaal Werk - MA", "MMM");
		for (Group g : draSWSJD2) {
			g.setEducation(draSWSJD2EDU);
		}
		groups.addAll(draSWSJD2);
		// Get 3rd year Sociaal Werk
		List<Group> draSWMW3 = getManager().getGroupsByName("3/SW-MW");
		Education draSWMW3EDU = getManager().createEducation("3 Ba Sociaal Werk - MW", "MMM");
		for (Group g : draSWMW3) {
			g.setEducation(draSWMW3EDU);
		}
		groups.addAll(draSWMW3);

		List<Group> draSWSCW3 = getManager().getGroupsByName("3/SW-SCW");
		Education draSWSCW3EDU = getManager().createEducation("3 Ba Sociaal Werk - SCW", "MMM");
		for (Group g : draSWSCW3) {
			g.setEducation(draSWSCW3EDU);
		}
		groups.addAll(draSWSCW3);

		List<Group> draSWSJD3 = getManager().getGroupsByName("3/SW-SJD");
		Education draSWSJD3EDU = getManager().createEducation("3 Ba Sociaal Werk - MA", "MMM");
		for (Group g : draSWSJD3) {
			g.setEducation(draSWSJD3EDU);
		}
		groups.addAll(draSWSJD3);
		// Get 1st year IIM
		List<Group> iim1 = getManager().getGroupsByName("1/IIM");
		Education iim1EDU = getManager().createEducation("1 Ba Idea & Innovation Management", "MMM");
		for (Group g : iim1) {
			g.setEducation(iim1EDU);
		}
		groups.addAll(iim1);
		// Get 2nd year IIM
		List<Group> iim2 = getManager().getGroupsByName("2/IIM");
		Education iim2EDU = getManager().createEducation("2 Ba Idea & Innovation Management", "MMM");
		for (Group g : iim2) {
			g.setEducation(iim2EDU);
		}
		groups.addAll(iim2);
		// Get 1st year JT
		List<Group> jt1 = getManager().getGroupsByName("1/JT");
		Education jt1EDU = getManager().createEducation("1 Ba Journalistiek", "MMM");
		for (Group g : jt1) {
			g.setEducation(jt1EDU);
		}
		groups.addAll(jt1);
		// Get 2nd year JT
		List<Group> jt2 = getManager().getGroupsByName("2/JT");
		Education jt2EDU = getManager().createEducation("2 Ba Journalistiek", "MMM");
		for (Group g : jt2) {
			g.setEducation(jt2EDU);
		}
		groups.addAll(jt2);
		// Get 3st year JT
		List<Group> jt3 = getManager().getGroupsByName("3/JT");
		Education jt3EDU = getManager().createEducation("3 Ba Journalistiek", "MMM");
		for (Group g : jt3) {
			g.setEducation(jt3EDU);
		}
		groups.addAll(jt3);
		// Get 1st year HM
		List<Group> hm1 = getManager().getGroupsByName("1/HM");
		Education hm1EDU = getManager().createEducation("1 Ba in het Hotelmanagement", "MMM");
		for (Group g : hm1) {
			g.setEducation(hm1EDU);
		}
		groups.addAll(hm1);
		// Get 2st year HM
		List<Group> hm2 = getManager().getGroupsByName("2/HM");
		Education hm2EDU = getManager().createEducation("2 Ba in het Hotelmanagement", "MMM");
		for (Group g : hm2) {
			g.setEducation(hm2EDU);
		}
		groups.addAll(hm2);
		// Get 3st year HM
		List<Group> hm3 = getManager().getGroupsByName("3/HM");
		Education hm3EDU = getManager().createEducation("3 Ba in het Hotelmanagement", "MMM");
		for (Group g : hm3) {
			g.setEducation(hm3EDU);
		}
		groups.addAll(hm3);
		// Get 4st year HM
		List<Group> hm4 = getManager().getGroupsByName("4/HM");
		Education hm4EDU = getManager().createEducation("4 Ba in het Hotelmanagement", "MMM");
		for (Group g : hm4) {
			g.setEducation(hm4EDU);
		}
		groups.addAll(hm4);
		// Get 1st year in de Verpleegkunde
		List<Group> vp1 = getManager().getGroupsByName("1/VP");
		Education vp1EDU = getManager().createEducation("1 Ba in de Verpleegkunde", "GL");
		for (Group g : vp1) {
			g.setEducation(vp1EDU);
		}
		groups.addAll(vp1);
		// Get 2st year in de Verpleegkunde
		List<Group> vp2 = getManager().getGroupsByName("2/VP");
		Education vp2EDU = getManager().createEducation("2 Ba in de Verpleegkunde", "GL");
		for (Group g : vp2) {
			g.setEducation(vp2EDU);
		}
		groups.addAll(vp2);
		// Get 3st year in de Verpleegkunde
		List<Group> vp3 = getManager().getGroupsByName("3/VP");
		Education vp3EDU = getManager().createEducation("3 Ba in de Verpleegkunde", "GL");
		for (Group g : vp3) {
			g.setEducation(vp3EDU);
		}
		groups.addAll(vp3);

		Console.info("Preparing to get timetables for " + groups.size() + " groups ...");
		Console.info("Creating cache of all subjects ...");
		getManager().getSubjects(true);
		// Save all groups to database
		for (Group group : groups) {
			Console.info("Loading subjects for group: " + group.getName());
			List<Subject> subjects = group.getSubjects(false);
			Console.info("Saving subjects for group: " + group.getName());
			for (Subject subject : subjects) {
				Console.info("Saving subject: " + subject.getName());
			}
			Console.info("Saving group: " + group.getName());
			getManager().saveGroup(group);
		}

		final List<StudyProgram> studyProgrammes = new ArrayList<StudyProgram>();
		StudyProgram podium1 = getManager().getStudyProgramByName("1 Ba Podiumtechnieken");
		podium1.setGroups(pt1);
		getManager().saveStudyProgram(podium1);
		studyProgrammes.add(podium1);

		StudyProgram podium2 = getManager().getStudyProgramByName("2 Ba Podiumtechnieken");
		podium2.setGroups(pt2);
		getManager().saveStudyProgram(podium2);
		studyProgrammes.add(podium2);

		StudyProgram podium3 = getManager().getStudyProgramByName("3 Ba Podiumtechnieken");
		podium3.setGroups(pt3);
		getManager().saveStudyProgram(podium3);
		studyProgrammes.add(podium3);

		StudyProgram draAct1Program = getManager().getStudyProgramByName("1 Ba in het Drama - Acteren");
		draAct1Program.setGroups(draAct1);
		getManager().saveStudyProgram(draAct1Program);
		studyProgrammes.add(draAct1Program);

		StudyProgram draAct2Program = getManager().getStudyProgramByName("2 Ba in het Drama - Acteren");
		draAct2Program.setGroups(draAct2);
		getManager().saveStudyProgram(draAct2Program);
		studyProgrammes.add(draAct2Program);

		StudyProgram draAct3Program = getManager().getStudyProgramByName("3 Ba in het Drama - Acteren");
		draAct3Program.setGroups(draAct3);
		getManager().saveStudyProgram(draAct3Program);
		studyProgrammes.add(draAct3Program);

		StudyProgram draActMaProgram = getManager().getStudyProgramByName("Ma in het Drama - Acteren");
		draActMaProgram.setGroups(draActMA);
		getManager().saveStudyProgram(draActMaProgram);
		studyProgrammes.add(draActMaProgram);

		StudyProgram draReg1Program = getManager().getStudyProgramByName("1 Ba in het Drama - Regie");
		draReg1Program.setGroups(draReg1);
		getManager().saveStudyProgram(draReg1Program);
		studyProgrammes.add(draReg1Program);

		StudyProgram draReg2Program = getManager().getStudyProgramByName("2 Ba in het Drama - Regie");
		draReg2Program.setGroups(draReg2);
		getManager().saveStudyProgram(draReg2Program);
		studyProgrammes.add(draReg2Program);

		StudyProgram draReg3Program = getManager().getStudyProgramByName("3 Ba in het Drama - Regie");
		draReg3Program.setGroups(draReg3);
		getManager().saveStudyProgram(draReg3Program);
		studyProgrammes.add(draReg3Program);

		StudyProgram draRegMaProgram = getManager().getStudyProgramByName("Ma in het Drama - Regie");
		draRegMaProgram.setGroups(draRegMA);
		getManager().saveStudyProgram(draRegMaProgram);
		studyProgrammes.add(draRegMaProgram);

		StudyProgram akRadio1Program = getManager().getStudyProgramByName("1 Ba AK - Radio");
		akRadio1Program.addGroup(akRadio1);
		getManager().saveStudyProgram(akRadio1Program);
		studyProgrammes.add(akRadio1Program);

		StudyProgram akASS1Program = getManager().getStudyProgramByName("1 Ba in de Audiovisuele Kunsten - ASS");
		akASS1Program.addGroup(akASS1);
		getManager().saveStudyProgram(akASS1Program);
		studyProgrammes.add(akASS1Program);

		StudyProgram akD1Program = getManager().getStudyProgramByName("1 Ba AK - Film - TV - DOC - SCHRIJVEN");
		akD1Program.addGroup(akD1);
		getManager().saveStudyProgram(akD1Program);
		studyProgrammes.add(akD1Program);

		StudyProgram akAN1Program = getManager().getStudyProgramByName("1 Ba AK - Animatiefilm");
		akAN1Program.addGroup(akAN1);
		getManager().saveStudyProgram(akAN1Program);
		studyProgrammes.add(akAN1Program);

		StudyProgram akASS2Program = getManager().getStudyProgramByName("2 Ba in de Audiovisuele Kunsten - ASS");
		akASS2Program.addGroup(akASS2);
		getManager().saveStudyProgram(akASS2Program);
		studyProgrammes.add(akASS2Program);

		StudyProgram akRadio2Program = getManager().getStudyProgramByName("2 Ba AK - Radio");
		akRadio2Program.addGroup(akRadio2);
		getManager().saveStudyProgram(akRadio2Program);
		studyProgrammes.add(akRadio2Program);

		StudyProgram akAN2Program = getManager().getStudyProgramByName("2 Ba AK - Animatiefilm");
		akAN2Program.addGroup(akAN2);
		getManager().saveStudyProgram(akAN2Program);
		studyProgrammes.add(akAN2Program);

		StudyProgram akB2Program = getManager().getStudyProgramByName("2 Ba in de Audiovisuele Kunsten - Beeld");
		akB2Program.addGroup(akB2);
		getManager().saveStudyProgram(akB2Program);
		studyProgrammes.add(akB2Program);

		StudyProgram akG2Program = getManager().getStudyProgramByName("2 Ba in de Audiovisuele Kunsten - Geluid");
		akG2Program.addGroup(akG2);
		getManager().saveStudyProgram(akG2Program);
		studyProgrammes.add(akG2Program);

		StudyProgram akM2Program = getManager().getStudyProgramByName("2 Ba in de Audiovisuele Kunsten - Montage");
		akM2Program.addGroup(akM2);
		getManager().saveStudyProgram(akM2Program);
		studyProgrammes.add(akM2Program);

		StudyProgram akD2Program = getManager().getStudyProgramByName("2 Ba AK- Film - TV - DOC - Schrijven");
		akD2Program.addGroup(akD2);
		getManager().saveStudyProgram(akD2Program);
		studyProgrammes.add(akD2Program);

		StudyProgram akRadio3Program = getManager().getStudyProgramByName("3 Ba AK - Radio");
		akRadio3Program.addGroup(akRadio3);
		getManager().saveStudyProgram(akRadio3Program);
		studyProgrammes.add(akRadio3Program);

		StudyProgram akAN3Program = getManager().getStudyProgramByName("3 Ba AK - Animatiefilm");
		akAN3Program.addGroup(akAN3);
		getManager().saveStudyProgram(akAN3Program);
		studyProgrammes.add(akAN3Program);

		StudyProgram akASS3Program = getManager().getStudyProgramByName("3 Ba in de Audiovisuele Kunsten - ASS");
		akASS3Program.addGroup(akASS3);
		getManager().saveStudyProgram(akASS3Program);
		studyProgrammes.add(akASS3Program);

		StudyProgram akB3Program = getManager().getStudyProgramByName("3 Ba in de Audiovisuele Kunsten - Beeld");
		akB3Program.addGroup(akB3);
		getManager().saveStudyProgram(akB3Program);
		studyProgrammes.add(akB3Program);

		StudyProgram akG3Program = getManager().getStudyProgramByName("3 Ba in de Audiovisuele Kunsten - Geluid");
		akG3Program.addGroup(akG3);
		getManager().saveStudyProgram(akG3Program);
		studyProgrammes.add(akG3Program);

		StudyProgram akM3Program = getManager().getStudyProgramByName("3 Ba in de Audiovisuele Kunsten - Montage");
		akM3Program.addGroup(akM3);
		getManager().saveStudyProgram(akM3Program);
		studyProgrammes.add(akM3Program);

		StudyProgram akF3Program = getManager().getStudyProgramByName("3 Ba AK - Film");
		akF3Program.addGroup(akF3);
		getManager().saveStudyProgram(akF3Program);
		studyProgrammes.add(akF3Program);

		StudyProgram akS3Program = getManager().getStudyProgramByName("3 Ba AK - Schrijven");
		akS3Program.addGroup(akS3);
		getManager().saveStudyProgram(akS3Program);
		studyProgrammes.add(akS3Program);

		StudyProgram akTV3Program = getManager().getStudyProgramByName("3 Ba AK - TV");
		akTV3Program.addGroup(akTV3);
		getManager().saveStudyProgram(akTV3Program);
		studyProgrammes.add(akTV3Program);

		StudyProgram akD3Program = getManager().getStudyProgramByName("3 Ba AK - Documentaire");
		akD3Program.addGroup(akD3);
		getManager().saveStudyProgram(akD3Program);
		studyProgrammes.add(akD3Program);

		StudyProgram akANMAProgram = getManager().getStudyProgramByName("Ma AK - Animatiefilm");
		akANMAProgram.addGroup(akANMA);
		getManager().saveStudyProgram(akANMAProgram);
		studyProgrammes.add(akANMAProgram);

		List<Thread> threadPool = new ArrayList<Thread>();
		for (int week = startSyncWeek; week <= endSyncWeek; week++) {
			final int currentWeek = week;
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					List<Group> fetchGroups = new ArrayList<Group>(groups);
					while (!fetchGroups.isEmpty()) {
						List<Subject> subjects = new ArrayList<Subject>();
						List<Group> addedGroups = new ArrayList<Group>();
						for (Group group : fetchGroups) {
							if ((subjects.size() + group.getSubjects().size()) > 1000) {

							} else {
								for (Subject sub : group.getSubjects()) {
									if (!subjects.contains(sub))
										if (!sub.getSubjectId().startsWith("MISC_"))
											subjects.add(sub);
								}
								addedGroups.add(group);
							}
						}
						for (Group group : addedGroups) {
							fetchGroups.remove(group);
						}

						boolean success = false;
						while (!success) {
							Console.info("Getting time table for week: " + currentWeek);
							Console.info("Remaining groups after fetch for week " + currentWeek + " : "
									+ fetchGroups.size());
							success = getManager().fetchTimeTable(currentWeek, subjects);
							if (!success) {
								Console.warning("Retrying week: " + currentWeek);
							}
						}
						System.gc();
					}
				}

			});
			t.start();
			threadPool.add(t);
			if (threadPool.size() >= 2) {
				while (threadPool.size() >= 1) {
					List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
					for (Thread thread : shadedThreadPool) {
						if (!thread.isAlive()) {
							threadPool.remove(thread);
						}
					}
				}
			}
			System.gc();
		}

		while (threadPool.size() != 0) {
			List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
			for (Thread thread : shadedThreadPool) {
				if (!thread.isAlive()) {
					threadPool.remove(thread);
				}
			}
		}

		threadPool = new ArrayList<Thread>();
		for (int week = startSyncWeek; week <= endSyncWeek; week++) {
			final int currentWeek = week;
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					boolean success = false;
					while (!success) {
						Console.info("Getting study program time table for week: " + currentWeek);
						success = getManager().fetchStudyProgramTimeTable(currentWeek, studyProgrammes);
						if (!success) {
							Console.warning("Retrying week: " + currentWeek);
						}
					}
				}

			});
			t.start();
			threadPool.add(t);
			if (threadPool.size() >= 2) {
				while (threadPool.size() >= 1) {
					List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
					for (Thread thread : shadedThreadPool) {
						if (!thread.isAlive()) {
							threadPool.remove(thread);
						}
					}
				}
			}
			System.gc();
		}

		while (threadPool.size() != 0) {
			List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
			for (Thread thread : shadedThreadPool) {
				if (!thread.isAlive()) {
					threadPool.remove(thread);
				}
			}
		}
		// Save all groups to database
		getManager().saveGroups(groups);

		threadPool = new ArrayList<Thread>();
		for (int week = startSyncWeek; week <= endSyncWeek; week++) {
			final int currentWeek = week;
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					boolean success = false;
					while (!success) {
						Console.info("Getting lectors time table for week: " + currentWeek);
						success = getManager().fetchLectorTimeTable(currentWeek, lectors);
						if (!success) {
							Console.warning("Retrying week: " + currentWeek);
						}
					}
				}

			});
			t.start();
			threadPool.add(t);
			if (threadPool.size() >= 2) {
				while (threadPool.size() >= 1) {
					List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
					for (Thread thread : shadedThreadPool) {
						if (!thread.isAlive()) {
							threadPool.remove(thread);
						}
					}
				}
			}
			System.gc();
		}

		while (threadPool.size() != 0) {
			List<Thread> shadedThreadPool = new ArrayList<Thread>(threadPool);
			for (Thread thread : shadedThreadPool) {
				if (!thread.isAlive()) {
					threadPool.remove(thread);
				}
			}
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long syncEndTime = System.currentTimeMillis();
		Sync sync = new Sync(syncEndTime, 0, 0, syncEndTime - syncStartTime);

		// Check for changes
		Console.info("Synchronizing time table ...");
		getManager().saveTimeTable(sync);
		Console.info("Saving completed! Waiting for next sync ...");
		Console.info("Last sync time: " + ((syncEndTime - syncStartTime) / 1000 / 60) + " min");
		try {
			// Computers hebben ook rust nodig ...
			Thread.sleep(1 * 60 * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getManager().loadDatabase();
		sync();
	}

	/**
	 * Initial call
	 * 
	 * @param args
	 *            Arguments
	 */
	public static void main(String... args) {
		new EHBRooster(args);
	}

	/**
	 * Get the base URL
	 * 
	 * @return Base URL
	 */
	public static String getBaseURL() {
		return baseURL;
	}

	public static String getBaseTimeTableURL() {
		return baseTimeTableURL;
	}

	public static void setBaseTimeTableURL(String baseTimeTableURL) {
		EHBRooster.baseTimeTableURL = baseTimeTableURL;
	}

	public ScheduleManager getManager() {
		return manager;
	}

	public void setManager(ScheduleManager manager) {
		this.manager = manager;
	}
}
