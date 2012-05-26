package utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class VectorDoubleWritable implements Writable, Cloneable {
	protected ArrayList<Double> vec = new ArrayList<Double>();

	public VectorDoubleWritable() {
		vec.clear();
	}

	@SuppressWarnings("unchecked")
	public VectorDoubleWritable(ArrayList<Double> array) {
		vec = (ArrayList<Double>) array.clone();
	}

	public VectorDoubleWritable(Text value) {
		vec.clear();
		StringTokenizer itr = new StringTokenizer(value.toString());
		while (itr.hasMoreTokens()) {
			vec.add(Double.parseDouble(itr.nextToken()));
		}
	}

	public ArrayList<Double> get() {
		return vec;
	}

	public int size() {
		return vec.size();
	}

	public double sum() {
		double sum = 0;
		for (Double data : vec) {
			sum += data;
		}
		return sum;
	}

	public VectorDoubleWritable plus(VectorDoubleWritable t2) {
		ListIterator<Double> ite1 = vec.listIterator();
		ListIterator<Double> ite2 = t2.get().listIterator();
		VectorDoubleWritable result = new VectorDoubleWritable();
		while (ite1.hasNext() && ite2.hasNext()) {
			result.append(ite1.next() + ite2.next());
		}
		return result;
	}

	public VectorDoubleWritable times(VectorDoubleWritable t2) {
		ListIterator<Double> ite1 = vec.listIterator();
		ListIterator<Double> ite2 = t2.get().listIterator();
		VectorDoubleWritable result = new VectorDoubleWritable();
		while (ite1.hasNext() && ite2.hasNext()) {
			result.append(ite1.next() * ite2.next());
		}
		return result;
	}

	public void append(Double data) {
		vec.add(data);
	}

	public void remove(Double data) {
		vec.remove(data);
	}

	public double euclidianDistance(VectorDoubleWritable d)
			throws IllegalStateException {
		if (d.get().size() != vec.size())
			throw new IllegalStateException("Dimension doesn't agree!");
		Iterator<Double> ite1 = vec.iterator();
		Iterator<Double> ite2 = d.get().iterator();
		double dist = 0;
		double p1, p2;
		for (; ite1.hasNext() && ite2.hasNext();) {
			p1 = ite1.next();
			p2 = ite2.next();
			dist += (p1 - p2) * (p1 - p2);
		}
		dist = Math.sqrt(dist);
		return dist;
	}

	@Override
	public int hashCode() {
		return vec.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Double data : vec) {
			sb.append(data + "\t");
		}
		return sb.toString().trim();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VectorDoubleWritable)) {
			return false;
		}
		VectorDoubleWritable other = (VectorDoubleWritable) o;
		if (vec.size() != other.get().size())
			return false;
		Iterator<Double> ite1 = vec.iterator();
		Iterator<Double> ite2 = other.get().iterator();
		for (; ite1.hasNext() && ite2.hasNext();) {
			if (ite1.next() != ite2.next())
				return false;
		}
		return true;
	}

	@Override
	public Object clone() {
		VectorDoubleWritable clone = new VectorDoubleWritable(this.vec);
		return clone;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		try {
			int size = in.readInt();
			for (int i = 0; i < size; i++) {
				vec.add(in.readDouble());
			}
		} catch (EOFException e) {

		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(vec.size());
		for (Double data : vec) {
			out.writeDouble(data);
		}
	}

}
