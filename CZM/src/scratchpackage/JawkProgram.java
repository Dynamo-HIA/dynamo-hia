package scratchpackage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Instanzen der Klasse JawkProgram "J-AWK-Programme", AWK-artige 
 *  Programme zur Verarbeitung von Text-Streams in Java.
 *
 * @author schultma
 */
public abstract class JawkProgram {
//    protected String fieldSeparator = ";";
    protected String fieldSeparator = "\\s";
      protected Pattern splitPattern;

    private List /*of Rule*/ rules = new ArrayList();

    private BufferedReader input;
    private BufferedWriter output;
   
    private class Line {
        private String content;
        private String[] fields;

        public Line ( String content ) {
            this.content = content;
        }

        public String getContent(){
            return content;
        }

        public String field( int i ) {
            if (fields==null)
                fields = splitPattern.split( content );
            return fields[i];
        }

        public void process() {
            for ( Iterator ruleIterator = rules.iterator(); ruleIterator.hasNext(); ) {
                Rule curRule = (Rule) ruleIterator.next();
                curRule.applyTo( this );
            }
        }
    }

    protected abstract class Command {
        private Line line;

        protected void print( String s ) {
            try {
                output.write( s );
            } catch ( IOException e ) {
                throw new RuntimeException(e);
            }
        }

        protected String f(int i) {
            return line.field(i);
        }
        
        protected String line() {
          return line.getContent();
        }

        public final void executeOn( Line l ) {
            line = l;
            execute();
        }
        abstract protected void execute();
    }

    protected class Rule {
        private Matcher matchPattern;
        private Command command;

        public Rule( String pattern, Command command ) {
            this.matchPattern = Pattern.compile( pattern ).matcher("");
            this.command = command;
        }

        public void applyTo( Line line ) {
            matchPattern.reset( line.getContent() );
            if ( matchPattern.find() ) {
                if ( command==null ) {
                    command.print( line.getContent() );
                } else {
                    command.executeOn( line );
                }
            }
        }
    }

    protected void addRule( Rule rule ) {
        rules.add( rule );
    }

    protected void addRules( Rule[] r ) {
        rules.addAll( Arrays.asList(r) );
    }

    protected abstract void registerRules(); 

    public JawkProgram () {
        registerRules();
    }

    public void process( InputStream in, OutputStream out ) {
        input = new BufferedReader( new InputStreamReader(in) );
        output = new BufferedWriter( new OutputStreamWriter(out) );

        splitPattern = Pattern.compile( fieldSeparator );

        try {

            String currentLine;
            while ( (currentLine = input.readLine()) != null ) {
               Line l = new Line(currentLine);
               l.process();
            }            
            output.flush();

        } catch ( IOException e ) {
           throw new RuntimeException(e);
        }      
    }


}