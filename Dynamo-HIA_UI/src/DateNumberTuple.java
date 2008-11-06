
	public class DateNumberTuple implements Comparable<DateNumberTuple> {
		long date = 0;
		int publicationNumber = 0;

		public DateNumberTuple(long date, int publicationNumber) {
			this.date = date;
			this.publicationNumber = publicationNumber;
		}

		public int compareTo(DateNumberTuple arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public long getDate() {
			return date;
		}

		public int getPublicationNumber() {
			return publicationNumber;
		}
	}
