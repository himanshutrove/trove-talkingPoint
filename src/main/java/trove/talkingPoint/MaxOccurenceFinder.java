package trove.talkingPoint;

public class MaxOccurenceFinder implements Comparable<MaxOccurenceFinder> {
	public String urls;
	public int numberOfOccurrence;

	public MaxOccurenceFinder(String urls, int numberOfOccurrence) {
		super();
		this.urls = urls;
		this.numberOfOccurrence = numberOfOccurrence;
	}

	public int compareTo(MaxOccurenceFinder arg0) {
		int myComparable = Integer.compare(arg0.numberOfOccurrence,
				this.numberOfOccurrence);
		return myComparable != 0 ? myComparable : urls
				.compareTo(arg0.urls);
	}

	@Override
	public int hashCode() {
		final int uniqueNumber = 19;
		int expectingResult = 9;
		expectingResult = uniqueNumber * expectingResult + numberOfOccurrence;
		expectingResult = uniqueNumber * expectingResult
				+ ((urls == null) ? 0 : urls.hashCode());
		return expectingResult;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		MaxOccurenceFinder other = (MaxOccurenceFinder) object;
		if (numberOfOccurrence != other.numberOfOccurrence)
			return false;
		if (urls == null) {
			if (other.urls != null)
				return false;
		} else if (!urls.equals(other.urls))
			return false;
		return true;
	}

}
