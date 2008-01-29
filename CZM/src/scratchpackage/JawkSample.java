package scratchpackage;
import java.io.FileInputStream;
import java.io.IOException;

public class JawkSample extends JawkProgram {

	protected void registerRules() {
		addRules(new Rule[] {

				new Rule("/a-z/", null)
				
			
				//		new Rule("\n", new Command() {
//			protected void execute() {
//				print("\nName: " + f(3));
//			}
//		}),
//
//		new Rule("^Q\\d", new Command() {
//			protected void execute() {
//				print("\nName: " + f(3));
//			}
//		}),
//
//		new Rule("^P", new Command() {
//			protected void execute() {
//				print("\n ignoring line " + line());
//			}
//		}),

		});

	}

	public static void main(String[] args) throws IOException {
		String dataDirectoryName = "C:/eclipse321/workspace/CZM/data";
		FileInputStream in = new FileInputStream(dataDirectoryName
				+ "/demeninput010305.txt");
		JawkSample prog = new JawkSample();

		prog.process(in, System.out);

	}
}
