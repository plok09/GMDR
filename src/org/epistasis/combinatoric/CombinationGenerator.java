package org.epistasis.combinatoric;

import java.util.Iterator;

public class CombinationGenerator implements Iterator {
	private int nVars;
	private boolean hasNext;
	private int[] combo;

	public CombinationGenerator() {
		nVars = 0;
		hasNext = true;
		combo = new int[0];
	}

	public CombinationGenerator(int nVars, int nCombo) {
		set(nVars, nCombo);
	}

	public void set(int nVars, int nCombo) {
		if (nCombo < 0) {
			throw new IllegalArgumentException("nCombo must be non-negative");
		}

		if (nVars < nCombo) {
			throw new IllegalArgumentException("nCombo cannot be larger than nVars");
		}

		this.nVars = nVars;
		combo = new int[nCombo];

		reset();
	}

	public void reset() {
		hasNext = true;
		for (int i = 0; i < combo.length; ++i) {
			combo[i] = i;
		}
	}

	public boolean hasNext() {
		return hasNext;
	}

	public Object next() {
		if (!hasNext()) {
			return null;
		}

		Object ret = combo.clone();
		hasNext = increment(0);

		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	private boolean increment(int index) {
		if (combo.length == 0) {
			return false;
		}

		if (++combo[combo.length - 1 - index] < nVars - index) {
			for (int i = combo.length - index; i < combo.length; ++i) {
				combo[i] = combo[i - 1] + 1;
			}

			return true;
		}

		if (index + 1 < combo.length) {
			return increment(index + 1);
		} else {
			return false;
		}
	}
}
