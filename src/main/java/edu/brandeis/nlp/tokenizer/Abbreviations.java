package edu.brandeis.nlp.tokenizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Abbreviations {

	static final String[] MONTHS = {
		"Jan.", "Feb.", "Mar.", "Apr.", "Jun.", "Jul.", "Aug.", "Sep.",
		"Sept.", "Oct.", "Nov.", "Dec." };

	static final String[] TITLES = {
		"Dr.", "Gen.", "Rep.", "JR.", "Jr.", "MD.", "Miss.", "Mr.", "Mrs.", "Ms.",
		"Prof.", "Sr.", "dr.", "rep.", "jr.", "miss.", "mr.", "mrs.", "ms.", 
		"prof.", "sr." };

	static final String[] STATES = {
		"ALA.", "Ala.", "Ariz.", "CALIF.", "Cal.", "Calif.", "Colo.", "Conn.", 
		"Dak.", "Del.", "FLA.", "Fla.", "Ga.", "ILL.", "IND.", "Ill.", "Ind.",
		"Kan.", "Kans.", "Ky.", "MICH.", "MISS.", "Mass.", "Mich.", "Minn.",
		"Miss.", "Mo.", "Mont.", "Nev.", "Okla.", "Ore.", "Penna.", "TEX.",
		"Tenn.", "Tex.", "Va.", "Wash.", "Wis." };

	static final String[] GEO = {
		"Av.", "Ave.", "Bldg.", "Blvd.", "Rd.", "St.", "av.", "ave.", "pl.", 
		"rd.", "sq.", "st." };

	static final String[] MEASURES = {
	    "10-yr.", "LB.", "cent.", "cm.", "ft.", "hr.", "lb.", "lb./cu.", "lbs.",
		"mil.", "min.", "mm.", "m.p.h.", "oz.", "sec.", "seq.", "yr." };

	static final String[] OTHER = {
		"Assn.", "Bros.", "Cir.", "Co.", "Corp.", "Ct.", "D-Ore.", "Dist.", 
		"ED.", "Eng.", "Inc.", "Kas.", "LA.", "La.", "Ltd.", "MD.", "MO.", 
		"Md.", "O.-B.", "O.-C.", "P.-T.A.", "Pa.", "Prop.", "R-N.J.", "SP.", 
		"SS.", "Tech.", "Ter.", "USN.", "Yok.", "a.m.", "al.", "dept.", "e.g.",
		"etc.", "gm.", "i.d.", "i.e.", "inc.", "kc.", "mos.", "p.m.",
		"post-A.D.", "pro-U.N.F.P." };

	static final String[] OTHER_END = {
	    "A.D.", "A.M.", "Ass.", "B.C.", "Bldg.", "Blvd.", "Co.", "Corp.", "D.C.",
		"Dist.", "Eng.", "Esq.", "I.Q.", "I.R.S.", "Inc.", "Jr.", "La.", "Md.", 
		"N.C.", "N.J.","N.Y.", "O.E.C.D.", "P.M.", "Pa.", "R.P.M.", "SS.", "Sr.",
		"St.", "Tech.", "U.N.", "U.S.", "U.S.A.", "U.S.S.R.", "a.m.", "al.", 
		"av.", "ave.", "cm.", "dr.", "esq.", "etc.", "gm.", "hr.", "jr.", "kc.",
		"lbs.", "mos.", "p.m.", "dr.", "D-Ore."};

	static Set<String> mon = new HashSet<>(Arrays.asList(MONTHS));
	public static Set<String> ttl = new HashSet<>(Arrays.asList(TITLES));
	public static Set<String> sta = new HashSet<>(Arrays.asList(STATES));
	public static Set<String> geo = new HashSet<>(Arrays.asList(GEO));
	public static Set<String> mea = new HashSet<>(Arrays.asList(MEASURES));
	public static Set<String> oth = new HashSet<>(Arrays.asList(OTHER));
	public static Set<String> end = new HashSet<>(Arrays.asList(OTHER_END));

	public static final Set<String> ABBREVS = new HashSet<>();
	public static final Set<String> END_ABBREVS = new HashSet<>();

	static {
		ABBREVS.addAll(Arrays.asList(MONTHS));
		ABBREVS.addAll(Arrays.asList(TITLES));
		ABBREVS.addAll(Arrays.asList(STATES));
		ABBREVS.addAll(Arrays.asList(GEO));
		ABBREVS.addAll(Arrays.asList(MEASURES));
		ABBREVS.addAll(Arrays.asList(OTHER));
		END_ABBREVS.addAll(Arrays.asList(MONTHS));
		END_ABBREVS.addAll(Arrays.asList(TITLES));
		END_ABBREVS.addAll(Arrays.asList(STATES));
		END_ABBREVS.addAll(Arrays.asList(GEO));
		END_ABBREVS.addAll(Arrays.asList(MEASURES));
		END_ABBREVS.addAll(Arrays.asList(OTHER_END)); }

}
