import java.util.Arrays;

public class Play {

	public static void main(String[] args) {
		DateNumberTuple[] tuples = new DateNumberTuple[3];
		DateNumberTuple dnt1 = new DateNumberTuple(2L, 1);
		tuples[0] = dnt1;
		DateNumberTuple dnt2 = new DateNumberTuple(2L, 2);
		tuples[1] = dnt2;
		DateNumberTuple dnt3 = new DateNumberTuple(1L, 1);
		tuples[2] = dnt3;
		for(int count = 0; count<tuples.length; count++){
			System.out.println("Voor: datum " + tuples[count].getDate() + " number " + tuples[count].getPublicationNumber());
		}
		Arrays.sort(tuples, new DateNumberTupleComparator());
		for(int count = 0; count<tuples.length; count++){
			System.out.println("Na: datum " + tuples[count].getDate() + " number " + tuples[count].getPublicationNumber());
		}
	}

}
