import java.util.Comparator;

	class DateNumberTupleComparator implements
			Comparator<DateNumberTuple> {

		public int compare(DateNumberTuple o1, DateNumberTuple o2) {
			int result = 0;
			long date1 = o1.getDate();
			int pubNumber1 = o1.getPublicationNumber();
			long date2 = o2.getDate();
			int pubNumber2 = o2.getPublicationNumber();
			if(date1 != date2){
				if(date1 > date2){
					result = 1;
					} else {
						result = -1;
					}
			} else {
				if(pubNumber1 > pubNumber2){
					result = 1;
					} else {
						result = -1;
					}
			}
			return result;
		}
	}
