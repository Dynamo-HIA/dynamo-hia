package scratchpackage;

import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JTextField;

public class Klaas extends javax.swing.JFrame {

	private JInternalFrame jInternalFrame = null;
	private JPanel jContentPane = null;
	private JButton jButton = null;
	private JTextField jTextField = null;

	/**
	 * This method initializes 
	 * 
	 */
	public Klaas() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(674, 356));
        this.setContentPane(getJInternalFrame());
			
	}

	/**
	 * This method initializes jInternalFrame	
	 * 	
	 * @return javax.swing.JInternalFrame	
	 */
	private JInternalFrame getJInternalFrame() {
		if (jInternalFrame == null) {
			jInternalFrame = new JInternalFrame();
			jInternalFrame.setContentPane(getJContentPane());
		}
		return jInternalFrame;
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJButton(), BorderLayout.CENTER);
			jContentPane.add(getJTextField(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Knoppie");
		}
		return jButton;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
		}
		return jTextField;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
