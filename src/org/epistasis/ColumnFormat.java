package org.epistasis;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ColumnFormat {
	private List widths;

	public ColumnFormat(List widths) {
		this.widths = new LinkedList(widths);
	}

	public String format(List values) {
		StringBuffer b = new StringBuffer();

		for (Iterator i = values.iterator(), j = widths.iterator(); i.hasNext() && j.hasNext();) {
			b.append(fitStringWidth(i.next().toString(), ((Number) j.next()).intValue()));
		}

		return b.toString();
	}

	public static String fitStringWidth(String string, int width) {
		if (string.length() > width) {
			string = string.substring(0, width);
		} else if (string.length() < width) {
			char[] fill = new char[width - string.length()];
			Arrays.fill(fill, ' ');
			string += new String(fill);
		}

		return string;
	}
}
