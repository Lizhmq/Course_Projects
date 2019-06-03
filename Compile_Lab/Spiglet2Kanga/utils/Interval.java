package utils;

public class Interval implements Comparable<Interval> {
	public int begin, end;
	public boolean save = false;
	public int tempNo;

	public Interval(int tempNo, int begin, int end) {
		this.begin = begin;
		this.end = end;
		this.tempNo = tempNo;
	}

	public int compareTo(Interval b) {
		if (begin == b.begin)
			return end - b.end;
		return begin - b.begin;
	}
}