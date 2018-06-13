package org.epistasis;

import java.awt.geom.Point2D;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinearRegressionCalculator extends AbstractList {
	private List points = new ArrayList();
	private double sumx = 0;
	private double sumy = 0;
	private Double meanx;
	private Double meany;
	private Double ssxx;
	private Double ssxy;
	private Double ssyy;
	private Double r2;
	private Double b;
	private Double a;

	public double getMeanX() {
		computeMeans();
		return meanx.doubleValue();
	}

	public double getMeanY() {
		computeMeans();
		return meany.doubleValue();
	}

	public double getSSxx() {
		computeSquareSums();
		return ssxx.doubleValue();
	}

	public double getSSxy() {
		computeSquareSums();
		return ssxy.doubleValue();
	}

	public double getSSyy() {
		computeSquareSums();
		return ssyy.doubleValue();
	}

	public double getB() {
		if (b == null) {
			b = new Double(getSSxy() / getSSxx());
		}

		return b.doubleValue();
	}

	public double getA() {
		if (a == null) {
			a = new Double(getMeanY() - getB() * getMeanX());
		}

		return a.doubleValue();
	}

	public double getR2() {
		if (r2 == null) {
			r2 = new Double(getSSxy() * getSSxy() / (getSSxx() * getSSyy()));

			if (r2.isNaN()) {
				r2 = new Double(0);
			}
		}

		return r2.doubleValue();
	}

	public Object set(int index, Object element) {
		Point2D oldPoint = (Point2D) get(index);
		Point2D newPoint = (Point2D) element;

		meanx = meany = ssxx = ssxy = ssyy = r2 = a = b = null;

		sumx += newPoint.getX() - oldPoint.getX();
		sumy += newPoint.getY() - oldPoint.getY();
		return points.set(index, newPoint);
	}

	public void add(int index, Object element) {
		Point2D point = (Point2D) element;

		meanx = meany = ssxx = ssxy = ssyy = r2 = a = b = null;

		sumx += point.getX();
		sumy += point.getY();

		points.add(index, point);
	}

	public void add(double x, double y) {
		add(new Point2D.Double(x, y));
	}

	public Object remove(int index) {
		Point2D point = (Point2D) remove(index);

		if (point != null) {
			sumx -= point.getX();
			sumy -= point.getY();

			meanx = meany = ssxx = ssxy = ssyy = r2 = a = b = null;
		}

		return point;
	}

	public Object get(int index) {
		return points.get(index);
	}

	public int size() {
		return points.size();
	}

	private void computeMeans() {
		if (meanx != null) {
			return;
		}

		meanx = new Double(sumx / size());
		meany = new Double(sumy / size());
	}

	private void computeSquareSums() {
		if (ssxx != null) {
			return;
		}

		double ssxx = 0;
		double ssxy = 0;
		double ssyy = 0;

		for (Iterator i = iterator(); i.hasNext();) {
			Point2D point = (Point2D) i.next();

			double diffx = point.getX() - getMeanX();
			double diffy = point.getY() - getMeanY();

			ssxx += diffx * diffx;
			ssxy += diffx * diffy;
			ssyy += diffy * diffy;
		}

		this.ssxx = new Double(ssxx);
		this.ssxy = new Double(ssxy);
		this.ssyy = new Double(ssyy);
	}
}
