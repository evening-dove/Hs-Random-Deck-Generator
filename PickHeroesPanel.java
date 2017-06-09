
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.*;


public class PickHeoresPanel extends JPanel
{
  //A panel used to keep track of what heros to build decks for, and
	//how many of each deck to make
  
  JPanel pick_heros_panel=new JPanel(new GridLayout(1, 0, 5, 0));
  JPanel hero_amount_panel=new JPanel(new GridLayout(1, 0, 5, 0));
  
  ArrayList all_heroes=new ArrayList();
	
  public PickHeoresPanel()
  {
    super(new BorderLayout());
		
		//Initialize all of the selectable hero buttons.
		for (int heroNameIndex=0; heroNameIndex < main_code.hero_names.length(); heroNameIndex++)
		{
			new HeroSelectable(main_code.hero_names.get(heroNameIndex));
		}
    
		//assemble the UI
    add(new JLabel("Make me a...", SwingConstants.CENTER), BorderLayout.NORTH);
    add(pick_heros_panel, BorderLayout.CENTER);
    add(hero_amount_panel, BorderLayout.SOUTH);
    
  }
  
  public class HeroSelectable
  {
		//An object used to keep track of how many of a certain hero's deck the user wants to make
		
		
    String hero;
    JCheckBox check_box;
    JTextField amount_field=new JTextField("0");
		
    private HeroSelectable(String hero){
      this.hero=hero;
      
      this.check_box=new JCheckBox(hero);
      check_box.addItemListener(new checkHeroListener());
      pick_heros_panel.add(check_box);
      
      amount_field.addKeyListener(new  heroAmountListener());
      amount_field.setEnabled(false);
      
      all_heroes.add(this);
      hero_amount_panel.add(amount_field);
    }
    
    
    private class checkHeroListener implements ItemListener{
      public void itemStateChanged(ItemEvent event){
        
				//If this hero was deselected
        if (event.getStateChange()==ItemEvent.DESELECTED){
          amount_field.setText("0");
          amount_field.setEnabled(false);
        }
				
				//If this hero was selected
				else{
          amount_field.setText("1");
          amount_field.setEnabled(true);
        }
      }
    }
    
    
    private class heroAmountListener implements KeyListener{
			//A listener used to keep track of the text field that the user can use to
			//type in the amount of decks with a certain hero they want.
			
      JTextField source;
      String source_text;
      
      public void keyPressed(KeyEvent event) {} //Nessesary to overwrite for the code to run
      
      public void keyReleased(KeyEvent event) {
        source=(JTextField)event.getSource();
        source_text=source.getText();
        
				//if the user didnt type in a number, remove it
        if (source_text.length()!=0 && "0123456789".indexOf(source_text.charAt(source_text.length()-1))==-1){
          source.setText(source_text.substring(0, source_text.length()-1));
          return;
        }
      }
        
      public void keyTyped(KeyEvent event){} //Nessesary to overwrite for the code to run
    }
  }
  
  
  
  public void transferHeroes(){
    /*
		Get information on the selected heroes so it can be given to the
		text file.
    */
    for (int i=0;i<all_heroes.size(); i+=1){
      HeroSelectable temp_hero=(HeroSelectable)all_heroes.get(i);
      if (temp_hero.check_box.isSelected()==true && temp_hero.amount_field.getText()!="0"){
        main_code.output_deck_text.add(temp_hero.hero+" "+temp_hero.amount_field.getText());
      }
    }
  }
  
  
  
}