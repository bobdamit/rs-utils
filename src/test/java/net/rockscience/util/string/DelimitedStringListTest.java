package net.rockscience.util.string;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DelimitedStringListTest {
	
	private DelimitedStringList underTestPipe;
	private DelimitedStringList underTestComma;
	
	@BeforeEach
	public void setUp() {
		underTestPipe = new DelimitedStringList();
		underTestComma = new DelimitedStringList(",");
	}

	@Test
	public void test() {
		String s = "A|B|C|D|E|F";
		
		List<String> sl = underTestPipe.toList(s);
		assertTrue(sl.size() == 6);
		
		String s2 = underTestPipe.toDelimitedString(sl);
		
		assertEquals(s,s2);
	}

	@Test
	public void testComma() {
		String s = "A,B,C,D,E,F";
		
		List<String> sl = underTestComma.toList(s);
		assertTrue(sl.size() == 6);
		
		String s2 = underTestComma.toDelimitedString(sl);
		
		assertEquals(s,s2);
	}
	
	@Test
	public void testNull() {
		String s = null;
		List<String> sl = underTestComma.toList(s);
		assertTrue(sl.size() == 0);
		
		String s2 = underTestComma.toDelimitedString(sl);
		
		assertEquals(s,s2);
	}
	
	@Test
	public void testOneElement() {
		String s = "One";
		List<String> sl = underTestComma.toList(s);
		assertTrue(sl.size() == 1);
		
		String s2 = underTestComma.toDelimitedString(sl);
		
		assertEquals(s,s2);
	}
	
	@Test
	public void testBlankElements() {
		String s = "   A| |B || C|||  ";	// and allow trailing delims
		
		List<String> sl = underTestPipe.toList(s);
		assertTrue(sl.size() == 3);
		
		sl.add("   ");
		s = underTestPipe.toDelimitedString(sl);
		
		assertEquals("A|B|C", s);
	}
	
}
