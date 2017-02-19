package com.haxademic.sketch.test;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;

public class CsvTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String _csvFile = FileUtil.getHaxademicDataPath() + "csv/match-game.csv";
	protected ArrayList<Score> _scores;
	protected String[] _csvHeaders;
	protected int _curId = 0;
	
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}

	public void setup() {
		super.setup();

		_scores = new ArrayList<Score>();
		try {
			readWithCsvListReader();
		} catch (Exception e) { e.printStackTrace(); }
		
		addScore( "FOO", "jonny.cat@dog.com", 33333, 9 );
		addScore( "FO1", "jonny.cat@dog.com", 33333, 9 );
		addScore( "FO2", "jonny.cat@dog.com", 33333, 9 );
		addScore( "FO3", "jonny.cat@dog.com", 33333, 9 );
	}

	public void drawApp() {
	}

	public void addScore( String initials, String email, int completeTime, int regionId ) {
		_scores.add( new Score( Integer.toString( _curId ), initials, email, completeTime, regionId ) );
		_curId++;
		try {
			writeWithCsvListWriter();
		} catch (Exception e) { e.printStackTrace(); }
	}

	/**
	 * Sets up the CSV processors for loading and saving data
	 * 
	 * @return the cell processors
	 */
	private static CellProcessor[] getProcessors() {

		final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+"; // just an example, not very robust!
		StrRegEx.registerMessage(emailRegex, "must be a valid email address");

		final CellProcessor[] processors = new CellProcessor[] { 
				new UniqueHashCode(), 	// score id (must be unique)
				new NotNull(), 			// initials
				new NotNull(), 			// emails
				new ParseInt(), 		// completeTime
				new ParseInt() 			// regionId
		};

		return processors;
	}

	/**
	 * An example of reading using CsvListReader.
	 */
	private void readWithCsvListReader() throws Exception {	
		ICsvListReader listReader = null;
		try {
			listReader = new CsvListReader(new FileReader( _csvFile ), CsvPreference.STANDARD_PREFERENCE);

			_csvHeaders = listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
			P.println( _csvHeaders );
			
			final CellProcessor[] processors = getProcessors();
			List<Object> scores;
			while( (scores = listReader.read(processors)) != null ) {
				int scoreId = Integer.parseInt( (String) scores.get(0));
				if( scoreId > _curId ) _curId = scoreId;
				
//				System.out.println( String.format("lineNo=%s, rowNo=%s, scores=%s", listReader.getLineNumber(), listReader.getRowNumber(), scores));
				Score score = new Score(
					(String) scores.get(0),
					(String) scores.get(1),
					(String) scores.get(2),
					(Integer) scores.get(3),
					(Integer) scores.get(4)
				);
				_scores.add( score );
			} 
			_curId++;
			P.println( "_curId = "+_curId );
		}
		finally {
			if( listReader != null ) {
				listReader.close();
			}
		}
	}
	
	/**
	 * An example of reading using CsvListWriter.
	 */
	private void writeWithCsvListWriter() throws Exception {

		ICsvListWriter listWriter = null;
		try {
			listWriter = new CsvListWriter(new FileWriter( _csvFile ), CsvPreference.STANDARD_PREFERENCE);

			final CellProcessor[] processors = getProcessors();
//			final String[] header = new String[] { "customerNo", "firstName", "lastName", "birthDate", "mailingAddress", "married", "numberOfKids", "favouriteQuote", "email", "loyaltyPoints" };

			// write the header
			listWriter.writeHeader( _csvHeaders );

			// write the customer lists
			for( int i=0; i < _scores.size(); i++ )
				listWriter.write(_scores.get(i).getCsvArray(), processors);
		}
		finally {
			if( listWriter != null ) {
				listWriter.close();
			}
		}
	}


	public class Score {
		
		public String id;
		public String initials;
		public String email;
		public int completeTime;
		public int regionId;
		
		public Score( String id, String initials, String email, int completeTime, int regionId ) {
			this.id = id;
			this.initials = initials;
			this.email = email;
			this.completeTime = completeTime;
			this.regionId = regionId;
		}
		
		public List<Object> getCsvArray() {
			return Arrays.asList(new Object[] { id, initials, email, completeTime, regionId });
		}
		
		public String toString() {
			return "----------\nID: " + id + "\nInitials: " + initials + "\nEmail: " + email + "\nComplete Time: " + completeTime + "\nRegion ID: " + regionId;
		}
	}
	


}