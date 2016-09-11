import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MyBeatBox {
	
	Sequencer seq1;
	Sequence seq;
	Track track;
	JFrame frame;
	JPanel mainPanel;
	
	int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
	
	String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
			"Open Hi-Hat","Acoustic Snare", "Crash Cymbal", "Hand Clap",
			"High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
			"Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
			"Open Hi Conga"};
	
	ArrayList<JCheckBox> checkBoxList;
	
	public void buildGui(){
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();

		JPanel backGround = new JPanel(layout);
		backGround.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		Box buttonsBox = new Box(BoxLayout.Y_AXIS);
		
		JButton start = new JButton("start");
		start.addActionListener(new startListener());
		buttonsBox.add(start);
		
		JButton stop = new JButton("stop");
		//stop.addActionListener(new stopActioListener());
		buttonsBox.add(stop);
		
		JButton save = new JButton("save");
		save.addActionListener(new saveActionListener());
		buttonsBox.add(save);
		
		JButton load = new JButton("load");
		load.addActionListener(new loadActionListener());
		buttonsBox.add(load);
		
		Box namesBox = new Box(BoxLayout.Y_AXIS);
		
		for (int n = 0; n<instrumentNames.length; n++){
			
			namesBox.add(new Label(instrumentNames[n]));
			
		}
		
		backGround.add(BorderLayout.WEST,namesBox);
		backGround.add(BorderLayout.EAST,buttonsBox);
		
		checkBoxList = new ArrayList<JCheckBox>();
		
		
		GridLayout grid = new GridLayout(16,16);
		grid.setVgap(1);
		grid.setHgap(2);
		mainPanel = new JPanel(grid);
		
		for(int x = 0; x<256; x++){
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkBoxList.add(c);
			mainPanel.add(c);
			
		}
		
		backGround.add(mainPanel);
		
		
		
		
		frame.setBounds(50, 50, 500, 400);
		frame.getContentPane().add(backGround);
		frame.setVisible(true);
		
		
		
		
	}
	
	public void setMidi(){
		
		try{
			seq1 = MidiSystem.getSequencer();
			seq1.open();
			seq = new Sequence(Sequence.PPQ,4);
			track = seq.createTrack();
			
			
			
			seq1.setSequence(seq);
			seq1.start();
		}
		catch(Exception e){}
		
		
	}
	
	
	public void makeTrackArrays(){
		int trackList[] = null;
		
		seq.deleteTrack(track);
		track = seq.createTrack();
		
		setMidi();
		
		for (int i = 0; i < 16 ; i++){
			
			trackList = new int[16];
			int key = instruments[i];
			
			for (int j = 0 ; j<16 ; j++){
				
				JCheckBox jc = (JCheckBox) checkBoxList.get(j + (i*16));
				if (jc.isSelected()){
					trackList[j] = key;
				}
				else{
					trackList[j] = 0;
				}
				

			}

			makeTracks(trackList);
			track.add(makeEvent(176,1,127,0,16));
		}
		track.add(makeEvent(192,9,1,0,15));
		
		try{
			seq1.setSequence(seq);
			seq1.start();
		}
		catch (Exception e){}


	}
	
	
	public  void makeTracks(int[] trackArray){
		
		for (int x = 0 ; x < 16 ; x++){
			
			int key = trackArray[x];
			
			if (trackArray[x] !=0){
				track.add(makeEvent(144,9,key,100,x));
				track.add(makeEvent(128,9,key,100,x+1));
			}
			
		}
		
		
	}
	
	public MidiEvent makeEvent(int cmd, int chan, int one, int two, int tick ){
		MidiEvent event = null;
		try{
		ShortMessage m = new ShortMessage(cmd, chan, one , two);
		event = new MidiEvent(m,tick);
		}
		catch(Exception e){}
		return event;
	}
	
	public class startListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			
			makeTrackArrays();
		}
	}
	public class saveActionListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			
			boolean checkStatus[]= new boolean[256];
			for (int i = 0 ; i <256 ; i++){
				
				JCheckBox j = checkBoxList.get(i);
				checkStatus[i] = j.isSelected();
				
				try{
					FileOutputStream file = new FileOutputStream(new File("music.ser"));
					ObjectOutputStream o = new ObjectOutputStream(file);
					o.writeObject(checkStatus);
					
				}
				catch(Exception a){
					a.printStackTrace();
				}
			}
		}
	}
	public class loadActionListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			checkBoxList.clear();
			try{
				FileInputStream file = new FileInputStream(new File("music.ser"));
				ObjectInputStream o = new ObjectInputStream(file);
				boolean[] checkStatus = (boolean[]) o.readObject();
				for (int i = 0 ; i <256; i++){
					JCheckBox jc = new JCheckBox();
					if (checkStatus[i]){
						jc.setSelected(true);
					}
					checkBoxList.add(jc);
					
				}
			}
			catch(Exception a){
				a.printStackTrace();
			}
			
		}
	}
	
 
	public static void main(String[] args) {
		
		MyBeatBox b = new MyBeatBox();
		b.buildGui();
		b.setMidi();
	}

}
