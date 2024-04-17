/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

/**
 * @author Michael Brundage
 * @author constructor with seed: Hendriek
 * also changed from final to non-final
 */
public class MTRand {
    private int mt_index=624; // initialized on 624 so the algorithm will run immediately another time;
    // this could be changed, but then this is not really an MT algorithm for the first 624 random numbers;
    private int[] mt_buffer = new int[624];

   
    // Initialize the generator from a seed ;
    
    public MTRand(long seed) {
    	 java.util.Random r = new java.util.Random(seed);
         for (int i = 0; i < 624; i++)
             mt_buffer[i] = r.nextInt();
         mt_index = 0; }
    

    
    public MTRand() {
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < 624; i++)
            mt_buffer[i] = r.nextInt();
        mt_index = 0;
    }

    public int random() {
        if (mt_index == 624)
        {
            mt_index = 0;
            int i = 0;
            int s;
            for (; i < 624 - 397; i++) {
                s = (mt_buffer[i] & 0x80000000) | (mt_buffer[i+1] & 0x7FFFFFFF);
                mt_buffer[i] = mt_buffer[i + 397] ^ (s >> 1) ^ ((s & 1) * 0x9908B0DF);
            }
            for (; i < 623; i++) {
                s = (mt_buffer[i] & 0x80000000) | (mt_buffer[i+1] & 0x7FFFFFFF);
                mt_buffer[i] = mt_buffer[i - (624 - 397)] ^ (s >> 1) ^ ((s & 1) * 0x9908B0DF);
            }
        
            s = (mt_buffer[623] & 0x80000000) | (mt_buffer[0] & 0x7FFFFFFF);
            mt_buffer[623] = mt_buffer[396] ^ (s >> 1) ^ ((s & 1) * 0x9908B0DF);
        }
        return mt_buffer[mt_index++];
    }
}

