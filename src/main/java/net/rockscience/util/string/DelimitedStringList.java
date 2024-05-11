package net.rockscience.util.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * A helper for compressing string lists to delimited strings and to 
 * expand delimited Strings into a string list
 * @author bdamiano
 *
 */
public class DelimitedStringList {
	
	private static final Map<String,String> delimsThatNeedEscape;
	
	static {
		delimsThatNeedEscape = new HashMap<>();
		delimsThatNeedEscape.put("|", "\\|");
		// more...?
	}
	
	public static List<String> fromCommaSeperated(String content) {
		return new DelimitedStringList(",").toList(content);
	}
	
	private final String delimiter;
	
	public DelimitedStringList(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public DelimitedStringList() {
		this("|");
	}

	public List<String> toList(String delimitedString) {
		delimitedString = StringUtils.trimToNull(delimitedString);
		
		if(delimitedString == null) {
			return Collections.emptyList();
		}
		
		String delimRx = delimsThatNeedEscape.get(delimiter);
		if(delimRx == null) {
			delimRx = delimiter;
		}
		
		String [] as = delimitedString.split(delimRx);
		
		List<String> result = new ArrayList<>();
		for(String a : as) {
			a = StringUtils.trimToNull(a);
			if(a != null) {
				result.add(a);
			}
		}
		return result;
	}
	
	
	public String toDelimitedString( Iterable<String> stringList) {
		StringBuilder sb = new StringBuilder();
		for(String s : stringList) {
			s = StringUtils.trimToNull(s);
			if(s != null) {
				if(sb.length() > 0 ) {
					sb.append(delimiter);
				}
				sb.append(s);
			}
		}
		return sb.length() == 0 ? null : sb.toString();
	}
	
}
