package potrace;

import java.util.Iterator;
import java.util.Vector;

public class Polygon implements Iterable<Path> {

	Vector<Path> mPaths = new Vector<Path>();

	double penalty = 10000d;

	public Polygon() {
	}

	public boolean addAll(Vector<Path> path) {
		return mPaths.addAll(path);
	}

	public boolean add(Path path) {
		return mPaths.add(path);
	}

	public int size() {
		return mPaths.size();
	}

	public Path get(int index) {
		return mPaths.get(index);
	}

	@Override
	public Iterator<Path> iterator() {
		return mPaths.iterator();
	}

}
