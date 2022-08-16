import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import javax.swing.*;



public class Calcualtor extends JFrame implements ActionListener  {
	private JPanel p = new JPanel();
	private TextField calcTextField = new TextField(10);
	static private boolean infix = true;
	static private char state ='0';
	static private double nk=0;
	static private double ans=0;
	static private char opr =' ';
	static private Stack<Double> stack = new Stack<Double>();
	private Button[] buttons = new Button[16];
	static private char[] buttonText={'9','8','7','*','4','5','6','/','1','2','3','-','.','0','=','+'};
	private Button butt = new Button("Clear");
	private JButton RPN = new JButton("RPN");
	public Calcualtor() {
		this.setBounds(300,300, 100, 220);
		this.setSize(100,220);
		this.setBackground(Color.white);
	
		calcTextField.setText("0");
		calcTextField.setEditable(false);

		p.setLayout(new FlowLayout());
		p.add(calcTextField); 
		for (int i=0; i<16; i++) {
		    buttons[i] = new Button (""+buttonText[i]);
		    buttons[i].addActionListener(this);
		    p.add(buttons[i]);
		} 
	 	RPN.addActionListener(this);
	 	p.add(RPN);

	 	butt.addActionListener(this);
	 	p.add(butt);
	 	
		this.add(p);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		Calcualtor x = new Calcualtor();	
	}
	public void setButtonsName(int i,String ch) { this.buttons[i].setLabel(ch);}
	public void setButtName(String ch) {this.butt.setLabel(ch);}
	@Override
	public void actionPerformed(ActionEvent e) {
		String key = e.getActionCommand();
		if(key.equals("RPN")) {
			infix=false;
			JButton src = (JButton) e.getSource();
			src.setText("infix");
			this.setButtonsName(14, "c");
			this.setButtName("Enter");
			clear();
			
		} 
		if(key.equals("infix")) {
			infix =true;
			JButton src = (JButton) e.getSource();
			src.setText("RPN");
			this.setButtonsName(14, "=");
			this.setButtName("Clear");
			clear();
		} 
		
		
		//logic for infix calculator
		if(infix) {
			//state j display the enter number & set nk = key
			if(Character.isDigit(key.charAt(0)) && (state=='i' || state=='0' || state=='j' || state=='d')) {
				//from state 'j'---still entering num, get the sum of the enter num and previous num*10
				if(state=='j') nk = nk*10+Character.getNumericValue(key.charAt(0));
				//from state 'd'--- entering the decimal number
				else if (state=='d') nk = Double.parseDouble(calcTextField.getText().concat(""+key.charAt(0)));
				else  nk = Character.getNumericValue(key.charAt(0));
				//display related value on screen
				//#1 display the entered key
				if(state=='0' || state=='i') this.calcTextField.setText(""+key.charAt(0));
				//#2 display decimal
				else this.calcTextField.setText(calcTextField.getText().concat(""+key.charAt(0)));
				state='j';
			} 
			
			//state i do calculation if have operand, otherwise display the entered number
			if(isOpr(key.charAt(0))  && (state=='j'|| state=='0')) {
				if(opr==' ') ans = nk;
				else ans = apply(ans,opr,nk);
				opr = key.charAt(0);
				this.calcTextField.setText(String.valueOf(ans));
				state ='i';
			}
			//state:equal----- back to state i or j
			if(key.charAt(0)=='='){
				if(state=='j'){
					nk = apply(ans,opr,nk);
					this.calcTextField.setText(String.valueOf(nk));
					opr = ' ';
					state='j';
				} //state ='i' do nothing
			}
			//State:d---- decimal 
			if(key.charAt(0)=='.' && (state=='j' || state=='0')){
				this.calcTextField.setText(calcTextField.getText().concat("."));
				state ='d';
			}
			if(key.equals("Clear")) clear();
		}
		
//---------------------------------------------------------------------------------------
		
		
		//RPN calculator
		else {
			//input key is digit (State 'i')
			if(Character.isDigit(key.charAt(0)) && (state=='0' || state=='i' || state=='d' || state=='j'||state=='k')){
				//handle nk, get nk ready for the stack
				if(state=='i') nk = nk*10+Character.getNumericValue(key.charAt(0));
				else if(state=='0' || state=='j' || state=='k') nk = Character.getNumericValue(key.charAt(0));
				else if (state=='d') nk = Double.parseDouble(calcTextField.getText().concat(""+key.charAt(0)));
				System.out.println("nk= "+nk);
				//handle display
				if(state=='i'||state=='d') this.calcTextField.setText(calcTextField.getText().concat(""+key.charAt(0)));
				else this.calcTextField.setText(""+key.charAt(0));
				state='i';
			}
			//input key is enter (State 'j')
			if(key.charAt(0)=='E' && (state=='i' || state=='0')) {
				if(state=='i') stack.push(nk);
				else if(state=='0')stack.push((double) 0);
				System.out.println("Enter pressed");
				System.out.println("Current Stack: "+ stack.toString());
				nk=0;
				state='j';
			}
			//input key is operand (State 'k')
			if(isOpr(key.charAt(0)) && (state=='i'  || state =='j' || state=='k')){
				//empty stack do nothing
				if(stack.size()!=0) {
					if(state=='i') {
						opr = key.charAt(0);
						ans = apply(stack.pop(),opr,nk);
						stack.push(ans);
						System.out.println("Current Stack: "+ stack.toString());
						this.calcTextField.setText(String.valueOf(ans));
						state='k';
					}else if((state =='j' || state=='k') && stack.size()>=2) {
						opr = key.charAt(0);
						nk = stack.pop();
						ans = apply(stack.pop(),opr,nk);
						stack.push(ans);
						System.out.println("Current Stack: "+ stack.toString());
						this.calcTextField.setText(String.valueOf(ans));
						state='k';
					}
				}
			}
			
			//input key is decimal (State 'd')
			if(key.charAt(0)=='.' && (state=='i' || state=='0')){
				this.calcTextField.setText(calcTextField.getText().concat("."));
				state ='d';
			}
			//input key is clear sign
			if(key.charAt(0)=='c'){
				clear();
			}
		}
		
		
		
	}
	
	public double apply(double a, char b, double c) {
	      if(b == '+') return a+c;
	      if(b == '-') return a-c;
	      if(b == '*') return a*c;
	      if(b == '/') return a/c;  
	      else return(c);
	}
	public boolean isOpr(char x) {
		if(x=='+' || x=='-' || x=='*' || x=='/') return true;
		else return false;
	}
	public void clear() {
		this.calcTextField.setText("0");
		this.nk=0;
		this.ans=0;
		this.opr=' '; 
		this.state='0';
		this.stack.clear();
	}
}
